package com.mes.jyd.service

import android.content.Intent
import java.util.*
import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.graphics.BitmapFactory
import android.os.*
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.mes.jyd.R
import com.mes.jyd.api.ApiService
import com.mes.jyd.delegate.NetworkUtil
import com.mes.jyd.delegate.ParaSave
import com.tsp.wmshand.delegate.retrofit.RetrofitManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.toast
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.net.SocketTimeoutException
import java.text.SimpleDateFormat


/**
 *
 * Created by pandanxin on 6/2/18.
 */
class DistributionOrderService: Service() {
    private lateinit var handler:Handler
    private lateinit var timer: Timer
    lateinit var apiService: ApiService

    private var  mNotificationManager: NotificationManager?=null
    private var  mBuilder: NotificationCompat.Builder?=null
    private var  mBuilder1: NotificationCompat.Builder?=null
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        handler = MyHandler()



        if (mNotificationManager == null) {
            mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }
        mBuilder = NotificationCompat.Builder(this, Companion.CHANNEL)
        mBuilder1 = NotificationCompat.Builder(this, Companion.CHANNEL)

        //创建个定时器
        timer = Timer()
        //设置标示，及时间间隔 5分钟
//        1800000
        timer.schedule(MyTimer(), 1, 1800000)//300000
    }

//显示Dialog
    fun showDialog(messageContent: String) {
        val obj=JSONArray(messageContent)

        var m=""
    (0 until obj.length())
            .asSequence()
            .map { obj.getJSONObject(it).getString("ptype") }
            .map { it.split(",") }
            .forEach { plist ->
                (0 until plist.size)
                        .asSequence()
                        .map { plist[it]+"；" }
                        .filterNot { m.contains(it) }
                        .forEach { m+= it }
            }
       // val intent = Intent(this, DistributionDashboardActivity::class.java).putExtra("type", 0).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//        val pIntent = PendingIntent.getActivity(applicationContext,0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
//
//        mBuilder!!.setContentIntent(pIntent)
//                .setContentTitle("共有 ${obj.length()} 待配送任务未领取，涉及物料类型：")
//                .setContentText(m)
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setLargeIcon(BitmapFactory.decodeResource(this.resources, R.mipmap.ic_launcher))
//                .setProgress(0,0,false)
//                .setDefaults(Notification.DEFAULT_ALL)
//                .setTicker("1")
//                .setWhen(System.currentTimeMillis())
//                .setFullScreenIntent(pIntent, true)
//                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
//                .setVisibility(Notification.VISIBILITY_PUBLIC)
//        val notification = mBuilder!!.build()
//        notification.flags = Notification.FLAG_AUTO_CANCEL
//
//        mNotificationManager!!.notify(Companion.NOTIFY_ID,notification)
//
//        Thread(Runnable {
//            try {
//                Thread.sleep(5000)//五秒后悬挂式通知消失
//                mNotificationManager!!.cancel(Companion.NOTIFY_ID)//按tag id 来清除消息
//                mBuilder1!!.setContentIntent(pIntent)
//                        .setContentTitle("共有 ${obj.length()} 待配送任务未领取，涉及物料类型：")
//                        .setContentText(m)
//                        .setSmallIcon(R.mipmap.ic_launcher)
//                        .setLargeIcon(BitmapFactory.decodeResource(this.resources, R.mipmap.ic_launcher))
//                        .setProgress(0,0,false)
//                        .setWhen(System.currentTimeMillis())
//                val mNotification = mBuilder1!!.build()
//                mNotification.flags = Notification.FLAG_AUTO_CANCEL
//                mNotificationManager!!.notify(Companion.NOTIFY_ID,mNotification)
//            } catch (e: InterruptedException) {
//                e.printStackTrace()
//            }finally {
//
//            }
//        }).start()
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
    }

    internal inner class MyHandler : Handler() {
        @SuppressLint("HandlerLeak")
        override fun handleMessage(message: Message) {
            val messageContent = message.data.get("MessageContent").toString()
            showDialog(messageContent)
        }
    }



    //设置定时器任务
    internal inner class MyTimer : TimerTask() {
        override fun run() {

            val lastTime= ParaSave.getDistributionOrderCheckTime(this@DistributionOrderService)

            try {
                 getDistributionOrder(lastTime)
            } catch (e: IOException) {
                e.printStackTrace()
            }


        }
    }

    /*//取到json的特定信息
    private String showMessageInfo(String serverMessage){
        String info = ""
        try {
            JSONObject jsonObject = new JSONObject(serverMessage)
            if( jsonObject != null ){
                info = jsonObject.getString("MessageContent")
            }
        } catch (JSONException e) {
            e.printStackTrace() 
        }
        return  info
    }*/

    //请求网络资源，即取得欲推送消息内容
    @Throws(IOException::class)
    fun getDistributionOrder(time:Long) {
            val timeString="2018-01-01 00:00:00"
            if(!NetworkUtil().isWifiConnected(this@DistributionOrderService)){
                return
            }/*
        RetrofitManager(this@DistributionOrderService).
                retrofit.create(ApiService::class.java)!!.getWillSendByTime(timeString)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            { t: JSONObject? ->
                                if (t?.getBoolean("success")!!) {
                                    Log.i("DistributionOrderCheck",SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(time))
//                                    ParaSave.saveDistributionOrderCheckTime(this@DistributionOrderService,System.currentTimeMillis())
                                    if (t.getInt("count") != 0){
                                        val data = t.getJSONArray("data")
                                        Log.i("TAG", "MessageContent is " + data.toString())
                                        //判断从服务端取到的消息是否为空，不为空则弹出消息
                                            val message = Message()
                                            message.what = 1
                                            val bundle = Bundle()
                                            bundle.putString("MessageContent", data.toString())
                                            message.data = bundle
                                            handler.sendMessage(message)
                                    }
                                } else {
                                    toast(t.getString("msg") ?: "error")
                                }
                            }, { t: Throwable? ->
                        if(t is SocketTimeoutException){
                            toast("网络超时，获取待配送工单失败，请检查网络")
                        }else{
                            toast(t?.message ?: "error")
                        }
                    }
                    )*/

    }

    companion object {
        val NOTIFY_ID = 8888
        val CHANNEL = "DISTRIBUTIONORDER"
    }
}