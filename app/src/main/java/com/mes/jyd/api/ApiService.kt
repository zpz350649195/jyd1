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

    //获取FTP服务器信息 type: tech 工艺文件 paper 图纸
    @GET("product.asmx/getftpmsg")
    fun getftpmsg(
        @Query("type") type: String
    ): Single<JSONObject>

}
