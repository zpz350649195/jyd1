package com.mes.jyd.viewModel.check

import android.R
import android.content.Context
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import com.mes.jyd.adapter.SpinnerAdapter
import com.mes.jyd.util.general
import com.mes.jyd.view.check.CheckPreviousDetailActivity
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
class CheckPreviousDetailViewModel(val vw: CheckPreviousDetailActivity, val ctx: Context) {


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


    fun settext(){
        //更新后相关文字设置
      //  vw.toolbar!!.title=vw.titletext+"("+vw.countcheck.toString()+"/"+vw.count.toString()+")"
        vw.toolbar!!.title=vw.titletext+"("+vw.count.toString()+")"
    }




    fun submitarr(){
        //获取需要入库的数据
        var arr=ArrayList<JSONObject>()
        var json:JSONObject?
        if(list.length()<1){
            Toast.makeText(vw,"没有可提交的数据", Toast.LENGTH_SHORT).show()
            return
        }

        for( i in 0..(list.length()-1)){

            var item=list.getJSONObject(i)

            json = JSONObject()
            json.put("id", item.getString("id"))
            json.put("value", item.getInt("checkresult"))
            arr.add(json)
        }

        if(arr.size<1){
            Toast.makeText(vw,"没有可提交的数据", Toast.LENGTH_SHORT).show()
            return
        }
        //提交数据

        // Toast.makeText(vw,arr.toString(),Toast.LENGTH_SHORT).show()
        vw.apiService().checkprevious(
            vw.userid,
            0,
            "",
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
                        var s=t?.getInt("state")
                        vw.showTextToast("操作成功")
                        if(s==1){
                            //询问是否排查
                            vw.alerrtDialog1()
                        }else{
                            vw.finish()
                        }

                    } else {
                        vw.showTextToast(t.getString("msg") ?: "error")
                    }
                }, { t: Throwable? ->
                    vw.showTextToast(t?.message ?: "error")
                }
            )
    }

    //质量排查结果提交
    fun checks(ifcheck:Int){
        if(ifcheck==1){
            try {
                val json = list.getJSONObject(0)
                val id=json.getInt("id")
                vw.apiService().inspectrecheck(
                    id
                )
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe { vw.showloading() }
                    .doAfterTerminate { vw.dismissloading()}//请求完成，设置加载为false
                    .subscribe(
                        { t: JSONObject? ->
                            //context.toast(t?.toString()!!)
                            if (t?.getBoolean("success")!!) {
                                vw.showTextToast("操作成功")
                                vw.finish()
                                vw.dialog1!!.cancel()
                            } else {
                                vw.showTextToast(t.getString("msg") ?: "error")
                            }
                        }, { t: Throwable? ->
                            vw.showTextToast(t?.message ?: "error")
                        }
                    )


            }catch (e:Exception){

            }
        }else{
            vw.dialog1!!.cancel()
        }


    }

}