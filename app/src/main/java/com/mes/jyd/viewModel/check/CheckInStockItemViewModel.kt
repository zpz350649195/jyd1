package com.mes.jyd.viewModel.check

import android.content.Context
import android.content.Intent
import com.mes.jyd.adapter.SpinnerAdapter
import com.mes.jyd.delegate.ArithUtil
import com.mes.jyd.view.check.CheckInStockDetailActivity
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
class CheckInStockItemViewModel(val vw: CheckInStockItemActivity, val ctx: Context) {


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

        vw.apiService().checkisgetitem(
            vw.userid,
            1,
            vw.id,
            vw._bc,
            0,
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

    fun intentto(id:Int){
        var _intent= Intent(vw._ctx, CheckInStockDetailActivity::class.java)

        _intent.putExtra("type","0") //调用类型 0 直接点击id调用 1 通过传入对象数组调用
        _intent.putExtra("id",id) //id值

        vw.startActivityForResult(_intent,222)

        /*var bundle= Bundle()
        bundle.putSerializable("list",itemArr)
        _intent.putExtra("bb",bundle)
        startActivity(_intent)*/

    }

    //根据批次和工序id新增巡检项
    fun getdetail(id:Int,isnew:Int,bc:String){//id 入库检验主表id  isnew 是否新增 1：是

        vw.apiService().checkisgetdetail(
            vw.userid,
            isnew,
            vw.id,
            id,
            bc
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
                        if(vw.state==1) {
                            vw.itemAdd.cancel()
                        }
                        //页面跳转
                        var mainid=t?.getInt("id")
                        intentto(mainid)

                    } else {
                        vw.showTextToast(t.getString("msg") ?: "error")
                        if(vw.state==1){
                            vw.txthint.setText(bc+" "+(t.getString("msg") ?: "error"))
                        }
                    }
                }, { t: Throwable? ->
                    vw.showTextToast(t?.message ?: "error")
                    if(vw.state==1){
                        vw.txthint.setText(bc+" "+t?.message ?: "error")
                    }
                }
            )
    }

    //重新设置标题
    fun setText(){
        vw._toolbar.title=vw._title+"("+vw.count.toString()+"/"+vw.counts.toString()+")"
    }

}