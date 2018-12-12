package com.mes.jyd.delegate

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat.startActivity
import android.support.v4.content.FileProvider
import android.text.TextUtils
import android.util.Log
import com.mes.jyd.util.logsaves
import java.io.File
import java.net.NetworkInterface
import java.util.*


/**
 *
 * Created by pandanxin on 2017/12/20.
 */
class AndroidUtil(val activity: Activity){
    fun isAppInstalled(uri:String):Boolean {
        val pm = activity.packageManager
        val installed: Boolean
        installed = try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES)
            true
        } catch(e:PackageManager.NameNotFoundException) {
            false
        }
        return installed
    }

    fun  installIntent( context: Context,path:String): Intent? {
        try {
            val file = File(path)
            val intent = Intent(Intent.ACTION_VIEW)

            //判读版本是否在7.0以上
            if (Build.VERSION.SDK_INT >= 24) {
                //provider authorities
                val apkUri = FileProvider.getUriForFile(context, "com.mes.fileprovider", file)
                //Granting Temporary Permissions to a URI
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                intent.setDataAndType(apkUri, "application/vnd.android.package-archive")
            } else {
                intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive")
            }
            return intent
        } catch ( e:Exception) {
            logsaves().save(e.message.toString())
            e.printStackTrace()
        }
        return null
    }

    @TargetApi(Build.VERSION_CODES.O)
    fun installApk(context: Context?, apkPath: String) {
        if (context == null || TextUtils.isEmpty(apkPath)) {
            return
        }
        logsaves().save(apkPath)

        val intent=installIntent(context,apkPath)

        if (Build.VERSION.SDK_INT >= 26) {
            val pm = activity.packageManager
            val b = pm.canRequestPackageInstalls()
            if (b) {
                if (intent != null) {
                    startActivity(context,intent,null)
                }
            } else {
                //请求安装未知应用来源的权限
                ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.REQUEST_INSTALL_PACKAGES), 0)
            }
        } else {
            logsaves().save(Build.VERSION.SDK_INT.toString())
            if (intent != null) {
                startActivity(context,intent,null)
            }
        }


    }

//    fun writeStreamToFile(stream: InputStream, file: File) {
//        try {
//            //
//            var output: OutputStream? = null
//            try {
//                output = FileOutputStream(file)
//            } catch (e1: FileNotFoundException) {
//                e1.printStackTrace()
//            }
//
//            try {
//                try {
//                    val buffer = ByteArray(1024)
//                    var read: Int
//                    read = stream.read(buffer)
//                    while (read != -1) {
//                        output!!.write(buffer, 0, read)
//                        read = stream.read(buffer)
//                    }
//                    output!!.flush()
//                } finally {
//                    output!!.close()
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//
//        } finally {
//            try {
//                stream.close()
//            } catch (e: IOException) {
//                e.printStackTrace()
//            }
//
//        }
//    }



    companion object {
        fun getMac(): String {
            try {
                val all = Collections.list(NetworkInterface.getNetworkInterfaces())
                for (nif in all) {
                    if (nif.name.toLowerCase() != "wlan0") continue

                    val macBytes = nif.hardwareAddress ?: return ""

                    val res1 = StringBuilder()
                    for (b in macBytes) {
                        res1.append(String.format("%02X:", b))
                    }

                    if (res1.isNotEmpty()) {
                        res1.deleteCharAt(res1.length - 1)
                    }
                    return res1.toString()
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }

            return ""
        }

        /**
         * 返回当前程序版本名
         */
        fun getAppVersionName(context: Context): String {
            var versionName = ""
            try {
                // ---get the package info---
                val pm = context.packageManager
                val pi = pm.getPackageInfo(context.packageName, 0)
                versionName = pi.versionName
                if (versionName == null || versionName.isEmpty()) {
                    return ""
                }
            } catch (e: Exception) {
                Log.e("VersionInfo", "Exception", e)
            }

            return versionName
        }
    }


}