package com.mes.jyd.view

import android.app.FragmentManager
import android.app.FragmentTransaction
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.View
import com.mes.jyd.R
import android.widget.Toast
import com.mes.jyd.fragment.ArticleListFragment
import kotlinx.android.synthetic.main.looper_activity2.*

class WorkThreadActivity : AppCompatActivity() {
   // private var btnSendToWorkUI: Button? = null
    private var handler: Handler? = null
    lateinit var manage:FragmentManager
    lateinit var transaction: FragmentTransaction
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.looper_activity2)
        button1.setOnClickListener {
            manage=fragmentManager
            transaction=manage.beginTransaction()
            val articleListFragment= ArticleListFragment()
            transaction.add(R.id.center,articleListFragment,"article")
            transaction.commit()

        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return super.onCreateOptionsMenu(menu)
    }


}