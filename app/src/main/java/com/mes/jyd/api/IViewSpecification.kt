package com.mes.jyd.api

import android.os.Bundle

/**
 * View 层规范接口
 * Created by pandanxin on 2017/11/27.
 */
interface IViewSpecification{
    //初始化参数
    fun initParams(args:Bundle?)
    //初始化视图
    fun initView()
    //处理业务
    fun doBusiness()
}