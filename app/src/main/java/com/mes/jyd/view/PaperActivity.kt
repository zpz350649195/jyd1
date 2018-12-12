package com.mes.jyd.view

import android.Manifest
import android.app.Activity
import android.content.ClipData
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import com.github.barteksc.pdfviewer.PDFView
import com.google.gson.JsonObject
import com.mes.jyd.R
import com.mes.jyd.base.BaseActivity
import com.mes.jyd.viewModel.PaperViewModel
import kotlinx.android.synthetic.main.pdf.*
import org.jetbrains.anko.appcompat.v7.coroutines.onMenuItemClick
import org.jetbrains.anko.ctx
import org.json.JSONObject

class PaperActivity:BaseActivity(){
    lateinit var filename:String
    lateinit var vm:PaperViewModel

    lateinit var  _json: JSONObject //FTP服务器信息
    var iftrue:Boolean=true //获取FTP服务信息是否成功
    var page:Int=0
    //lateinit var _toolbar: Toolbar


    lateinit var _pdf: PDFView

   fun button(view: View){
       _pdf=pdfView
       vm.loadpdf()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pdf)
        getPermission()

        //获取文件名称
        filename=intent.getStringExtra("filename")
        page=intent.getIntExtra("page",0)


        _pdf=pdfView
        papertoolbar.setNavigationOnClickListener {
            this.finish()
        }

        papertoolbar.menu.add(0,0,0,"下载pdf")
        papertoolbar.menu.add(0,1,0,"查看pdf")

        papertoolbar.onMenuItemClick { item ->
            when (item!!.itemId) {
                0 -> {
                    vm.download()
                    vm.loadpdf()
                }
                1 -> {
                    // vm.download()
                    getPermission()
                    vm.loadpdf()
                }
            }

        }
    }

    override fun initParams(args: Bundle?) {
       vm= PaperViewModel(this,this.ctx)
        /*coordinatorLayout {
            //工具栏开始
            _toolbar = toolbar {
                title = "PDF查看"

                backgroundColorResource = R.color.colorPrimary
                setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
                setNavigationOnClickListener {
                    finish()
                }
                //查询按钮
               var down= menu.add(0,0,0,"下载pdf")
                menu.add(0,1,0,"查看pdf")

                onMenuItemClick { item ->
                    when (item!!.itemId) {
                        0 -> {
                           vm.download()
                        }
                        1 -> {
                            // vm.download()
                            vm.loadpdf()
                        }
                    }
                }
              // down.setOnMenuItemClickListener()

                *//*inflateMenu(R.menu.product)
                onMenuItemClick { item ->
                    when (item!!.itemId) {
                        R.id.menu_paoduct_line -> {
                        }
                        R.id.menu_paoduct_paper -> {
                        }
                        R.id.menu_paoduct_error -> {
                        }
                    }
                }*//*
                elevation = 6f

            }.lparams {
                width = matchParent
            }
        }*/


    }

    override fun initView() {

    }

    override fun doBusiness() {
        vm.getFtpMsg("paper")
    }
    var PERMISSIONS_STORAGE:Array<String> = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)

    /**
     * 获取动态权限
     */
    fun getPermission(){
        var hasWriteContactsPermission = ContextCompat.checkSelfPermission(ctx,
        Manifest.permission.READ_EXTERNAL_STORAGE)
        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(this,
                    PERMISSIONS_STORAGE,1)
            }
            ActivityCompat.requestPermissions(this,
                PERMISSIONS_STORAGE,1)
        }
        while (( ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE))!= PackageManager.PERMISSION_GRANTED) {
        }
    }

    override fun finish() {
        var _intent=Intent()
        _intent.putExtra("page",page)
        setResult(Activity.RESULT_OK,_intent)
        super.finish()
    }
}