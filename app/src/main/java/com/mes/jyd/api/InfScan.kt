package com.mes.jyd.api

import android.content.Context
import com.mes.jyd.base.scanActivity

interface InfScan {

  //  abstract  fun showResult(barcode: String)
    //初始化
    fun ScanUtil(context: Context,_scanActivity: scanActivity)
    //绑定
    fun bind()

  //打开扫描
    fun open()

    //开始扫描
    fun scan()

    //结束扫描

    fun close()
}