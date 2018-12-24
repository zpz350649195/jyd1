package com.mes.jyd.viewModel.io

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.Toast
import com.mes.jyd.util.general
import com.mes.jyd.view.io.InStockDetailActivity
import com.mes.jyd.view.product.ProductInspectCheckActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception


/**
 * 生产执行方法集
 */
class InStockDetailViewModel(val vw: InStockDetailActivity, val ctx: Context) {


    var userid = ""
    var list = JSONArray()


    fun getdata(){
        vw.apiService().instockgetdetail(
            1,
            vw.id,
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
                        val data=t.getJSONArray("data")
                         list=data
                        vw.count=t.getInt("count")
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
        vw.toolbar!!.title=vw.titletext+"("+list.length().toString()+")"
    }



    fun submitarr(lgort:String){
        //获取需要入库的数据
        var arr=ArrayList<JSONObject>()
        var json:JSONObject?
        if(list.length()<1){
            Toast.makeText(vw,"没有可提交的数据",Toast.LENGTH_SHORT).show()
            return
        }

        for( i in 0..(list.length()-1)){

            var item=list.getJSONObject(i)
            if(item.getBoolean("ischeck")) {
                json = JSONObject()
                json.put("id", item.getString("id"))
                json.put("num", item.getString("num"))
                arr.add(json)
            }
        }

        if(arr.size<1){
            Toast.makeText(vw,"没有可提交的数据",Toast.LENGTH_SHORT).show()
            return
        }
        //提交数据

       // Toast.makeText(vw,arr.toString(),Toast.LENGTH_SHORT).show()
        vw.apiService().instock(
            vw.userid,
            lgort,
            arr.toString()
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { vw.showloading() }
            .doAfterTerminate { vw.dismissloading()}//请求完成，设置加载为false
            .subscribe(
                { t: JSONObject? ->
                    //context.toast(t?.toString()!!)
                    if (t?.getBoolean("success")!!) {

                        vw.showTextToast("入库成功")
                        vw.dialoglgort!!.cancel()
                        getdata()

                    } else {
                        vw.showTextToast(t.getString("msg") ?: "error")
                    }
                }, { t: Throwable? ->
                    vw.showTextToast(t?.message ?: "error")
                }
            )
    }



}