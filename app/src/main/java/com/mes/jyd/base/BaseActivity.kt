package com.mes.jyd.base

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import org.jetbrains.anko.*
import android.widget.Toast
import com.mes.jyd.api.ApiService
import com.mes.jyd.api.IViewSpecification
import com.mes.jyd.delegate.NetworkUtil
import com.mes.jyd.delegate.ParaSave
import com.mes.jyd.delegate.Tag
import com.tsp.wmshand.delegate.retrofit.RetrofitManager


/**
 * 基类 - View层基类
 * Created by pandanxin on 2017/11/27.
 */
abstract class BaseActivity : AppCompatActivity(), IViewSpecification {

    //是否屏幕旋转
    private var isAllowScreenRotate = false
    private lateinit var loadingDialog: DialogInterface
    var tagObj = Tag()

    private var loadingCount=0

    //    val service = RetrofitManager(this).retrofit.create(ApiService::class.java)
    fun showloading() {
        if(loadingCount>0)
            loadingCount+=1
        else{
            loadingDialog = alert {
                customView {
                    verticalLayout {
                        lparams(width = wrapContent)
                        progressBar {
                        }.lparams {
                            margin = dip(24)
                            gravity = Gravity.CENTER_HORIZONTAL
                        }
                    }
                }
                isCancelable = false
            }.show()
            loadingCount=1
        }

    }

    fun dismissloading() {
        if(loadingCount==1) {
            try {
                loadingDialog.dismiss()
            }catch (e:Exception){}
            loadingCount=0
        }else if(loadingCount>1){
            loadingCount-=1
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        if (!isAllowScreenRotate) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }

        if(!ParaSave.getSystemDisplay(this)) {
            Log.d("BaseActivity","不启用系统显示模式")
            resizeDensityDpi()
        }else{
            defaultDisplay()
        }

        val bundle = intent.extras
        initParams(bundle)

        initView()

        doBusiness()

    }


    override fun onResume() {
        super.onResume()
        if(!ParaSave.getSystemDisplay(this)) {
            Log.d("BaseActivity","不启用系统显示模式")
            resizeDensityDpi()
        }else{
            defaultDisplay()
        }

    }

    fun startActivity(targetActivity: Class<*>) {
        startActivity(Intent(this, targetActivity))
    }

    fun startActivity(targetActivity: Class<*>, bundle: Bundle?) {
        startActivity(Intent(this, targetActivity).putExtras(bundle))
    }

    fun startActivityForResult(cls: Class<*>, bundle: Bundle?,
                               requestCode: Int) {
        val intent = Intent()
        intent.setClass(this, cls)
        intent.putExtras(bundle)
        startActivityForResult(intent, requestCode)
    }

    fun addFragmentToActivity(fragmentManager: FragmentManager,
                              fragment: Fragment, frameId: Int) {
        val transaction = fragmentManager.beginTransaction()
        if (!fragment.isAdded)
            transaction.add(frameId, fragment)
        fragmentManager.fragments.filter { it.id == fragment.id }.map { transaction.hide(it) }
        transaction.show(fragment)
        transaction.commit()
    }

    fun apiService(): ApiService {
        return RetrofitManager(this).retrofit.create(ApiService::class.java)
    }

    @Suppress("DEPRECATION")
    private fun resizeDensityDpi() {

        val cfg = resources.configuration
        displayMetrics.densityDpi = 320
        displayMetrics.xdpi = 294.967f
        displayMetrics.ydpi = 295.563f
        displayMetrics.density = 2f
        displayMetrics.scaledDensity = 2f
        cfg.densityDpi = 320

        resources.updateConfiguration(cfg, displayMetrics)
        //windowManager.defaultDisplay.getMetrics(displayMetrics)
//        val densityDpi1 = displayMetrics.densityDpi
//        val density = displayMetrics.density
//        val TAG = "kljl"
//        Log.d(TAG, "densityDpi1 " + densityDpi1)
//        Log.d(TAG, "density " + density)
//        Log.d(TAG, "heightPixels " + displayMetrics.heightPixels)
//        Log.d(TAG, "scaledDensity " + displayMetrics.scaledDensity)
//        Log.d(TAG, "widthPixels " + displayMetrics.widthPixels)
//        Log.d(TAG, "xdpi " + displayMetrics.xdpi)
//        Log.d(TAG, "ydpi " + displayMetrics.ydpi)
    }

    @Suppress("DEPRECATION")
    private fun defaultDisplay(){
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val cfg = resources.configuration
        cfg.densityDpi=displayMetrics.densityDpi
        resources.updateConfiguration(cfg, displayMetrics)
    }

    fun checkWifi(){
        if(!NetworkUtil().isWifiConnected(this@BaseActivity)){
            toast("当前没有连接到 WIFI 网络")

        }
    }

    private var toast: Toast? = null

    fun showTextToast(msg: String) {
        if (toast == null) {
            toast = Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT)
        } else {
            toast!!.setText(msg)
        }
        toast!!.show()
    }

    //软键盘禁用
    open fun HideKeyboard(view:Activity) {
        /* Timer().schedule(object:TimerTask(){
            override fun run() {
                var manager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                manager.hideSoftInputFromWindow(
                    vw.currentFocus.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS
                )
            }
        }, 1000)*/
        if(!ParaSave.getDisplayKeyboard(view)) {
            var manager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            manager.hideSoftInputFromWindow(
                view.currentFocus.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }

    }


}