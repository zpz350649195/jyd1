package com.mes.jyd.viewModel

import android.content.Context
import com.mes.jyd.delegate.NetworkUtil
import com.mes.jyd.delegate.ParaSave
import com.mes.jyd.view.LoginActivity
import com.mes.jyd.view.NavigationActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import org.json.JSONObject

/**
 *
 * Created by pandanxin on 2017/12/9.
 */
class LoginViewModel(val viewActivity: LoginActivity, val ctx:Context){

    fun login(barcode:String){
        if(!NetworkUtil().isWifiConnected(ctx)){
            viewActivity.toast("当前没有连接到 WIFI 网络")
            return
        }
        viewActivity.apiService().loginbybarcode(barcode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { viewActivity.showloading() }
                .doAfterTerminate { viewActivity.dismissloading()}//请求完成，设置加载为false
                .subscribe(
                        { t: JSONObject? ->
                            //context.toast(t?.toString()!!)
                            if (t?.getBoolean("success")!!) {
                                val data=t//t.getJSONArray("data").getJSONObject(0)
                                ParaSave.saveUserBarcode(ctx,barcode)
                                ParaSave.saveUserName(ctx,data.getString("un"))
                                ParaSave.saveUserId(ctx,data.getString("ui"))
                                viewActivity.startActivity<NavigationActivity>("name" to data.getString("un"))

                            } else {
                                ctx.toast(t.getString("msg") ?: "error")
                            }
                        }, { t: Throwable? ->
                    ctx.toast(t?.message ?: "error")
                }
                )

    }
}