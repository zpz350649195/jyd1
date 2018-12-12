package com.mes.jyd.view

import android.os.Bundle
import android.view.Gravity
import android.view.KeyEvent
import android.widget.Toast
import com.mes.jyd.R
import com.mes.jyd.base.BaseScanActivity
import com.mes.jyd.base.scanActivity
import com.mes.jyd.delegate.AndroidUtil
import com.mes.jyd.viewModel.LoginViewModel

import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.coroutines.onMenuItemClick
import org.jetbrains.anko.appcompat.v7.toolbar
import org.jetbrains.anko.sdk25.coroutines.onClick


class LoginActivity : scanActivity(){

    lateinit var vm: LoginViewModel

    override fun initParams(args: Bundle?) {
        vm=LoginViewModel(this,this)
        ScanUtil(this)
    }

    override fun initView() {
        verticalLayout {
            toolbar {
                val appName=resources.getString(R.string.app_name)
                val v= AndroidUtil.getAppVersionName(this@LoginActivity)
                title = "$appName $v"
                backgroundColorResource = R.color.colorPrimary
//                setLogo(R.mipmap.ic_launcher)
                inflateMenu(R.menu.login_menu)
                onMenuItemClick { item ->
                    when (item!!.itemId) {
//                        R.id.nav_menu_symbology -> startActivity<SettingsSymbologyActivity>()
                        R.id.nav_menu_service -> startActivity<SetttingsActivity>()

                    }
                }

                elevation = 6f
            }.lparams {
                width= matchParent
            }
            relativeLayout {
                verticalLayout {
                    imageView {
                        imageResource = R.drawable.ic_scan

                        onClick {
                            launchScannerActivity()
                        }
                    }.lparams {
                        height = dip(80)
                        width = dip(80)
                        horizontalGravity = Gravity.CENTER_HORIZONTAL
                    }
                    textView {
                        text=tagObj.scanTag(6)
                        textSize=24f
                        gravity=Gravity.CENTER
                    }.lparams {
                        verticalMargin=dip(16)
                    }
                }.lparams(width= wrapContent) {
                    horizontalGravity = Gravity.CENTER_HORIZONTAL
                    verticalGravity=Gravity.CENTER_VERTICAL
                }

            }.lparams(width= matchParent,height = matchParent)

        }
    }

    override fun doBusiness() {
    }

    override fun showResult(barcode: String) {
        vm.login(barcode)
    }

    override fun onResume() {
        super.onResume()
        open()
      //  scan()
    }

    override fun onPause() {
        super.onPause()
        close()
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
}

