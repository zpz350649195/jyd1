package com.mes.jyd.util

import android.os.Handler
import android.os.Message
import org.apache.commons.net.ftp.FTP
import org.apache.commons.net.ftp.FTPClient
import org.apache.commons.net.ftp.FTPReply
import org.json.JSONObject

import java.io.*

class FTPManager(var handler:Handler) {
    internal var ftpClient: FTPClient? = null

    init {
        ftpClient = FTPClient()
    }


    // 连接到ftp服务器
    @Synchronized
    @Throws(Exception::class)
    fun connect(_json:JSONObject): Boolean {
        var bool = false
        if (ftpClient!!.isConnected) {//判断是否已登陆
            ftpClient!!.disconnect()
        }
        ftpClient!!.setDataTimeout(20000)//设置连接超时时间
        ftpClient!!.controlEncoding = "utf-8"
        ftpClient!!.connect("10.11.28.179", 21)
        ftpClient!!.connect(_json.getString("ip"), _json.getInt("port"))
        if (FTPReply.isPositiveCompletion(ftpClient!!.replyCode)) {
            if (ftpClient!!.login(_json.getString("username"), _json.getString("password"))) {
               // if (ftpClient!!.login(administrator", "abcd.1234")) {
                bool = true
                System.out.println("ftp连接成功")
            }
        }
        return bool
    }

    // 创建文件夹
    @Throws(Exception::class)
    fun createDirectory(path: String): Boolean {
        var bool = false
        val directory = path.substring(0, path.lastIndexOf("/") + 1)
        var start = 0
        //var end = 0
        if (directory.startsWith("/")) {
            start = 1
        }
       var end = directory.indexOf("/", start)
        while (true) {
            val subDirectory = directory.substring(start, end)
            if (!ftpClient!!.changeWorkingDirectory(subDirectory)) {
                ftpClient!!.makeDirectory(subDirectory)
                ftpClient!!.changeWorkingDirectory(subDirectory)
                bool = true
            }
            start = end + 1
            end = directory.indexOf("/", start)
            if (end == -1) {
                break
            }
        }
        return bool
    }

    // 实现上传文件的功能
    @Synchronized
    @Throws(Exception::class)
    fun uploadFile(localPath: String, serverPath: String): Boolean {
        // 上传文件之前，先判断本地文件是否存在
        val localFile = File(localPath)
        if (!localFile.exists()) {
            System.out.println("本地文件不存在")
            return false
        }
        System.out.println("本地文件存在，名称为：" + localFile.name)
        createDirectory(serverPath) // 如果文件夹不存在，创建文件夹
        System.out.println("服务器文件存放路径：" + serverPath + localFile.name)
        val fileName = localFile.name
        // 如果本地文件存在，服务器文件也在，上传文件，这个方法中也包括了断点上传
        val localSize = localFile.length() // 本地文件的长度
        val files = ftpClient!!.listFiles(fileName)
        var serverSize: Long = 0
        if (files.size == 0) {
            System.out.println("服务器文件不存在")
            serverSize = 0
        } else {
            serverSize = files[0].size // 服务器文件的长度
        }
        if (localSize <= serverSize) {
            if (ftpClient!!.deleteFile(fileName)) {
                System.out.println("服务器文件存在,删除文件,开始重新上传")
                serverSize = 0
            }
        }
        val raf = RandomAccessFile(localFile, "r")
        // 进度
        val step = localSize / 1000
        var process: Long = 0
        var currentSize: Long = 0
        // 好了，正式开始上传文件
        ftpClient!!.enterLocalPassiveMode()
        ftpClient!!.setFileType(FTP.BINARY_FILE_TYPE)
        ftpClient!!.restartOffset = serverSize
        raf.seek(serverSize)
        val output = ftpClient!!.appendFileStream(fileName)
        val b = ByteArray(1024)
        var length:Int= 0
        length = raf.read(b)
        while (length != -1) {
            output.write(b, 0, length)
            currentSize = currentSize + length
            if (currentSize / step != process) {
                process = currentSize / step
                if (process % 10 == 0L) {
                    System.out.println("上传进度：$process")
                }
            }
            length = raf.read(b)
        }
        output.flush()
        output.close()
        raf.close()
        if (ftpClient!!.completePendingCommand()) {
            System.out.println("文件上传成功")
            return true
        } else {
            System.out.println("文件上传失败")
            return false
        }
    }

    // 实现下载文件功能，可实现断点下载
    @Synchronized
    @Throws(Exception::class)
    fun downloadFile(localPath: String, serverPath: String): Boolean {
        try {
            var localPath = localPath + "/"
            // 先判断服务器文件是否存在
            val files = ftpClient!!.listFiles(serverPath)
            if (files.size == 0) {
                System.out.println("服务器文件不存在")
                return false
            }
            System.out.println("远程文件存在,名字为：" + files[0].name)
            localPath = localPath + files[0].name
            // 接着判断下载的文件是否能断点下载
            val serverSize = files[0].size // 获取远程文件的长度
            val localFile = File(localPath)
            var localSize: Long = 0
            if (localFile.exists()) {
                localSize = localFile.length() // 如果本地文件存在，获取本地文件的长度
                if (localSize >= serverSize) {
                    System.out.println("文件已经下载完了")
                    val file = File(localPath)
                    file.delete()
                    System.out.println("本地文件存在，删除成功，开始重新下载")
                    return false
                }
            }
            // 进度
            val step = serverSize / 10000
            var process: Long = 0
            var currentSize: Long = 0
            // 开始准备下载文件
            ftpClient!!.enterLocalActiveMode()
            ftpClient!!.setFileType(FTP.BINARY_FILE_TYPE)
            val out = FileOutputStream(localFile, true)
            ftpClient!!.restartOffset = localSize
            val input = ftpClient!!.retrieveFileStream(serverPath)
            val b = ByteArray(1024)
            var length = 0
            length = input.read(b)
            while (length != -1) {
                out.write(b, 0, length)
                currentSize = currentSize + length
                if (currentSize / step != process) {
                    process = currentSize / step
                    if (process % 10 == 0L) {
                        val message = Message.obtain()
                        message.what = 2
                        message.obj=process/100f
                     //   _ctx.vm.handler.sendMessage(message)
                        handler.sendMessage(message)
                    }
                }
                length = input.read(b)
            }
            out.flush()
            out.close()
            input.close()
            // 此方法是来确保流处理完毕，如果没有此方法，可能会造成现程序死掉
            if (ftpClient!!.completePendingCommand()) {
                val message = Message.obtain()
                message.what = 1
                message.obj ="文件下载成功"
                handler.sendMessage(message)
                return true
            } else {
                val message = Message.obtain()
                message.what = 1
                message.obj ="文件下载失败"
                handler.sendMessage(message)
                return false
            }
        }catch (ex:Exception){
            logsaves().save(ex.message.toString())
            val message = Message.obtain()
            message.what = 1
            message.obj =ex.message.toString()
            handler.sendMessage(message)
            return false
        }
    }

    // 如果ftp上传打开，就关闭掉
    @Throws(Exception::class)
    fun closeFTP() {
        if (ftpClient!!.isConnected) {
            ftpClient!!.disconnect()
        }
    }

}
