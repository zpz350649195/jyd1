package com.mes.jyd.viewModel.io

import android.R
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import com.mes.jyd.adapter.SpinnerAdapter
import com.mes.jyd.delegate.ArithUtil
import com.mes.jyd.util.general
import com.mes.jyd.view.io.InStockActivity
import com.mes.jyd.view.io.InStockDetailActivity
import com.mes.jyd.view.product.ProductCheckActivity
import com.mes.jyd.view.product.ProductInspectActivity
import com.mes.jyd.view.product.ProductInspectCheckActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.alert
import org.jetbrains.anko.custom.customView
import org.json.JSONArray
import org.json.JSONObject


/**
 * 生产执行方法集
 */
class InStockViewModel(val vw: InStockActivity, val ctx: Context) {


    var userid = ""
    var list = JSONArray()


    fun getdata(type:Int){
        var _p=0
        if(type==0) //下拉刷新
        {
            _p=1
        }else{//加载更多
            _p=vw.page+1

        }

        vw.apiService().getinstockitem(
            vw.userid,
            _p
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
                        if(type==0){
                         list=data
                        }else if(type==1)
                        {
                           // list.put
                            list = ArithUtil.joinJSONArray(list, data)!!
                        }
                        vw.count=list.length()
                        vw.counts=t.getInt("counts")
                        setText()
                        if(t.getInt("count")>0) {
                            vw.page = _p
                            vw.listadapter.rebuild()
                            vw.refreshLayout.isRefreshing = false
                            if(type==1) {
                                vw._listview.setSelection(vw.count - 1) //定位到最后
                            }
                        }

                    } else {
                        vw.showTextToast(t.getString("msg") ?: "error")
                    }
                }, { t: Throwable? ->
                    vw.showTextToast(t?.message ?: "error")
                }
            )

    }

    //判断条码是否正确
    fun judgebc(){

        vw.apiService().instockgetdetail(
            0,
            -1,
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
                        intentto(-1)
                    } else {
                        vw.showTextToast(t.getString("msg") ?: "error")
                    }
                }, { t: Throwable? ->
                    vw.showTextToast(t?.message ?: "error")
                }
            )

    }

    fun intentto(id:Int){
        var _intent= Intent(vw._ctx, InStockDetailActivity::class.java)

        _intent.putExtra("type","0") //调用类型 0 直接点击id调用 1 通过传入对象数组调用
        _intent.putExtra("id",id) //mainid值
        _intent.putExtra("bc",vw.bc)
        vw.startActivityForResult(_intent,222)


    }

    //重新设置标题
    fun setText(){
        vw._toolbar.subtitle=vw.count.toString()+"/"+vw.counts.toString()
    }

}