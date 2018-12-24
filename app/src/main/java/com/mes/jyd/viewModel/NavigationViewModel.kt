package com.mes.jyd.viewModel

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.graphics.BitmapFactory
import android.support.v4.content.ContextCompat.startActivity
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.mes.jyd.R
import com.mes.jyd.delegate.AndroidUtil
import com.mes.jyd.delegate.NetworkUtil
import com.mes.jyd.delegate.ParaSave
import com.mes.jyd.util.logsaves
import com.mes.jyd.view.LoginActivity
import com.mes.jyd.view.NavigationActivity
import com.mes.jyd.view.product.ProductActivity
import com.mes.jyd.view.WorkThreadActivity
import com.mes.jyd.view.io.InStockActivity
import com.mes.jyd.view.product.ProductInspectActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import org.jetbrains.anko.toast
import org.json.JSONObject


/**
 * ViewModel 类-Navigation
 * Created by pandanxin on 2017/11/27.
 */
class NavigationViewModel(val viewActivity: NavigationActivity, val ctx: Context) {


    var userid = ""


    private val mapNav = mapOf(
            "02" to mapOf("item_id" to "02",
                    "item_name" to "生产执行",
                    "item_imageid" to R.drawable.ic_work_24dp
            ),
            "03" to mapOf("item_id" to "03",
                    "item_name" to "巡检",
                    "item_imageid" to R.drawable.ic_work_24dp
            ),
            "04" to mapOf("item_id" to "04",
                "item_name" to "完工入库",
                "item_imageid" to R.drawable.ic_work_24dp
                )
    )
    var list = mutableListOf<Map<String, Any>>()



    fun go(ctx: Context, id: String) {
        val intent: Intent = when (id) {
            "02" -> Intent(ctx, ProductActivity::class.java).putExtra("type", 0).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            "03" -> Intent(ctx, ProductInspectActivity::class.java).putExtra("type", 0).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            "04" -> Intent(ctx, InStockActivity::class.java).putExtra("type", 0).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            else -> return
        }
        startActivity(ctx, intent, null)
        /*when(id){
            "02" -> Intent(ctx, ProductActivity::class.java).putExtra("type", 0).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            "03" ->Intent(ctx, ProductActivity::class.java).putExtra("type", 0).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            else->return

        }*/
    }

    fun createfile(){

        logsaves().save("1111")
       /* val names=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file: File
        if (Build.VERSION.SDK_INT >= 24) {
   *//*         val f = File(names)
            if (!f.exists()) {
                f.mkdir()
            }*//*
            file = File(names, "test.apk")
        }else {
            val root = Environment.getExternalStorageDirectory().path
            file = File(root, "updateDemo.apk")
        }
        if (file.exists())
            file.delete()
        try {
            file.createNewFile()
           // return file
        } catch ( e: IOException) {
            e.printStackTrace()
        }*/
       // return null

    }

    fun doit(){
        //通知栏信息测试
        var nt=viewActivity.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

      /*  var channel=NotificationChannel("12","what",NotificationManager.IMPORTANCE_DEFAULT)

        nt.createNotificationChannel(channel)*/

        //实例化通知栏构造器
        var mBuilder=NotificationCompat.Builder(viewActivity,"12")
     //   mBuilder.setChannelId("12")
        //设置标题
        mBuilder.setContentTitle("我是标题")
            //设置内容
            .setContentText("我是内容")
            //设置大图标
            .setLargeIcon(BitmapFactory.decodeResource(viewActivity.resources, R.mipmap.ic_launcher))
            //设置小图标
            .setSmallIcon(R.mipmap.ic_launcher_round)
            //设置通知时间
            .setWhen(System.currentTimeMillis())
            //首次进入时显示效果
            .setTicker("我是测试内容")
            //设置通知方式，声音，震动，呼吸灯等效果，这里通知方式为声音
            .setDefaults(Notification.DEFAULT_SOUND)
        //发送通知请求
        nt.notify(1, mBuilder.build())


    }

    fun getUserBarcode() {
        val userBarcode = ParaSave.getUserBarcode(ctx)
        if (userBarcode.isEmpty()) {
            viewActivity.startActivity(ctx.intentFor<LoginActivity>().clearTask().newTask())
        } else {
            viewActivity.name = "${ParaSave.getUserName(ctx)} $userBarcode"
            userid = userBarcode
            getuser()
           // getPermission()
        }
    }

    fun getuser(){
        list.clear()

        var navObj = mapNav.getValue("02")
        var nav = mapOf(
            "item_id" to navObj.getValue("item_id"),
            "item_name" to navObj.getValue("item_name"),
            "item_imageid" to navObj.getValue("item_imageid")
        )
        list.add(nav)

         navObj = mapNav.getValue("03")
         nav = mapOf(
            "item_id" to navObj.getValue("item_id"),
            "item_name" to navObj.getValue("item_name"),
            "item_imageid" to navObj.getValue("item_imageid")
        )
        list.add(nav)

        navObj = mapNav.getValue("04")
        nav = mapOf(
            "item_id" to navObj.getValue("item_id"),
            "item_name" to navObj.getValue("item_name"),
            "item_imageid" to navObj.getValue("item_imageid")
        )
        list.add(nav)

        viewActivity.mAdapter.rebuild()
        viewActivity.refreshLayout.isRefreshing = false
    }

    fun changeAccount() {
        ParaSave.saveUserBarcode(ctx, "")
        viewActivity.startActivity(ctx.intentFor<LoginActivity>().clearTask().newTask())
//                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    fun getPermission() {
        if (!NetworkUtil().isWifiConnected(ctx)) {
            viewActivity.toast("当前没有连接到 WIFI 网络")
            viewActivity.refreshLayout.isRefreshing = false
            return
        }
        viewActivity.apiService().getPermission(userid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    viewActivity.showloading()

                }//开始请求数据，设置加载为true
                .doAfterTerminate { viewActivity.dismissloading() }//请求完成，设置加载为false
                .subscribe(
                        { t: JSONObject? ->
                            //ctx.toast(t?.toString()!!)
                            if (t?.getBoolean("success")!!) {
                                list.clear()
                                val dataArray = t.getJSONArray("data")
                                if (dataArray.length() > 0) {
                                    val numList = mutableListOf<String>()
                                    for (i in 0 until dataArray.length()) {
                                        val obj = dataArray.getJSONObject(i)
                                        if (obj.getInt("isleaf") == 1) {//获取叶子节点
                                            val num = obj.getString("number")
                                            if (mapNav.containsKey(num) && !numList.contains(num)) {//判断是否包含匹配号
                                                numList.add(num)
                                                val name = obj.getString("name")
                                                val navObj = mapNav.getValue(num)
                                                val nav = mapOf(
                                                        "item_id" to navObj.getValue("item_id"),
                                                        "item_name" to name,
                                                        "item_imageid" to navObj.getValue("item_imageid")
                                                )
                                                list.add(nav)
                                            }
                                        }
                                    }
//                                    (0 until dataArray.length())
//                                            .filter { dataArray.getJSONObject(it).getInt("isleaf")==1 }
//                                            .map { dataArray.getJSONObject(it).getString("number") }
//                                            .mapNotNull { mapNav[it] }
//                                            .forEach {
//
//                                                list.add(it)
//                                            }

                                }
                                viewActivity.mAdapter.rebuild()
                                Log.i("Nav", dataArray.toString())
                            } else {
                                ctx.toast(t.getString("msg") ?: "error")
                            }
                            viewActivity.refreshLayout.isRefreshing = false
                        }, { t: Throwable? ->
                    ctx.toast(t?.message ?: "error")

                    viewActivity.refreshLayout.isRefreshing = false
                }
                )
    }

    fun getNewVersion() { //0:如果有新版本直接更新，
        if (!NetworkUtil().isWifiConnected(ctx)) {
            viewActivity.toast("当前没有连接到 WIFI 网络")
            viewActivity.refreshLayout.isRefreshing = false
            return
        }
        viewActivity.apiService().getAppNewVersion()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
//                .doOnSubscribe {
//                    viewActivity.showloading()
//
//                }//开始请求数据，设置加载为true
//                .doAfterTerminate { viewActivity.dismissloading()}//请求完成，设置加载为false
                .subscribe(
                        { t: JSONObject? ->
                            //ctx.toast(t?.toString()!!)
                            if (t?.getBoolean("success")!!) {
                                val obj = t.getJSONArray("data").getJSONObject(0)
                                val newVersion = obj.getString("version")
                                val nvl = newVersion.split(".")

                                val version = AndroidUtil.getAppVersionName(ctx)
                                val vl = version.split(".")


                                val desc = obj.getString("vdesc")
                                if (nvl[0] > vl[0]) {
                                    viewActivity.showUpdate(newVersion, desc)
                                    return@subscribe
                                }
                                if ((nvl[1] > vl[1])&&(nvl[0] == vl[0])) {
                                    viewActivity.showUpdate(newVersion, desc)
                                    return@subscribe
                                }
                                if (nvl[2] > vl[2]&&(nvl[0] == vl[0])&&(nvl[1] == vl[1])) {
                                    viewActivity.showUpdate(newVersion, desc)
                                    return@subscribe
                                }

                            } else {
                                ctx.toast(t.getString("msg") ?: "error")
                            }
                        }, { t: Throwable? ->
                    ctx.toast(t?.message ?: "error")
                }
                )
    }
}