package com.mes.jyd.delegate

import java.text.SimpleDateFormat
import java.util.*

class utils {
    fun getTime(): String {
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val curDate = Date(System.currentTimeMillis())
        return format.format(curDate)
    }

    fun getDate(): String {
        val format = SimpleDateFormat("yyyy-MM-dd")
        val curDate = Date(System.currentTimeMillis())
        return format.format(curDate)
    }

    fun getTimeByPattern(pattern:String):String{
        val format = SimpleDateFormat(pattern)
        val curDate = Date(System.currentTimeMillis())
        return format.format(curDate)
    }
}