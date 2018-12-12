package com.mes.jyd.json


import com.tsp.wmshand.delegate.retrofit.JsonRequestBodyConverter
import com.tsp.wmshand.delegate.retrofit.JsonResponseBodyConverter
import org.json.JSONObject

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

class JsonConverterFactory : Converter.Factory() {

    override fun responseBodyConverter(type: Type?, annotations: Array<Annotation>?, retrofit: Retrofit?): Converter<ResponseBody, *>? {
        return JsonResponseBodyConverter<JSONObject>()
    }

    override fun requestBodyConverter(type: Type?, parameterAnnotations: Array<Annotation>?, methodAnnotations: Array<Annotation>?, retrofit: Retrofit?): Converter<*, RequestBody>? {
        return JsonRequestBodyConverter<JSONObject>()
    }

    companion object {

        fun create(): JsonConverterFactory {
            return JsonConverterFactory()
        }
    }

    /*public Converter<ResponseBody, ?> fromResponseBody(Type type, Annotation[] annotations) {
        return new JsonResponseBodyConverter<JSONObject>();
    }
    public Converter<?, RequestBody> toRequestBody(Type type, Annotation[] annotations) {
        return new JsonRequestBodyConverter<JSONObject>();
    }*/
}