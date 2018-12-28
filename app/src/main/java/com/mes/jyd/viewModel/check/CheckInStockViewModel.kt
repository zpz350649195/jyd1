package com.mes.jyd.viewModel.check

import android.content.Context
import android.content.Intent
import com.mes.jyd.adapter.SpinnerAdapter
import com.mes.jyd.delegate.ArithUtil
import com.mes.jyd.view.check.CheckInStockActivity
import com.mes.jyd.view.check.CheckInStockItemActivity
import com.mes.jyd.view.check.ProductInspectActivity
import com.mes.jyd.view.check.ProductInspectCheckActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONArray
import org.json.JSONObject


/**
 * 生产执行方法集
 */
class CheckInStockViewModel(val vw: CheckInStockActivity, val ctx: Context) {


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

        vw.apiService().checkisgetmsg(
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

    fun intentto(id:Int,bc:String){
        var _intent= Intent(vw._ctx, CheckInStockItemActivity::class.java)

        _intent.putExtra("type","0") //调用类型 0 直接点击id调用 1 通过传入对象数组调用
        _intent.putExtra("id",id) //id值
        _intent.putExtra("bc",bc) //条码值

        vw.startActivityForResult(_intent,222)

        /*var bundle= Bundle()
        bundle.putSerializable("list",itemArr)
        _intent.putExtra("bb",bundle)
        startActivity(_intent)*/

    }

    //扫码判断条码是否合理
    fun judgebc(bc: String){
        vw.apiService().checkisgetitem(
            vw.userid,
            0,
            0,
            bc,
            0,
            1
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
                        //页面跳转
                        intentto(0,bc)
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

    //重新设置标题
    fun setText(){
        vw._toolbar.title=vw._title+"("+vw.count.toString()+"/"+vw.counts.toString()+")"
    }

}