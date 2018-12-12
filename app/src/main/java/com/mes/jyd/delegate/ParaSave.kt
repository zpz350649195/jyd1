package com.mes.jyd.delegate

import android.content.Context
import com.mes.jyd.R

object ParaSave {

    fun saveSymbology(context: Context, symbologys: String) {
        val shared = context.getSharedPreferences("symbology", Context.MODE_PRIVATE)
        val editor = shared.edit()
        editor.putString("sym", symbologys)
        editor.apply()
    }


    fun getSymbology(context: Context): String {
        val shared = context.getSharedPreferences("symbology", Context.MODE_PRIVATE)

        return shared.getString("sym", "0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51")
    }

    fun getServiceUrl(context: Context): String {
        val shared = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        val ip=shared.getString("server_ip", context.resources.getString(R.string.settings_server_ip))
        val port=shared.getString("server_port", context.resources.getString(R.string.settings_server_port))
        return "http://$ip:$port/"
    }

    fun getIns(context: Context):String{
        val shared = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        return shared.getString("server_ins", context.resources.getString(R.string.settings_server_ins))
    }

    fun getSystemDisplay(context: Context): Boolean {
        val shared = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        return shared.getBoolean("display_system",context.resources.getString(R.string.settings_system_display).toBoolean())
    }

    fun saveUserBarcode(context: Context, userBarcode: String) {
        val shared = context.getSharedPreferences("system", Context.MODE_PRIVATE)
        val editor = shared.edit()
        editor.putString("userBarcode", userBarcode)
        editor.apply()
    }

    fun getUserBarcode(context: Context): String {
        val shared = context.getSharedPreferences("system", Context.MODE_PRIVATE)
        return shared.getString("userBarcode", "")
    }


    fun saveUserName(context: Context, userName: String) {
        val shared = context.getSharedPreferences("system", Context.MODE_PRIVATE)
        val editor = shared.edit()
        editor.putString("userName", userName)
        editor.apply()
    }

    fun getUserName(context: Context): String {
        val shared = context.getSharedPreferences("system", Context.MODE_PRIVATE)
        return shared.getString("userName", "")
    }


    fun saveUserId(context: Context, userId: String) {
        val shared = context.getSharedPreferences("system", Context.MODE_PRIVATE)
        val editor = shared.edit()
        editor.putString("userId", userId)
        editor.apply()
    }

    fun getUserId(context: Context): String {
        val shared = context.getSharedPreferences("system", Context.MODE_PRIVATE)
        return shared.getString("userId", "-1")
    }

    fun saveBindMtlCar(context: Context, bindMtlCar: String) {
        val shared = context.getSharedPreferences("system", Context.MODE_PRIVATE)
        val editor = shared.edit()
        editor.putString("bindMtlCar", bindMtlCar)
        editor.apply()
    }

    fun getBindMtlCar(context: Context): String {
        val shared = context.getSharedPreferences("system", Context.MODE_PRIVATE)
        return shared.getString("bindMtlCar", "")
    }

    fun savePrintMAC(context: Context, printMAC: String) {
        val shared = context.getSharedPreferences("system", Context.MODE_PRIVATE)
        val editor = shared.edit()
        editor.putString("printMac", printMAC)
        editor.apply()
    }

    fun getPrintMAC(context: Context): String {
        val shared = context.getSharedPreferences("system", Context.MODE_PRIVATE)
        return shared.getString("printMac", "")
    }

    fun savePrintName(context: Context, printName: String) {
        val shared = context.getSharedPreferences("system", Context.MODE_PRIVATE)
        val editor = shared.edit()
        editor.putString("printName", printName)
        editor.apply()
    }

    fun getPrintName(context: Context): String {
        val shared = context.getSharedPreferences("system", Context.MODE_PRIVATE)
        return shared.getString("printName", "")
    }

    fun getPaperWidth(context: Context): Int {
        val shared = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        return shared.getString("print_paper_width",context.resources.getString(R.string.settings_print_paper_width)).toInt()
    }
    fun getPaperHeight(context: Context): Int {
        val shared = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        return shared.getString("print_paper_height",context.resources.getString(R.string.settings_print_paper_height)).toInt()
    }

    fun saveLabelPrintMAC(context: Context, labelPrintMac: String) {
        val shared = context.getSharedPreferences("system", Context.MODE_PRIVATE)
        val editor = shared.edit()
        editor.putString("labelPrintMac", labelPrintMac)
        editor.apply()
    }

    fun getLabelPrintMAC(context: Context): String {
        val shared = context.getSharedPreferences("system", Context.MODE_PRIVATE)
        return shared.getString("labelPrintMac", "")
    }

    fun saveUpdateCheckTime(context: Context, Time: Long) {
        val shared = context.getSharedPreferences("system", Context.MODE_PRIVATE)
        val editor = shared.edit()
        editor.putLong("updateCheckTime", Time)
        editor.apply()
    }

    fun getUpdateCheckTime(context: Context): Long {
        val shared = context.getSharedPreferences("system", Context.MODE_PRIVATE)
        return shared.getLong("updateCheckTime", 0)
    }
    fun saveDistributionOrderCheckTime(context: Context, Time: Long) {
        val shared = context.getSharedPreferences("system", Context.MODE_PRIVATE)
        val editor = shared.edit()
        editor.putLong("DistributionOrderCheckTime", Time)
        editor.apply()
    }

    fun getDistributionOrderCheckTime(context: Context): Long {
        val shared = context.getSharedPreferences("system", Context.MODE_PRIVATE)
        return shared.getLong("DistributionOrderCheckTime", 0)
    }

}
