package com.mes.jyd.scan

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.view.KeyEvent
import com.mes.jyd.api.InfScan
import com.mes.jyd.base.scanActivity

class HHSScan() :InfScan{


    var ACTION_SCAN = "com.rfid.SCAN_CMD"
    var ACTION_SCAN_INIT = "com.rfid.SCAN_INIT"

    var ACTION_CLOSE_SCAN = "com.rfid.CLOSE_SCAN"

    var ACTION_SET_SCAN_MODE = "com.rfid.SET_SCAN_MODE"

    lateinit var context: Context
    lateinit var _as:scanActivity

    override fun ScanUtil(context: Context,_scanActivity: scanActivity){
        this.context = context
        this._as=_scanActivity

        bind()
    }

    override fun bind() {
        setScanMode(0)
        val intent = Intent()
        intent.action = ACTION_SCAN_INIT
        context.sendBroadcast(intent)

        val keyfilter = IntentFilter()
        keyfilter.addAction("android.rfid.FUN_KEY")
        keyfilter.addAction("android.intent.action.FUN_KEY")
        context.registerReceiver(keyReceiver, keyfilter)

        val filter = IntentFilter()
        filter.addAction("com.rfid.SCAN")
        context.registerReceiver(receiver, filter)
    }

    override fun open() {

    }

   override fun scan() {
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

    override fun close() {
        val toKillService = Intent()
        toKillService.action = ACTION_CLOSE_SCAN
        context.sendBroadcast(toKillService)
    }


   /* open fun ifscan():Boolean{
        return  true

    }*/

    var keyActive: Boolean = true
    val keyReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {

            val keyCode: Int = intent.getIntExtra("keyCode", 0)
            val keyDown: Boolean = intent.getBooleanExtra("keydown", false)

            if (keyActive && keyDown && (keyCode == KeyEvent.KEYCODE_F1 || keyCode == KeyEvent.KEYCODE_F2 || keyCode == KeyEvent.KEYCODE_F3 ||
                        keyCode == KeyEvent.KEYCODE_F4 || keyCode == KeyEvent.KEYCODE_F5 || keyCode == KeyEvent.KEYCODE_F6)) {
                if(_as.ifscan())
                    scan()
            } else if (!keyDown) {
                keyActive = true
            }
        }
    }

    var exitTime: Long = 0

    val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (System.currentTimeMillis() - exitTime > 1500) {
                var data = intent.getByteArrayExtra("data")
                var barcode: String
                if (data != null) {
                    barcode = String(data)//Tools.Bytes2HexString(data, data.size)

                    if (barcode != "") {
                        _as.showResult(barcode)
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
}