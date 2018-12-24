package com.mes.jyd.api

import io.reactivex.Single
import org.json.JSONObject
import retrofit2.http.GET
import retrofit2.http.Query




interface ApiService {

    //登录
    @GET("meshandset.asmx/loginbybarcode")
    fun loginbybarcode(
            @Query("barcode") barcode: String
        ): Single<JSONObject>

    //获取权限
    @GET("meshandset.asmx/getpermission")
    fun getPermission(
        @Query("ui") userId: String
    ): Single<JSONObject>


    //获取App最新版本号
    @GET("meshandset.asmx/getappnewversion")
    fun getAppNewVersion():Single<JSONObject>

    //生产扫码
    @GET("product.asmx/productscan")
    fun productscan(
        @Query("ui") ui: Int,
        @Query("pnid") pnid: Int,
        @Query("bc") bc: String
    ): Single<JSONObject>

    //获取生产计划
    @GET("product.asmx/queryplan")
    fun queryplan(
        @Query("ui") ui: Int,
        @Query("pnid") pnid: Int,
        @Query("page") page: Int,
        @Query("pnum") pnum: Int
    ): Single<JSONObject>

    //获取生产详细生产任务
    @GET("product.asmx/getdetail")
    fun getdetail(
        @Query("ui") ui: Int,
        @Query("pnid") pnid: Int
    ): Single<JSONObject>

    //获取检测项
    @GET("product.asmx/getcheckitem")
    fun getcheckitem(
        @Query("ui") ui: Int,
        @Query("pnid") pnid: Int,
        @Query("checktype") checktype: Int
    ): Single<JSONObject>

    //设置检测结果值
    @GET("product.asmx/setcheckresult")
    fun setcheckresult(
        @Query("ui") ui: Int,
        @Query("pnid") pnid: Int,
        @Query("id") id: Int,
        @Query("value") value: Float,
        @Query("checkbox") checkbox: Int,
        @Query("desc") desc: String

    ): Single<JSONObject>

    //巡检获取待检验列表
    @GET("check.asmx/getnocheckitem")
    fun getinspectitem(
        @Query("ui") ui: Int,
        @Query("page") page: Int
    ): Single<JSONObject>

    //巡检根据主表id获取检测项
    @GET("check.asmx/getcheckitem")
    fun getinspectcheckitem(
        @Query("id") id: Int
    ): Single<JSONObject>

    //根据产品条码获取工序信息
    @GET("check.asmx/gettechbybarcode")
    fun getinspecttech(
        @Query("bc") bc: String
    ): Single<JSONObject>

    //新增巡检项
    @GET("check.asmx/addcheck")
    fun inspectadditem(
        @Query("ui") ui: Int,
        @Query("bid") bid: Int,
        @Query("procid") procid: Int
    ): Single<JSONObject>

    //质量排查
    @GET("check.asmx/setrecheck")
    fun inspectrecheck(
        @Query("id") id: Int
    ): Single<JSONObject>

    //待入库清单
    @GET("io.asmx/getinstockitem")
    fun getinstockitem(
        @Query("ui") ui: Int,
        @Query("page") page: Int
    ): Single<JSONObject>

    //根据id或条码获取待入库详细信息
    @GET("io.asmx/getdetailbyid")
    fun instockgetdetail(
        @Query("type") type: Int,
        @Query("id") id: Int,
        @Query("bc") bc: String
    ): Single<JSONObject>

    //批量入库
    @GET("io.asmx/instock")
    fun instock(
        @Query("ui") ui: Int,
        @Query("lgort") lgort: String,
        @Query("arrstr") arrstr: String
    ): Single<JSONObject>

    //获取FTP服务器信息 type: tech 工艺文件 paper 图纸
    @GET("product.asmx/getftpmsg")
    fun getftpmsg(
        @Query("type") type: String
    ): Single<JSONObject>

}
