package com.mes.jyd.base

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat
import android.view.KeyEvent
import android.widget.Toast
import com.mes.jyd.api.InfScan
import com.mes.jyd.delegate.AndroidUtil
import com.mes.jyd.delegate.ParaSave
import com.mes.jyd.scan.HHSScan
import com.mes.jyd.scan.JYDScan
import com.mes.jyd.scan.PBScan
import com.mes.jyd.view.ScannerActivity
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.Permission
import com.yanzhenjie.permission.PermissionListener
import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.toast

abstract class scanActivity: BaseActivity() {

    abstract  fun showResult(barcode: String)

    lateinit var _mode:String

    open fun ifscan():Boolean{
        return  true

    }

   /* lateinit var _scanhhs:HHSScan*/
    lateinit var _scan:InfScan
    lateinit var context:Context
    var _type:Int=1 //火花塞扫描接口
    fun ScanUtil(context: Context){
        this.context=context
        _mode=ParaSave.getIns(context)
        if(_mode=="0")
            _scan=HHSScan()
        else if(_mode=="1")
            _scan=JYDScan()
        else if(_mode=="2"){//平板手机
            _scan=PBScan()
        }

        _scan.ScanUtil(context,this)
    }

    fun scan() {
        _scan.scan()
    }

    fun close(){
        _scan.close()
    }

    fun open(){
        _scan.open()
    }


    companion object {
        val REQUEST_BARCODE = 666
    }

    fun launchScannerActivity() {
        //如果没有扫码服务
        if (!AndroidUtil(this).isAppInstalled("com.pda.hwscan")) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                AndPermission.with(this@scanActivity)
                    .requestCode(102)
                    .permission(Permission.CAMERA)
                    .callback(object : PermissionListener {
                        override fun onSucceed(requestCode: Int, grantPermissions: MutableList<String>) {
                            startActivityForResult<ScannerActivity>(REQUEST_BARCODE)
                        }

                        override fun onFailed(requestCode: Int, deniedPermissions: MutableList<String>) {
                            toast("请授予应用相机权限，才能扫描条码")
                        }
                    })
                    .start()
            } else {
                startActivityForResult<ScannerActivity>(REQUEST_BARCODE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_BARCODE -> if (resultCode == Activity.RESULT_OK) {
                showResult(data!!.getStringExtra("barcode"))
            }
        }
    }

    private var exitTime: Long = 0
    override  fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
       //
        if(keyCode==132){
            scan()
            return true
        }

        if (keyCode == KeyEvent.KEYCODE_BACK && event!!.action == KeyEvent.ACTION_DOWN) {
            if (System.currentTimeMillis() - exitTime > 2000) {
                Toast.makeText(applicationContext, "再按一次关闭此页",
                    Toast.LENGTH_SHORT).show()
                exitTime = System.currentTimeMillis()
            } else {
                finish()
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

}