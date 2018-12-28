package com.mes.jyd.viewModel.check

import android.content.Context
import android.content.Intent
import com.mes.jyd.adapter.SpinnerAdapter
import com.mes.jyd.delegate.ArithUtil
import com.mes.jyd.view.check.ProductInspectActivity
import com.mes.jyd.view.check.ProductInspectCheckActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONArray
import org.json.JSONObject


/**
 * 生产执行方法集
 */
class ProductInspectViewModel(val vw: ProductInspectActivity, val ctx: Context) {


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

        vw.apiService().getinspectitem(
            vw.userid,
            4,
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
        var _intent= Intent(vw._ctx, ProductInspectCheckActivity::class.java)

        _intent.putExtra("type","0") //调用类型 0 直接点击id调用 1 通过传入对象数组调用
        _intent.putExtra("id",id) //id值

        vw.startActivityForResult(_intent,222)

        /*var bundle= Bundle()
        bundle.putSerializable("list",itemArr)
        _intent.putExtra("bb",bundle)
        startActivity(_intent)*/

    }
    //根据产品条码获取工序信息
    fun gettech(bc:String){
        vw.apiService().getinspecttech(
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
                        val data=t.getJSONArray("data")
                        vw.srArr=data

                        vw.showTextToast("操作成功")
                        vw.txtProduct.setText(bc)
                        listgetstring()
                    } else {
                        var msg1=t.getString("msg") ?: "error"
                        vw.showTextToast(bc+"-"+msg1)

                    }
                }, { t: Throwable? ->
                    var msg2=t?.message ?: "error"
                    vw.showTextToast(bc+"-"+msg2)

                }
            )
    }

    //将工序数组转化成字符串输出
    fun listgetstring(){
        vw.srString.clear()
        vw.chooseIndex=-1
        var _json:JSONObject

        if(vw.srArr.length()>0) {
            for (i in 0..(vw.srArr.length() - 1)) {
                _json=vw.srArr.getJSONObject(i)
                vw.srString.add(_json.getString("proccode")+"-"+_json.getString("technicsdemand"))
            }
      //     vw.srAdapter= ArrayAdapter<String>(vw.context, R.layout.simple_spinner_dropdown_item, vw.srString)

            vw.srAdapter= SpinnerAdapter(vw.context,vw.srString)

           // vw.srAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            vw.sr.adapter=vw.srAdapter
            vw.chooseIndex=0
        }

    }
    //清空下拉框数据
    fun emptyspinner(){
        vw.srString.clear()
        vw.chooseIndex=-1
        vw.srArr= JSONArray()

        vw.srAdapter= SpinnerAdapter(vw.context,vw.srString)
        vw.sr.adapter=vw.srAdapter
        vw.chooseIndex=-1
    }

    //根据批次和工序id新增巡检项
    fun getadddata(){
        if(vw.chooseIndex<0||vw.srArr.length()<1||vw.chooseIndex>=vw.srArr.length()){
            vw.showTextToast("请先选择工序")
            return
        }
        //获取工序信息
        var _json=vw.srArr.getJSONObject(vw.chooseIndex)

        vw.apiService().inspectadditem(
            vw.userid,
            _json.getInt("batchmainid"),
            _json.getInt("procseq")
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
                        vw.itemAdd.cancel()
                        //页面跳转
                        var mainid=t?.getInt("id")
                        intentto(mainid)

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