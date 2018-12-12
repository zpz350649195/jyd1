package com.tsp.wmshand.delegate.retrofit

import android.content.Context
import com.mes.jyd.delegate.ParaSave
import com.mes.jyd.json.JsonConverterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory

/**
 *
 * Created by pandanxin on 2017/12/2.
 */
class RetrofitManager(context:Context){

    private var url= ParaSave.getServiceUrl(context)//"http://192.168.1.104:7777"
    // 日志信息拦截器
    private var loggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    private val addMACHeader=object:Interceptor{
        override fun intercept(chain: Interceptor.Chain?): Response {
            val newRequest= chain!!.request().newBuilder()
                    .addHeader("mac","aaa")
                    .build()
            return chain.proceed(newRequest)
        }

    }
    private var okHttpBuilder = OkHttpClient.Builder()
//            .connectTimeout(1, TimeUnit.MINUTES) //default: ten seconds
//            .readTimeout(30, TimeUnit.SECONDS)    //default: ten seconds
//            .writeTimeout(15, TimeUnit.SECONDS)//default: ten seconds
            .addInterceptor(addMACHeader)
            .addInterceptor(loggingInterceptor)
            .build()

//    //SOAP请求过滤器
//    var httpRequestInterceptor = HttpRequestInterceptor()
//    builder.addInterceptor(httpRequestInterceptor);

    //
    val retrofit = Retrofit.Builder()
            .baseUrl(url)
            //.baseUrl("http://192.168.0.106:8081")
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(JsonConverterFactory.create())
            .client(okHttpBuilder)
            .build()!!


}