package com.mes.jyd.view

import android.content.Context
import android.os.Bundle
import android.widget.EditText
import com.mes.jyd.R
import com.mes.jyd.base.BaseActivity
import com.mes.jyd.fragment.SettingFragment
import org.jetbrains.anko.appcompat.v7.toolbar
import org.jetbrains.anko.backgroundColorResource
import org.jetbrains.anko.ctx
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.verticalLayout

class SetttingsActivity : BaseActivity() {

    private lateinit var txtUrl: EditText
    private val settingFragment = SettingFragment()
    private val MainID = 1
    override fun initParams(args: Bundle?) {
    }


    override fun initView() {
        verticalLayout {
            toolbar {
                title = "设置"
                backgroundColorResource = R.color.colorPrimary


                setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
                setNavigationOnClickListener {
                    finish()
                }

                elevation = 6f
            }
            verticalLayout {
                id = MainID
            }.lparams(width = matchParent)
        }
        fragmentManager.beginTransaction()
                .add(MainID, settingFragment)
                .commit()
    }

    override fun doBusiness() {
        val sharedPreferences = ctx.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val editer = sharedPreferences.edit()
        if (sharedPreferences.getString("server_ip", "").isEmpty()) {
            editer.putString("server_ip", resources.getString(R.string.settings_server_ip))
        }
        if (sharedPreferences.getString("server_port", "").isEmpty()) {
            editer.putString("server_port", resources.getString(R.string.settings_server_port))
        }

        if (sharedPreferences.getString("server_ins", "").isEmpty()) {
            editer.putString("server_ins", resources.getString(R.string.settings_server_ins))
        }

        try {
            if (sharedPreferences.getString("display_system", "").isEmpty()) {
                editer.putBoolean("display_system", resources.getString(R.string.settings_system_display).toBoolean())
            }

            if (sharedPreferences.getString("display_keyboard", "").isEmpty()) {
                editer.putBoolean("display_keyboard", resources.getString(R.string.settings_display_keyboard).toBoolean())
            }
        }catch (ex:Exception){ }

        /*if (sharedPreferences.getString("print_paper_width", "").isEmpty()) {
            editer.putString("print_paper_width", resources.getString(R.string.settings_print_paper_width))
        }
        if (sharedPreferences.getString("print_paper_height", "").isEmpty()) {
            editer.putString("print_paper_height", resources.getString(R.string.settings_print_paper_height))
        }*/

        editer.apply()
    }
}
