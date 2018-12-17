package com.mes.jyd.service

import android.app.Service
import android.content.Intent
import android.os.Bundle
import android.os.IBinder

import com.seuic.scanner.DecodeInfo
import com.seuic.scanner.DecodeInfoCallBack
import com.seuic.scanner.Scanner
import com.seuic.scanner.ScannerFactory
import com.seuic.scanner.ScannerKey

class ScannerService : Service(), DecodeInfoCallBack {
    lateinit var scanner: Scanner


    override fun onCreate() {
        super.onCreate()

        scanner = ScannerFactory.getScanner(this)
        scanner.open()
        scanner.setDecodeInfoCallBack(this)

        Thread(runnable).start()
    }


    internal var runnable: Runnable = Runnable {
        val ret1 = ScannerKey.open()
        if (ret1 > -1) {
            while (true) {
                val ret = ScannerKey.getKeyEvent()
                if (ret > -1) {
                    when (ret) {
                        ScannerKey.KEY_DOWN -> scanner.startScan()
                        ScannerKey.KEY_UP -> scanner.stopScan()
                    }
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val onStartCommand = super.onStartCommand(intent, flags, startId)
        return 0
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        scanner.setDecodeInfoCallBack(null)
        scanner.close()
        super.onDestroy()
    }

    override fun onDecodeComplete(info: DecodeInfo) {
        val intent = Intent(ACTION)
        val bundle = Bundle()
        bundle.putString(BAR_CODE, info.barcode)
        bundle.putString(CODE_TYPE, info.codetype)
        bundle.putInt(LENGTH, info.length)
        intent.putExtras(bundle)
        sendBroadcast(intent)
    }

    companion object {
        internal val TAG = "ScannerApiTest"

        val BAR_CODE = "barcode"
        val CODE_TYPE = "codetype"
        val LENGTH = "length"

        val ACTION = "seuic.android.scanner.scannertestreciever"
    }
}
