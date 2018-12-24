package com.mes.jyd.viewModel.product

import android.annotation.SuppressLint
import android.content.Context
import android.os.Environment
import android.os.Handler
import android.os.Message
import android.widget.Toast
import com.mes.jyd.delegate.NetworkUtil
import com.mes.jyd.util.logsaves
import com.mes.jyd.util.FTPManager
import com.mes.jyd.view.product.ProductPaperActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.pdf.*
import org.jetbrains.anko.toast
import org.json.JSONObject
import java.io.File
import java.lang.Exception

class ProductPaperViewModel(val vw: ProductPaperActivity, val ctx: Context) {
    var _f= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath
    var toast: Toast? = null

    fun getFtpMsg(type:String){
        //获取FTP服务器信息 tech:工艺文件 paper：图纸
        if(!NetworkUtil().isWifiConnected(ctx)){
            vw.toast("当前没有连接到 WIFI 网络")
            return
        }
        vw.apiService().getftpmsg(type)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { vw.showloading() }
            .doAfterTerminate { vw.dismissloading()}//请求完成，设置加载为false
            .subscribe(
                { t: JSONObject? ->
                    //context.toast(t?.toString()!!)
                    if (t?.getBoolean("success")!!) {
                        if(t!!.getInt("count")>0){
                            vw.iftrue=true
                            vw._json=   t.getJSONArray("data").getJSONObject(0)
                        }else{
                            vw.iftrue=false
                            ctx.toast("获取失败")
                        }

                    } else {
                        vw.iftrue=false
                        ctx.toast(t.getString("msg") ?: "error")
                    }
                }, { t: Throwable? ->
                      vw.iftrue=false
                    ctx.toast(t?.message ?: "error")
                }
            )

    }
    fun showTextToast(msg: String) {
        try {
            if (toast == null) {
                    toast = Toast.makeText(vw, msg, Toast.LENGTH_SHORT)
            } else {
                toast!!.setText(msg)
            }
            toast!!.show()
        }catch (e:Exception){
            // System.out.println(e.message.toString())
            logsaves().save(e.message.toString())
        }
    }

    /**
     * 把处理结果放回ui线程
     */
   private val  handler =@SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                1->{
                    showTextToast(msg.obj.toString())
                }
                2 -> {
                    val progress = msg.obj.toString().toFloat()
                    showTextToast("正在下载：$progress %")
                }


            }
        }
    }
    //FTP文件下载
    fun download(){
         if(!vw.iftrue){
             showTextToast("获取FTP服务器信息失败！")
             return
         }
         Thread( Runnable{
              run{
                  try {
                    //  System.out.println("正在连接ftp服务器....")
                      var ftpManager =FTPManager(handler)
                      if (ftpManager.connect(vw._json)) {

                         // var _f= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePathv
                          var path="/"
                          if(vw._json.getString("folder")!=""){
                              path+=vw._json.getString("folder")+"/"
                          }
                          path+=vw.filename

                        //  if (ftpManager.downloadFile(_f, "/paper/tt.pdf")) {
                          if (ftpManager.downloadFile(_f, path)) {
                              ftpManager.closeFTP()
                              loadpdf()
                          }
                      }else{
                          showTextToast("FTP服务器连接失败！")
                      }
                  } catch (e:Exception) {
                    //  System.out.println(e.message.toString())
                    //  logsaves().save(e.message.toString())
                      showTextToast(e.message.toString())
                      // TODO: handle exception
                      // System.out.println(e.getMessage());
                  }

            }
        }).start()
    }

    fun loadpdf() =//打开pdf文件
            try {
                val localFile = File(_f + "/" + vw.filename)
                vw._pdf.fromFile(localFile)   //设置pdf文件地址
                        .defaultPage(vw.page)         //设置默认显示第1页
                        /*.onPageChange(
                                object: OnPageChangeListener {
                            override fun onPageChanged(page: Int, pageCount: Int){
                                vw.page=page
                            }}
                        )*/
                        .onPageChange {
                            page, pageCount ->
                            vw.page=page
                            vw.papertoolbar.title="图纸查看("+(page+1).toString()+"/"+pageCount.toString()+")"
                        }
                        .swipeHorizontal(false)
                        /*  .onPageChange(this)     //设置翻页监听
                    .onLoad(this)           //设置加载监听
                    .onDraw(this)            //绘图监听*/
                        //  .showMinimap(false)     //pdf放大的时候，是否在屏幕的右上角生成小地图
                        // .swipeVertical(false)  //pdf文档翻页是否是垂直翻页，默认是左右滑动翻页
                        .enableSwipe(true)   //是否允许翻页，默认是允许翻页
                        // .pages( 2 , 3 , 4 , 5  )  //把2 , 3 , 4 , 5 过滤掉
                        .load()
            }catch (e:Exception){
                logsaves().save(e.message.toString())
            }

}