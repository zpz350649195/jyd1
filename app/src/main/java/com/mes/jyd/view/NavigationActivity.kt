package com.mes.jyd.view

import android.Manifest
import android.app.Service
import android.content.ComponentName
import android.content.DialogInterface
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.IBinder
import android.os.Process
import android.support.design.widget.CoordinatorLayout
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.Toolbar
import android.view.Gravity
import android.view.KeyEvent
import android.widget.GridView
import android.widget.TextView
import android.widget.Toast
import com.mes.jyd.R
import com.mes.jyd.adapter.DashboardCardItemAdapter
import com.mes.jyd.base.BaseActivity
import com.mes.jyd.delegate.AndroidUtil
import com.mes.jyd.delegate.ParaSave
import com.mes.jyd.service.DownloadService
import com.mes.jyd.viewModel.NavigationViewModel
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.Permission
import com.yanzhenjie.permission.PermissionListener
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.coroutines.onMenuItemClick
import org.jetbrains.anko.appcompat.v7.toolbar
import org.jetbrains.anko.design.coordinatorLayout
import org.jetbrains.anko.sdk25.coroutines.onItemClick
import org.jetbrains.anko.support.v4.swipeRefreshLayout
import java.io.File


class NavigationActivity : BaseActivity() {
    private lateinit var vm: NavigationViewModel
    lateinit var mAdapter: DashboardCardItemAdapter

    lateinit var refreshLayout: SwipeRefreshLayout
    lateinit var mainLayout: CoordinatorLayout
    private lateinit var toolbar: Toolbar
    private lateinit var gridview:GridView
    private lateinit var emptyView: TextView

    lateinit var scanAlert: DialogInterface
    var name = ""

    override fun initParams(args: Bundle?) {
        vm = NavigationViewModel(this, this)
        mAdapter=DashboardCardItemAdapter(vm)
    //    ScanUtil(this.ctx)
      //  vm.getUserBarcode()
    }

    override fun initView() {
        val list = vm.list
        mainLayout=coordinatorLayout {
            toolbar=toolbar {
                val appName=resources.getString(R.string.app_name)
                val v= AndroidUtil.getAppVersionName(this@NavigationActivity)
                title = "$appName $v"
                subtitle = name
                backgroundColorResource = R.color.colorPrimary

                inflateMenu(R.menu.nav_menu)
                onMenuItemClick { item ->
                    when (item!!.itemId) {
//                        R.id.nav_menu_symbology -> startActivity<SettingsSymbologyActivity>()
                        R.id.nav_menu_service -> startActivity<SetttingsActivity>()
//                        R.id.nav_menu_test -> startActivity<TestActivity>()
                        R.id.nav_menu_changeaccount -> vm.changeAccount()
                        R.id.nav_menu_download->{
                            val now=System.currentTimeMillis()
                            if (ContextCompat.checkSelfPermission(this@NavigationActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                AndPermission.with(this@NavigationActivity)
                                        .requestCode(102)
                                        .permission(Permission.STORAGE)
                                        .callback(object : PermissionListener {
                                            override fun onSucceed(requestCode: Int, grantPermissions: MutableList<String>) {
                                                vm.getNewVersion()
                                                ParaSave.saveUpdateCheckTime(this@NavigationActivity,now)
                                            }

                                            override fun onFailed(requestCode: Int, deniedPermissions: MutableList<String>) {
                                                toast("请授予存储空间权限，才能下载")
                                            }
                                        })
                                        .start()
                            } else {
                                vm.getNewVersion()
                                ParaSave.saveUpdateCheckTime(this@NavigationActivity,now)
                            }
                        }
                    }
                }
                elevation = 8f
            }.lparams(width = matchParent)
            refreshLayout = swipeRefreshLayout {
                setProgressBackgroundColorSchemeResource(android.R.color.white)
                setColorSchemeResources(android.R.color.holo_blue_light,
                        android.R.color.holo_red_light,
                        android.R.color.holo_orange_light)
//                backgroundColor = Color.WHITE
                gridview=gridView {
                    lparams {
                        topPadding=dip(16)
                    }
                    numColumns = 3
                    gravity = Gravity.CENTER
                    adapter = mAdapter
//                stretchMode = GridView.STRETCH_SPACING
//                columnWidth = dip(120)
//                horizontalSpacing=dip(2)
//                verticalSpacing=dip(2)
                    onItemClick { _, _, i, _ ->
                        vm.go(this@NavigationActivity, list[i]["item_id"].toString())

                    }
                }
                //下拉刷新事件
                setOnRefreshListener {
                   // vm.getPermission()
                    refreshLayout.isRefreshing = false
                }
            }.lparams(width = matchParent, height = matchParent) {
                topMargin = dip(56)
            }
            emptyView = textView {
                text = "无菜单"
                textSize = 30f
                gravity = Gravity.CENTER
            }.lparams {
                width = matchParent
                height = matchParent
            }
            gridview.emptyView=emptyView
        }
    }

    override fun doBusiness() {
      vm.getUserBarcode()
        //    vm.getuser()

        //待配送工单通知服务
       /* val startIntent = Intent(this, DistributionOrderDistributionOrderService
                DownloadServiceService::class.java)
        startService(startIntent)*/
    }



    override fun onResume() {
        super.onResume()
        toolbar.subtitle=name
     //   open()
      //  getNewVersion()


    }

    override fun onPause() {
        super.onPause()
      //  close()
    }

    private fun getNewVersion(){
        val now=System.currentTimeMillis()
        val check=ParaSave.getUpdateCheckTime(this)
//        7200000
        if(check==0L||(now-check>7200000)){
            vm.getNewVersion()
            ParaSave.saveUpdateCheckTime(this,now)
        }
    }

    private var exitTime: Long = 0

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN) {
            if (System.currentTimeMillis() - exitTime > 2000) {
                Toast.makeText(applicationContext, "再按一次退出程序",
                        Toast.LENGTH_SHORT).show()
                exitTime = System.currentTimeMillis()
            } else {
                finish()
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

     fun showResult(barcode: String) {
        toast(barcode)
    }

    fun showUpdate(version:String,desc:String){
        try {
            scanAlert.cancel()
        }catch (ex:Exception){

        }

        scanAlert=alert (desc,"新版本 $version"){
            positiveButton("更新"){
                downApk(version)
            }
            negativeButton("取消"){
            }
            isCancelable=false
        }.show()
    }

    private var conn: ServiceConnection? = null
    private fun downApk(version: String) {
        val url = "${ParaSave.getServiceUrl(this)}app/mes_$version.apk"
        if(conn==null) {
            conn = object : ServiceConnection {
                override fun onServiceConnected(name: ComponentName, service: IBinder) {
                    val binder = service as DownloadService.DownloadBinder
                    val myService = binder.getService()
                    myService.downApk(
                        this@NavigationActivity,
                        this@NavigationActivity,
                        url,
                        object : DownloadService.DownloadCallback {
                            override fun onPrepare() {

                            }

                            override fun onProgress(progress: Int) {
                                showTextToast("正在下载：$progress %")
                            }

                            override fun onComplete(file: File) {
                                try {
//                                val authority = "com.tsp.fileProvider"
//                                val fileUri = FileProvider.getUriForFile(this@NavigationActivity, authority, file)
//                                val intent = Intent(Intent.ACTION_VIEW)
//                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//
//                                //7.0以上需要添加临时读取权限
//                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                                    intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
//                                    intent.setDataAndType(fileUri, "application/vnd.android.package-archive")
//                                } else {
//                                    val uri = Uri.fromFile(file)
//                                    intent.setDataAndType(uri, "application/vnd.android.package-archive")
//                                }
//
//                                startActivity(intent)
                                    val androidUtil = AndroidUtil(this@NavigationActivity)

                                    androidUtil.installApk(this@NavigationActivity, file.toString())
                                    //弹出安装窗口把原程序关闭。
                                    //避免安装完毕点击打开时没反应
                                    Process.killProcess(Process.myPid())
//                                System.exit(0)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }

                            }

                            override fun onFail(msg: String) {
                                showTextToast(msg)
                            }
                        })
                }

                override fun onServiceDisconnected(name: ComponentName) {
                    //意味中断，较小发生，酌情处理
                }
            }
        }
        val intent = Intent(this@NavigationActivity, DownloadService::class.java)
        bindService(intent, conn, Service.BIND_AUTO_CREATE)
    }



}
