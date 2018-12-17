package com.mes.jyd.viewModel

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Environment
import android.support.v4.content.ContextCompat.startActivity
import android.support.v4.app.NotificationCompat
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.mes.jyd.R
import com.mes.jyd.delegate.*
import com.mes.jyd.service.DownloadService
import com.mes.jyd.util.general
import com.mes.jyd.view.LoginActivity
import com.mes.jyd.view.NavigationActivity
import com.mes.jyd.view.ProductActivity
import com.mes.jyd.view.ProductCheckActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.*
import org.jetbrains.anko.design.textInputEditText
import org.jetbrains.anko.design.textInputLayout
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.lang.Exception


/**
 * 生产执行方法集
 */
class ProductCheckViewModel(val vw: ProductCheckActivity, val ctx: Context) {


    var userid = ""
    var list = JSONArray()


    fun getdata(){
        vw.apiService().getcheckitem(
            vw.userid,
            vw.pnid,
            0
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { vw.showloading() }
            .doAfterTerminate { vw.dismissloading()}//请求完成，设置加载为false
            .subscribe(
                { t: JSONObject? ->
                    //context.toast(t?.toString()!!)
                    if (t?.getBoolean("success")!!) {
                        val data=t.getJSONArray("data")
                         list=data
                        vw.count=t.getInt("count")
                        vw.countcheck=t.getInt("countcheck")
                        settext()
                        vw.ifchange=true
                        vw.listAdapter.rebuild()
                        vw.refreshLayout.isRefreshing=false


                    } else {
                        vw.showTextToast(t.getString("msg") ?: "error")
                    }
                }, { t: Throwable? ->
                    vw.showTextToast(t?.message ?: "error")
                }
            )

    }



    fun settext(){
        //更新后相关文字设置
        vw.toolbar!!.title=vw.titletext+"("+vw.countcheck.toString()+"/"+vw.count.toString()+")"
    }

    fun changeValue(item:JSONObject){
        var _value=general.getString(item,"relvalue")
        if(!item.getBoolean("isvalue")){//用checkbox
            _value=general.getString(item,"checkresult")
            vw._textInputLayout.visibility=View.GONE
            vw.checkValue.visibility=View.VISIBLE

            vw.checkValue.setChecked(false)


            if(_value=="1"){
                //vw.checkValue.isChecked=true
                vw.checkValue.setChecked(true)
            }
        }else{

            vw._textInputLayout.visibility=View.VISIBLE
            vw.checkValue.visibility=View.GONE
            if(_value==""){
                vw.txtValue.text.clear()
            }else
               vw.txtValue.setText(_value)
        }
    }

    //保存检测结果
    fun savecheck(){
        if(vw.position>-1) {
            if(list.length()>vw.position){
                var json: JSONObject = list.getJSONObject(vw.position)
                var value=""
                var checkvalue=0
                if(!json.getBoolean("isvalue")){//checkbox
                    if(vw.checkValue.isChecked)
                        checkvalue=1
                    else
                        checkvalue=0

                    value="0"
                }else{//填写的num
                    value=vw.txtValue.text.toString()
                    if(value==""){
                        vw.showTextToast("必须填写检测结果")
                        return
                    }
                }

                val id=json.getInt("id")
                //提交数据到服务器
                vw.apiService().setcheckresult(
                    vw.userid,
                    vw.pnid,
                    id,
                    value.toFloat(),
                    checkvalue,
                    ""
                )
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe { vw.showloading() }
                    .doAfterTerminate { vw.dismissloading()}//请求完成，设置加载为false
                    .subscribe(
                        { t: JSONObject? ->
                            //context.toast(t?.toString()!!)
                            if (t?.getBoolean("success")!!) {
                                vw.countcheck=t.getInt("countcheck")
                                settext()
                                checkvalue=t.getInt("result")
                                setvaluetojson(json,value,checkvalue)
                                /*vw.ifchange=true
                                vw.position=-1*/
                                /*vw.listAdapter.linear=null
                                vw.listAdapter.rebuild()
                                vw.refreshLayout.isRefreshing=false*/
                                change()
                                vw.showTextToast("提交成功")
                            } else {
                                vw.showTextToast(t.getString("msg") ?: "error")
                            }
                        }, { t: Throwable? ->
                            vw.showTextToast(t?.message ?: "error")
                        }
                    )

            }else{
                vw.showTextToast("操作失败，请重新加载数据")
            }

        }else{
            vw.showTextToast("必须选择检验项")
        }
    }

    fun setvaluetojson(json:JSONObject,value:String,_checkvalue:Int){
        //将值改变到数组并重新加载
      //  var json=list.getJSONObject(vw.position)
        if(json.getBoolean("isvalue")){
            json.put("relvalue",value)
        }
        json.put("checkresult",_checkvalue.toString())

    }
    //将检验过的放到最后面
    fun change(){
        //检验数据提交
        if (list.length() > 1) {
            if (vw.position >= 0 && list.length() > vw.position) {
                val json = list.getJSONObject(vw.position)
             //   list.put(json)
             //   list.put(1,json)
                if(vw.position!=1&&list.length()>1) {
                    list.remove(vw.position)
                    //获取下一个未检验项目
                    var nocheck=JSONObject()
                    var i1=-1
                    for(i in 0..(list.length()-1)){
                        nocheck=list.getJSONObject(i)
                        if(general.getString(nocheck,"checkresult")==""){
                            list.remove(i)
                            i1=i
                            break
                        }
                    }


                    var _list = list
                    list = JSONArray()

                    if(i1==-1) {
                        list.put(_list.getJSONObject(0))
                        _list.remove(0)
                    }else{
                        list.put(nocheck)
                    }

                    list.put(json)
                    if(_list.length()>0) {
                        for (index in 0..(_list.length() - 1)) {
                            list.put(_list.getJSONObject(index))
                        }
                    }
                }


                vw.listAdapter.linear = null
                vw.position = -1
                vw.ifchange = true
                vw.listAdapter.rebuild()
                vw.refreshLayout.isRefreshing = false


            }
        }
    }


}