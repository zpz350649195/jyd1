package com.tsp.wmshand.delegate.retrofit

/**
 *
 * Created by pandanxin on 2017/12/1.
 */
import org.json.JSONException
import org.json.JSONObject

import okhttp3.ResponseBody
import retrofit2.Converter
import java.io.IOException


@Suppress("UNCHECKED_CAST")
internal class JsonResponseBodyConverter<T> : Converter<ResponseBody, T> {

    @Throws(IOException::class)
    override fun convert(value: ResponseBody): T? {
        val jsonObj: JSONObject
        return try {
            jsonObj = JSONObject(value.string())
            jsonObj as T
        } catch (e: JSONException) {
            null
        }

    }
}