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
import com.mes.jyd.view.LoginActivity
import com.mes.jyd.view.NavigationActivity
import com.mes.jyd.view.ProductActivity
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
class ProductViewModel(val vw: ProductActivity, val ctx: Context) {


    var userid = ""
    var list = JSONArray()

    var list2 = JSONArray()


    fun getdata(){
        var jo = JSONObject()
        jo.put("t", "1322165465498641555")//条码
        jo.put("m", "M1345646513")
        jo.put("mn", "M134564M1345646513M1345646513M1345646513M1345646513M1345646513M1345646513M13456465136513")
        jo.put("n", "300")
        jo.put("nover", "105")

        list.put(jo)



        vw.listAdapter.rebuild()
        vw.refreshLayout.isRefreshing=false

        list2=JSONArray()
        var jo1 = JSONObject()
        jo1.put("t", "1322165465498641555")//条码
        jo1.put("m", "M1345646513")
        jo1.put("mn", "M134564M1345646513M1345646513M1345646513M1345646513M1345646513M1345646513M13456465136513")
        jo1.put("n", "300")

        list2.put(jo1)

        vw.listAdapterdetail.rebuild()
        vw.refreshLayoutDetail.isRefreshing=false

    }

    fun changepage(_pageid:Int){
        if(_pageid==0){
            gettaskplan(vw.positionid,1)
            vw.linearLayoutdetail.setVisibility(View.GONE)
            vw.linearLayouttask.setVisibility(View.VISIBLE)
            vw.pageid=_pageid
        }else{
            vw.taskpage=1
            getdetail()
            vw.linearLayoutdetail.setVisibility(View.VISIBLE)
            vw.linearLayouttask.setVisibility(View.GONE)
            vw.pageid=_pageid
        }
    }

    fun changeline(){
        //更改产线
        vw.ifchangeline=true
        vw.dialogline = vw.alert {
                customView {
                    verticalLayout {
                        isFocusable = true
                        isFocusableInTouchMode = true
                        lparams {
                            verticalMargin = dip(8)
                        }
                        relativeLayout{
                            textView {
                                text = "产线工位信息"
                                textSize = 21f
                                textColor = R.color.colorAccent
                                typeface = Typeface.create("Roboto-medium", Typeface.NORMAL)
                                /*onClick {
                                    // startActivity<AntichannelingCodeListActivity>()
                                }*/
                                isEnabled=false
                            }.lparams(width = matchParent) {
                                topMargin = dip(3)
                                horizontalMargin = dip(5)
                            }

                            imageView {
                                imageResource = R.drawable.ic_close_24dp
                                onClick {
                                    vw.dialogline.cancel()
                                }
                            }.lparams {
                                topMargin = dip(3)
                                height = wrapContent
                                width = wrapContent
                                alignParentRight()
                            }
                        }

                       vw._linetext= textView {
                            text = "产线" //tagObj.scanTag(12)
                            textSize = 15f
                            typeface = Typeface.create("Roboto-medium", Typeface.NORMAL)
                            gravity = Gravity.LEFT
                        }.lparams(width = matchParent) {

                           topMargin=dip(12)
                            horizontalGravity = Gravity.CENTER_HORIZONTAL
                        }

                        textInputLayout {
                            vw._linevalue = textInputEditText {
                               // hint = "产线"
                                setText(vw.linename)
                                singleLine = false
                                isEnabled=false
                            }
                        }.lparams(width = matchParent)

                        vw._positiontext= textView {
                            text = "工位" //tagObj.scanTag(12)
                            textSize = 15f
                            typeface = Typeface.create("Roboto-medium", Typeface.NORMAL)
                            gravity = Gravity.LEFT
                        }.lparams(width = matchParent) {
                            topMargin = dip(12)
                            horizontalGravity = Gravity.CENTER_HORIZONTAL
                        }

                        textInputLayout {
                            vw._positionvalue = textInputEditText {
                             //   hint = "工位"
                                setText(vw.positionname)
                                singleLine = false
                                isEnabled=false
                            }
                        }.lparams(width = matchParent)

                        button {
                            text="更换工位"
                            textSize=20f
                            backgroundColor=Color.argb(85,0,0,200)
                            textColor=Color.argb(85,0,0,0)

                            onClick {
                                //调用接口

                            }
                        }.lparams{
                            width= wrapContent
                            padding=dip(15)
                            margin=dip(10)
                            gravity=Gravity.CENTER
                        }
                    }
                }
                onCancelled {
                    vw.ifchangeline=false
                }
            }.show()
    }

    fun initlineposition(){
        vw._linevalue.setText(vw.linename)
        vw._positionvalue.setText(vw.positionname)
    }

    fun productscan(){
        //基础判断 用户id不能为空
        if(vw.userid==-1){
            vw.toast("用户Id不存在，请重新登录")
            return
        }

        var _pnid:Int=vw.positionid

        if(vw.ifchangeline)
                _pnid=0
        /*       var _bc=vw.bc

             if(vw.ifchangeline) {//扫描工位码
                  _bc=""

                  try {
                      _pnid = vw.bc.toInt()
                  } catch (e: Exception) {
                      vw.toast("工位Id必须为整型" + e.message.toString())
                      return
                  }

              }else{
                  if(vw.positionid==0) {//扫描工位码
                      _bc=""
                      try {
                          _pnid = vw.bc.toInt()
                      } catch (e: Exception) {
                          vw.txtmsg.text="请扫描工位码！"
                          vw.toast("工位Id必须为整型" + e.message.toString())
                          return
                      }
                  }
              }*/

        if (!NetworkUtil().isWifiConnected(ctx)) {
            vw.toast("当前没有连接到 WIFI 网络")
            return
        }
        vw.apiService().productscan(
            vw.userid,
           _pnid,
            vw.bc
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { vw.showloading() }
            .doAfterTerminate { vw.dismissloading()}//请求完成，设置加载为false
            .subscribe(
                { t: JSONObject? ->
                    //context.toast(t?.toString()!!)
                    if (t?.getBoolean("success")!!) {
                      /*  val data=t//t.getJSONArray("data").getJSONObject(0)
                        ParaSave.saveUserBarcode(ctx,vw.bc)
                        ParaSave.saveUserName(ctx,data.getString("un"))
                        ParaSave.saveUserId(ctx,data.getString("ui"))
                        vw.startActivity<NavigationActivity>("name" to data.getString("un"))*/


                        var _pageid=t.getInt("page")
                        if(_pageid<0){
                            vw.txtmsg.text="服务器返回的数据不正确，页面编号不能小于0！"
                            return@subscribe
                        }

                        if(_pnid==0){//如果是在选择产线
                            vw.positionid=t.getInt("positionid")
                            vw.positionname=t.getString("positionname")
                            vw.lineid=t.getInt("lineid")
                            vw.linename=t.getString("linename")
                            setTitle()
                        }
                        //判断应该切换到哪个视图
                        /*if(vw.pageid!=_pageid){
                            changepage(_pageid)
                        }*/
                        changepage(_pageid)
                        vw.txtmsg.text=t.getString("msg") ?: "error"

                        if(vw.ifchangeline){
                            vw.dialogline.cancel()
                        }

                    } else {
                       // ctx.toast(t.getString("msg") ?: "error")
                        if(vw.ifchangeline)//如果是在选择工位 则悬浮提示
                            ctx.toast(t.getString("msg") ?: "error")
                        else
                            vw.txtmsg.text=t.getString("msg") ?: "error"
                    }
                }, { t: Throwable? ->
                    vw.txtmsg.text=t?.message ?: "error"
                }
            )

    }


    fun gettaskplan(pnid:Int,_page:Int){
        //获取生产计划 pnid 工位Id _page 第几页
/*        var jo = JSONObject()
        jo.put("t", "1322165465498641555")//条码
        jo.put("m", "M1345646513")
        jo.put("mn", "M134564M1345646513M1345646513M1345646513M1345646513M1345646513M1345646513M13456465136513")
        jo.put("n", "300")
        jo.put("nover", "105")*/

    //    list.put(jo)
        vw.apiService().queryplan(
            vw.userid,
            pnid,
            _page,
            3
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { vw.showloading() }
            .doAfterTerminate { vw.dismissloading()}//请求完成，设置加载为false
            .subscribe(
                { t: JSONObject? ->
                    //context.toast(t?.toString()!!)
                    if (t?.getBoolean("success")!!) {
                        if(t.getString("count")=="0")
                            vw.txtmsg.text="没有更多数据"
                        else
                            vw.txtmsg.text="共"+t.getString("counts")+"；新加载"+t.getString("count")+"条数据"
                         val data=t.getJSONArray("data")
                        if(_page==1){
                            vw.taskpage=1
                            list=data
                        }else{
                            vw.taskpage++
                         //   list.put(data)
                            list = ArithUtil.joinJSONArray(list, data)!!
                        }
                        vw.listAdapter.rebuild()
                        vw.refreshLayout.isRefreshing=false
                    } else {
                        // ctx.toast(t.getString("msg") ?: "error")
                        if(vw.ifchangeline)//如果是在选择工位 则悬浮提示
                            ctx.toast(t.getString("msg") ?: "error")
                        else
                            vw.txtmsg.text=t.getString("msg") ?: "error"
                    }
                }, { t: Throwable? ->
                    vw.txtmsg.text=t?.message ?: "error"
                }
            )
    }

    fun getdetail(){
        //获取生产任务

        if(vw.positionid<=0){
            vw.toast("还未选择工位")
            return
        }

        vw.apiService().getdetail(
            vw.userid,
            vw.positionid
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { vw.showloading() }
            .doAfterTerminate { vw.dismissloading()}//请求完成，设置加载为false
            .subscribe(
                { t: JSONObject? ->
                    //context.toast(t?.toString()!!)
                    if (t?.getBoolean("success")!!) {
                        if(t.getString("count")=="0")
                            vw.txtmsg.text="数据处理失败"
                        else{
                            val data=t.getJSONArray("data")
                            list2=data
                            vw.listAdapterdetail.rebuild()
                            vw.refreshLayoutDetail.isRefreshing=false
                        }

                    } else {
                        // ctx.toast(t.getString("msg") ?: "error")
                        if(vw.ifchangeline)//如果是在选择工位 则悬浮提示
                            ctx.toast(t.getString("msg") ?: "error")
                        else
                            vw.txtmsg.text=t.getString("msg") ?: "error"
                    }
                }, { t: Throwable? ->
                    vw.txtmsg.text=t?.message ?: "error"
                }
            )
      /*  list2=JSONArray()
        var jo1 = JSONObject()
        jo1.put("t", "1322165465498641555")//条码
        jo1.put("m", "M1345646513")
        jo1.put("mn", "M134564M1345646513M1345646513M1345646513M1345646513M1345646513M1345646513M13456465136513")
        jo1.put("n", "300")

        list2.put(jo1)

        vw.listAdapterdetail.rebuild()
        vw.refreshLayoutDetail.isRefreshing=false*/
    }

    fun setTitle(){
        vw._toolbar.subtitle=vw.linename+"-"+vw.positionname

    }
    
}