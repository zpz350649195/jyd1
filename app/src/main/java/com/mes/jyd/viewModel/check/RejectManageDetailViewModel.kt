package com.mes.jyd.viewModel.check

import android.R
import android.content.Context
import android.view.View
import android.widget.ArrayAdapter
import com.mes.jyd.adapter.SpinnerAdapter
import com.mes.jyd.util.general
import com.mes.jyd.view.check.ProductInspectCheckActivity
import com.mes.jyd.view.check.RejectManageDetailActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception


/**
 * 生产执行方法集
 */
class RejectManageDetailViewModel(val vw: RejectManageDetailActivity, val ctx: Context) {


    var userid = ""
    var list = JSONArray()


    fun getdata(){
        vw.apiService().getinspectcheckitem(
            vw.id
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


    fun rejectmanage(desc:String){
        vw.apiService().rejectmanage(
            vw.userid,
            vw.id,
            vw.chooseIndex,
            desc
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { vw.showloading() }
            .doAfterTerminate { vw.dismissloading()}//请求完成，设置加载为false
            .subscribe(
                { t: JSONObject? ->
                    //context.toast(t?.toString()!!)
                    if (t?.getBoolean("success")!!) {
                        //页面跳转 弹出框关闭
                        vw.showTextToast( "操作成功")
                        vw.finish()

                    } else {
                        vw.showTextToast(t.getString("msg") ?: "error")
                        /* if(vw.state==1){
                             vw.txthint.setText(t.getString("msg") ?: "error")
                         }*/
                    }
                }, { t: Throwable? ->
                    vw.showTextToast(t?.message ?: "error")
                    /* if(vw.state==1){
                         vw.txthint.setText(t?.message ?: "error")
                     }*/
                }
            )
    }



    fun settext(){
        //更新后相关文字设置
      //  vw.toolbar!!.title=vw.titletext+"("+vw.countcheck.toString()+"/"+vw.count.toString()+")"
        vw.toolbar!!.title=vw.titletext+"("+vw.count.toString()+")"
    }


    //获取提交时下拉选择数据
    fun getarr(){
        vw.srString=ArrayList()
        vw.srString.add("合格")
        vw.srString.add("让步接收")
        vw.srString.add("返工")
        vw.srString.add("报废")
        vw.srArr= JSONArray()
      //  vw.srAdapter= ArrayAdapter<String>(vw, R.layout.simple_spinner_dropdown_item, vw.srString) //simple_spinner_dropdown_item
        vw.srAdapter= SpinnerAdapter(vw,vw.srString)
    }


}