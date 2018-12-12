package com.mes.jyd.delegate

import org.json.JSONArray
import java.math.BigDecimal

/**
 *
 * Created by pandanxin on 2/2/18.
 */
class ArithUtil{
    companion object {
        private const val DEF_DIV_SCALE=10
        //加法
        fun add(d1:Double,d2:Double):Double{
            val b1=BigDecimal(d1.toString())
            val b2=BigDecimal(d2.toString())

            return b1.add(b2).toDouble()
        }
        //减法
        fun sub(d1:Double,d2:Double):Double{
            val b1=BigDecimal(d1.toString())
            val b2=BigDecimal(d2.toString())

            return b1.subtract(b2).toDouble()
        }
        //乘法
        fun mul(d1:Double,d2:Double):Double{
            val b1=BigDecimal(d1.toString())
            val b2=BigDecimal(d2.toString())

            return b1.multiply(b2).toDouble()
        }
        //除法
        fun div(d1:Double,d2: Double):Double{
            return div(d1,d2, DEF_DIV_SCALE)
        }

        fun div(d1:Double,d2: Double,scale:Int):Double{
            if(scale<0){
                throw IllegalAccessException("The scale must be a positive integer or zero")
            }
            val b1=BigDecimal(d1.toString())
            val b2=BigDecimal(d2.toString())

            return b1.divide(b2,scale,BigDecimal.ROUND_HALF_UP).toDouble()
        }

        fun  joinJSONArray(mData:JSONArray , array:JSONArray ): JSONArray? {
            val buffer =  StringBuffer()
            try {
                var len = mData.length()
                for ( i in 0 until  len) {
                    val obj1 =  mData.getJSONObject(i)
                    if (i == len - 1)
                        buffer.append(obj1.toString())
                    else
                        buffer.append(obj1.toString()).append(",")
                }
                len = array.length()
                if (len > 0&&buffer.isNotEmpty())
                    buffer.append(",")
                for (i in 0 until  len) {
                    val obj1 =  array.getJSONObject(i)
                    if (i == len - 1)
                        buffer.append(obj1.toString())
                    else
                        buffer.append(obj1.toString()).append(",")
                }
                buffer.insert(0, "[").append("]")
                return  JSONArray(buffer.toString())
            } catch ( e:Exception) {
            }
            return null
        }


    }
}