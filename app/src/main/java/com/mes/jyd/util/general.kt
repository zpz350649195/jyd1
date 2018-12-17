package com.mes.jyd.util

import android.content.Context
import org.json.JSONObject

object general {
    fun getString(json: JSONObject, key: String):String {
        var rv =""
        if(json.get(key).equals(null)) {
            rv = ""
        }else{
            rv = json.getString(key)
        }

        return rv
    }
}