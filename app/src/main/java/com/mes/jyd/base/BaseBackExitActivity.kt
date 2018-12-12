package com.mes.jyd.base

import android.view.KeyEvent
import android.widget.Toast
import com.mes.jyd.base.BaseActivity

/**
 * 双击返回退出-基类
 * Created by pandanxin on 2017/11/27.
 */
abstract class BaseBackExitActivity: BaseActivity(){

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