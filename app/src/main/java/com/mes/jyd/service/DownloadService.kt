package com.mes.jyd.service

/**
 *
 * Created by pandanxin on 31/1/18.
 */

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.BitmapFactory
import android.os.Binder
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.support.annotation.Nullable
import android.support.v4.app.NotificationCompat
import android.text.TextUtils
import com.mes.jyd.R
import com.mes.jyd.delegate.AndroidUtil
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.Locale
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

/**
 * Created by snail
 * on 2017/12/7.
 */
class DownloadService : Service() {

    //定义notify的id，避免与其它的notification的处理冲突
    val NOTIFY_ID = 0
    private val CHANNEL = "update"

    private val  binder = DownloadBinder()
    var  mNotificationManager:NotificationManager?=null
    var  mBuilder:NotificationCompat.Builder?=null
    lateinit var  callback: DownloadCallback

    //定义个更新速率，避免更新通知栏过于频繁导致卡顿
    var rate = .0f

    @Nullable
    override fun  onBind(intent:Intent):IBinder {
        return binder
    }

    override fun  unbindService(conn:ServiceConnection) {
        super.unbindService(conn)
        mNotificationManager!!.cancelAll()
        mNotificationManager = null
        mBuilder = null
    }

    /**
     * 和activity通讯的binder
     */
    class DownloadBinder : Binder(){
        fun  getService(): DownloadService {
            return DownloadService()
        }
    }

    /**
     * 创建通知栏
     */
    private fun setNotification() {
        if (mNotificationManager == null) {
            mNotificationManager = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            /*val channel1=NotificationChannel(CHANNEL,"what",NotificationManager.IMPORTANCE_DEFAULT)
            mNotificationManager!!.createNotificationChannel(channel1)*/
        }

        mBuilder = NotificationCompat.Builder(ctx,CHANNEL)
       // mBuilder!!.setChannelId(CHANNEL)
        mBuilder!!.setContentTitle("开始下载")
                .setContentText("正在连接服务器")
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setLargeIcon(BitmapFactory.decodeResource(ctx.resources, R.mipmap.ic_launcher))
                .setOngoing(true)
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
        mNotificationManager!!.notify(NOTIFY_ID, mBuilder!!.build())
    }

    /**
     * 下载完成
     */
    private fun  complete( msg:String) {
        if (mBuilder != null) {
            mBuilder!!.setContentTitle("新版本").setContentText(msg)
            val notification = mBuilder!!.build()
            notification.flags = Notification.FLAG_AUTO_CANCEL
            mNotificationManager!!.notify(NOTIFY_ID, notification)
        }
        stopSelf()
    }

    /**
     * 开始下载apk
     */
    lateinit var ctx:Context
    lateinit var activity:Activity
    fun downApk(act:Activity,ct:Context, url:String, callback: DownloadCallback) {
        ctx=ct
        activity=act
        this.callback = callback
        if (TextUtils.isEmpty(url)) {
            complete("下载路径错误")
            return
        }
        setNotification()
        handler.sendEmptyMessage(0)
        val request = Request.Builder().url(url).build()


//     var response=  OkHttpClient().newCall(request).execute()


        OkHttpClient().newCall(request).enqueue(object :Callback {

            override fun  onFailure(call:Call, e:IOException) {
                val message = Message.obtain()
                message.what = 1
                message.obj = e.message
                handler.sendMessage(message)
            }


            override fun onResponse(call:Call, response:Response)  {
                if (response.body() == null) {
                    val message = Message.obtain()
                    message.what = 1
                    message.obj = "下载错误"
                    handler.sendMessage(message)
                    return
                }
                var IS: InputStream? = null
                val buff = ByteArray(2048)
                 var len:Int
                var fos:FileOutputStream? = null
                try {
                    IS = response.body()!!.byteStream()
                    val total = response.body()!!.contentLength()
                    val file = createFile()
                    fos =  FileOutputStream(file)
                    var sum = 0
                    len = IS.read(buff)
                    while (len != -1) {
                        fos.write(buff,0,len)
                        sum+=len
                        var progress =  (sum * 1.0f / total * 100).toInt()
                        if (rate != progress.toFloat()) {
                            val message = Message.obtain()
                            message.what = 2
                            message.obj = progress
                            handler.sendMessage(message)
                            rate = progress.toFloat()
                        }
                        len = IS.read(buff)
                    }
                    fos.flush()
                    if(fos!=null)
                         fos.close()
                    val message = Message.obtain()
                    message.what = 3
                    message.obj = file!!.absoluteFile
                    handler.sendMessage(message)
                } catch ( e:Exception) {
                    e.printStackTrace()
                } finally {
                    try {
                        if (IS != null)
                            IS.close()
                        if (fos != null)
                            fos.close()
                    } catch ( e:Exception) {
                        e.printStackTrace()
                    }
                }
            }
        })
    }


    /**
     * 路径为根目录
     * 创建文件名称为 updateDemo.apk
     */
    fun  createFile(): File? {
        val file:File
        if (Build.VERSION.SDK_INT >= 24) {
            val f = File(activity.filesDir, "test1")
            if (!f.exists()) {
                f.mkdir()
            }
             file = File(activity.filesDir, "test1/test.apk")
        }else {
            val root = Environment.getExternalStorageDirectory().path
            file = File(root, "updateDemo.apk")
        }
        /*val names=  Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        file = File(names, "test.apk")*/

       // return file
        if (file.exists())
            file.delete()
        try {
            file.createNewFile()
            return file
        } catch ( e:IOException) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * 把处理结果放回ui线程
     */
    val  handler =@SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                0 ->{callback.onPrepare()}
                1 -> {
                    mNotificationManager!!.cancel(NOTIFY_ID)
                    callback.onFail(msg.obj.toString())
                    stopSelf()
                }
                2 -> {
                    val progress = msg.obj.toString().toInt()
                    callback.onProgress(progress)
                    mBuilder!!.setContentTitle("正在下载：新版本...")
                            .setContentText(String.format(Locale.CHINESE, "%d%%", progress))
                            .setProgress(100, progress, false)
                            .setWhen(System.currentTimeMillis())
                    val notification = mBuilder!!.build ()
                    notification.flags = Notification.FLAG_AUTO_CANCEL
                    mNotificationManager!!.notify(NOTIFY_ID, notification)
                }
                3->{
                    callback.onComplete(msg.obj as File)
                    //app运行在界面,直接安装
                    //否则运行在后台则通知形式告知完成
                    if (onFront()) {
                        mNotificationManager!!.cancel(NOTIFY_ID)
                    } else {
//                        AndroidUtil(activity).installApk(ctx,msg.obj.toString())

                        val androidUtil = AndroidUtil(activity)

                        try {
                                val intent = androidUtil.installIntent(ctx,msg.obj.toString())
                                val pIntent = PendingIntent.getActivity(applicationContext,0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                                mBuilder!!.setContentIntent(pIntent)
                                        .setContentTitle(packageName)
                                        .setContentText("下载完成，点击安装")
                                        .setProgress(0,0,false)
                                        .setDefaults(Notification.DEFAULT_ALL)
                                val notification = mBuilder!!.build()
                                notification.flags = Notification.FLAG_AUTO_CANCEL
                                mNotificationManager!!.notify(NOTIFY_ID,notification)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                    stopSelf()
                 }

            }
        }
    }


    /**
     * 是否运行在用户前面
     */
    fun  onFront():Boolean {
        val activityManager = ctx.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val appProcesses = activityManager.runningAppProcesses
        if (appProcesses==null||appProcesses.isEmpty())
            return false
        return appProcesses.any { it.processName == ctx.packageName && it.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND }
    }


    /**
     * 安装
     * 7.0 以上记得配置 fileProvider
     */



    /**
     * 销毁时清空一下对notify对象的持有
     */
    override fun onDestroy() {
        mNotificationManager = null
        super.onDestroy()
    }


    /**
     * 定义一下回调方法
     */
    interface DownloadCallback{
        fun onPrepare()
        fun onProgress(progress:Int )
        fun onComplete( file:File)
        fun onFail( msg:String)
    }
}