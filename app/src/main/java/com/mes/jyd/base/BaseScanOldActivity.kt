package com.mes.jyd.base

import android.Manifest
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat
import android.view.KeyEvent
import com.mes.jyd.delegate.AndroidUtil
import com.mes.jyd.view.ScannerActivity
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.Permission
import com.yanzhenjie.permission.PermissionListener
import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.toast

abstract class BaseScanOldActivity  : BaseActivity() {

    abstract fun showResult(barcode: String)

    private lateinit var rr: BroadcastReceiver

    var ACTION_SCAN = "com.rfid.SCAN_CMD"
    var ACTION_SCAN_INIT = "com.rfid.SCAN_INIT"

    var ACTION_CLOSE_SCAN = "com.rfid.CLOSE_SCAN"

    var ACTION_SET_SCAN_MODE = "com.rfid.SET_SCAN_MODE"

    lateinit var context: Context

    fun ScanUtil(context: Context){
        this.context = context

        bind()
    }

    fun bind(){
        setScanMode(0)
        val intent = Intent()
        intent.action = ACTION_SCAN_INIT
        context.sendBroadcast(intent)

        val keyfilter = IntentFilter()
        keyfilter.addAction("android.rfid.FUN_KEY")
        keyfilter.addAction("android.intent.action.FUN_KEY")
        registerReceiver(keyReceiver, keyfilter)

        val filter = IntentFilter()
        filter.addAction("com.rfid.SCAN")
        registerReceiver(receiver, filter)
    }

    fun scan() {
        val intent = Intent()
        intent.action = ACTION_SCAN
        context.sendBroadcast(intent)

    }

    fun setScanMode(mode: Int) {
        val intent = Intent()
        intent.action = ACTION_SET_SCAN_MODE
        intent.putExtra("mode", mode)
        context.sendBroadcast(intent)
    }

    fun close() {
        val toKillService = Intent()
        toKillService.action = ACTION_CLOSE_SCAN
        context.sendBroadcast(toKillService)
    }


    open fun ifscan():Boolean{
        return  true

    }

    var keyActive: Boolean = true
    val keyReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {

            val keyCode: Int = intent.getIntExtra("keyCode", 0)
            val keyDown: Boolean = intent.getBooleanExtra("keydown", false)

            if (keyActive && keyDown && (keyCode == KeyEvent.KEYCODE_F1 || keyCode == KeyEvent.KEYCODE_F2 || keyCode == KeyEvent.KEYCODE_F3 ||
                        keyCode == KeyEvent.KEYCODE_F4 || keyCode == KeyEvent.KEYCODE_F5 || keyCode == KeyEvent.KEYCODE_F6)) {
                if(ifscan())
                    scan()
            } else if (!keyDown) {
                keyActive = true
            }
        }
    }

    private var exitTime: Long = 0

    val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (System.currentTimeMillis() - exitTime > 1500) {
                var data = intent.getByteArrayExtra("data")
                var barcode: String
                if (data != null) {
                    barcode = String(data)//Tools.Bytes2HexString(data, data.size)

                    if (barcode != "") {
                        showResult(barcode)
                        keyActive = false
                    }
                    exitTime = System.currentTimeMillis()
                }

            }
        }
    }

    companion object {
        val REQUEST_BARCODE = 666
    }

    fun launchScannerActivity() {
        //如果没有扫码服务
        if (!AndroidUtil(this).isAppInstalled("com.pda.hwscan")) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                AndPermission.with(this@BaseScanOldActivity)
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
}