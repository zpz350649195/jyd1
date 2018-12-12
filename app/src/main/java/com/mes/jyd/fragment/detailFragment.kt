package com.mes.jyd.fragment

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mes.jyd.R
import kotlinx.android.synthetic.main.fragmentrighttest.*
import kotlinx.android.synthetic.main.fragmentrighttest.view.*

class detailFragment:Fragment(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        var view:View=inflater!!.inflate(R.layout.fragmentrighttest,null)
        view.textView1.setText(arguments.getString("item"))
        return view

    }

    override fun onPause() {
        super.onPause()
    }
}