package com.mes.jyd.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class SpinnerAdapter(private val mContext: Context, private val mStringArray: ArrayList<String>) :
    ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, mStringArray) {

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        //修改Spinner展开后的字体颜色
        if (convertView == null) {
            val inflater = LayoutInflater.from(mContext)
            convertView = inflater.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false)
        }

        //此处text1是Spinner默认的用来显示文字的TextView
        val tv = convertView!!.findViewById<View>(android.R.id.text1) as TextView
        tv.text = mStringArray[position]
        tv.textSize = 22f
        tv.setTextColor(Color.BLACK)

        return convertView

    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        // 修改Spinner选择后结果的字体颜色
        if (convertView == null) {
            val inflater = LayoutInflater.from(mContext)
            convertView = inflater.inflate(android.R.layout.simple_spinner_item, parent, false)
        }

        //此处text1是Spinner默认的用来显示文字的TextView
        val tv = convertView!!.findViewById<View>(android.R.id.text1) as TextView
        tv.text = mStringArray[position]
        tv.textSize = 18f
        tv.setTextColor(Color.BLUE)
        return convertView
    }

}
