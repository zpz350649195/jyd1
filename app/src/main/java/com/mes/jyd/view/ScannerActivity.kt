package com.mes.jyd.view

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.ViewManager
import com.mes.jyd.R
import com.mes.jyd.delegate.SoundUtil
import me.dm7.barcodescanner.zbar.BarcodeFormat
import me.dm7.barcodescanner.zbar.Result
import me.dm7.barcodescanner.zbar.ZBarScannerView
import org.jetbrains.anko.backgroundColorResource
import org.jetbrains.anko.custom.ankoView
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.toolbar
import org.jetbrains.anko.verticalLayout


class ScannerActivity : AppCompatActivity(), ZBarScannerView.ResultHandler  {

    private lateinit var mScannerView: ZBarScannerView

    private var mSelectedIndices: ArrayList<Int>? = null

    private lateinit var soundUtil: SoundUtil

//    private lateinit var scannerView:View

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        mSelectedIndices = state?.getIntegerArrayList(Companion.SELECTED_FORMATS)
        soundUtil= SoundUtil()
        soundUtil.initSoundPool(this)
//        setContentView(R.layout.activity_scanner)
//        val contentFrame = findViewById<View>(R.id.content_frame) as ViewGroup
//        mScannerView = ZBarScannerView(this)
        verticalLayout {
            toolbar {
                title = "扫码"
                backgroundColorResource = R.color.colorPrimary
                setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
                setNavigationOnClickListener {
                    finish()
                }
                elevation = 6f
            }.lparams(width = matchParent) {
                // alignParentTop()
            }
            mScannerView=zbarScannerView {  }.lparams(width= matchParent,height = matchParent)
        }

        setupFormats()
//        contentFrame.addView(mScannerView)

    }
//    inline fun ViewManager.zbarScannerView() = zbarScannerView(theme = 0){}

    private inline fun ViewManager.zbarScannerView(init: ZBarScannerView.() -> Unit): ZBarScannerView {
        return ankoView({ ZBarScannerView(it) }, theme = 0, init = init)
    }

    override fun onResume() {
        super.onResume()
        mScannerView.setResultHandler(this) // Register ourselves as a handler for scan results.
        mScannerView.startCamera()          // Start camera on resume
    }

    override fun onPause() {
        super.onPause()
        mScannerView.stopCamera()           // Stop camera on pause
    }

    override fun handleResult(rawResult: Result) {
        Log.v(ContentValues.TAG, rawResult.contents) // Prints scan results
        Log.v(ContentValues.TAG, rawResult.barcodeFormat.name) // Prints the scan format (qrcode, pdf417 etc.)
//        toast("${rawResult.barcodeFormat.name} - ${rawResult.contents}")
        val intent= Intent()
        intent.putExtra("barcode",rawResult.contents)
        setResult(Activity.RESULT_OK,intent)

        // If you would like to resume scanning, call this method below:
        mScannerView.resumeCameraPreview(this)
        soundUtil.play(1, 0)
        finish()
    }

    private fun setupFormats() {
        val formats = ArrayList<BarcodeFormat>()
        if (mSelectedIndices == null || mSelectedIndices!!.isEmpty()) {
            mSelectedIndices = ArrayList()
            for (i in BarcodeFormat.ALL_FORMATS.indices) {
                mSelectedIndices!!.add(i)
            }
        }

        mSelectedIndices!!.mapTo(formats) { BarcodeFormat.ALL_FORMATS[it] }
        mScannerView.setFormats(formats)
    }

    companion object {
        private val SELECTED_FORMATS = "SELECTED_FORMATS"
    }
}
