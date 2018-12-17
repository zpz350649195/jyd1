package com.mes.jyd.scan

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.view.KeyEvent
import com.example.iscandemo.ScannerInerface
import com.mes.jyd.api.InfScan
import com.mes.jyd.base.scanActivity

class PBScan : InfScan {

    private lateinit var rr: BroadcastReceiver
   // lateinit var scan: ScannerInerface

    lateinit var context: Context
    lateinit var _as: scanActivity
    var SCANACTION="com.android.server.scannerservice.broadcast"

    override fun ScanUtil(context: Context, _scanActivity: scanActivity){
        this.context = context
        this._as=_scanActivity

        bind()

    }

    override fun bind(){
        /*scan= ScannerInerface(context)
        //设置返回字符串为UTF-8
        scan.setCharSetMode(4)
        //扫描为广播形式 0为直接发送到编辑框
        scan.setOutputMode(1)*/
        /* setScanMode(0)
         val intent = Intent()
         intent.action = ACTION_SCAN_INIT
         context.sendBroadcast(intent)

         val keyfilter = IntentFilter()
         keyfilter.addAction("android.rfid.FUN_KEY")
         keyfilter.addAction("android.intent.action.FUN_KEY")
         registerReceiver(keyReceiver, keyfilter)
         */
    }

    override fun open() {
        val filter = IntentFilter(SCANACTION)
        filter.setPriority(Int.MAX_VALUE)

        context.registerReceiver(receiver, filter)
    }

    override fun scan() {
        /*val intent = Intent()
        intent.action = ACTION_SCAN
        context.sendBroadcast(intent)*/


    }

/*
    fun setScanMode(mode: Int) {
        val intent = Intent()
        intent.action = ACTION_SET_SCAN_MODE
        intent.putExtra("mode", mode)
        context.sendBroadcast(intent)
    }
*/

    override fun close() {
        /*val toKillService = Intent()
        toKillService.action = ACTION_CLOSE_SCAN
        context.sendBroadcast(toKillService)*/
        context.unregisterReceiver(receiver)
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

            val scanResult = intent.getStringExtra("value")

            if (intent.action.equals(SCANACTION)) {
                val code = intent.getStringExtra("scannerdata")
                _as.showResult(code)
            }
            /*if (System.currentTimeMillis() - exitTime > 1500) {
                *//*var data = intent.getByteArrayExtra("data")
                 var barcode: String = ""
                 if (data != null) {
                     barcode = String(data)//Tools.Bytes2HexString(data, data.size)

                     if (barcode != "") {
                         showResult(barcode)
                         keyActive = false
                     }
                     exitTime = System.currentTimeMillis()
                 }*//*
                 val scanResult = intent.getStringExtra("value")
                 if (scanResult != "") {
                     showResult(scanResult)
                     keyActive = false
                     exitTime = System.currentTimeMillis()
                 }

             }*/
        }
    }

}