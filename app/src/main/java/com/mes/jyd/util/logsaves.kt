package com.mes.jyd.util

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

class logsaves {
    lateinit var fileOutputStream: FileOutputStream
    lateinit var fileInputStream: FileInputStream
   var utils= com.mes.jyd.delegate.utils()
//    var folder="/hhs/"
   // var folder="/hhs"

    fun save(str1:String){

        var str=str1
        var folder="/"
        //获取文件名称
        var name=folder+utils.getDate()+".txt"
        //给保存字符添加换行
        //str=str+"\r\n"

        var _f= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

        try {
            fileOutputStream = FileOutputStream(_f.absolutePath+name,true)
            fileOutputStream.write(str.toByteArray())
            fileOutputStream.close()
        } catch (e: FileNotFoundException) {

            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }

    }

    fun getppr(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity.checkSelfPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            var arrs= arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            activity.requestPermissions(arrs, 1)
        }
    }
}