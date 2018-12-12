package com.tsp.wmshand.delegate.retrofit

/**
 *
 * Created by pandanxin on 2017/12/1.
 */
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Converter
import java.io.IOException


internal class JsonRequestBodyConverter<T> : Converter<T, RequestBody> {

    @Throws(IOException::class)
    override fun convert(value: T): RequestBody {
        return RequestBody.create(MEDIA_TYPE, value.toString())
    }

    companion object {
        private val MEDIA_TYPE = MediaType.parse("application/text; charset=UTF-8")
    }
}