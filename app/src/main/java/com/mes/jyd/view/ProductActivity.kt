package com.mes.jyd.view

import android.content.DialogInterface
import android.content.Intent
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
import android.graphics.Color
import android.opengl.Visibility
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.Toolbar
import android.view.Display
import android.view.Gravity
import android.view.View
import android.view.ViewManager
import android.widget.AbsListView
import android.widget.LinearLayout
import android.widget.TextView
import com.mes.jyd.R
import com.mes.jyd.adapter.ProductAdapter
import com.mes.jyd.adapter.ProductTaskAdapter
import com.mes.jyd.base.BaseScanActivity
import com.mes.jyd.delegate.ListView
import com.mes.jyd.viewModel.ProductViewModel
import kotlinx.android.synthetic.main.activity_main.view.*
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.coroutines.onMenuItemClick
import org.jetbrains.anko.appcompat.v7.toolbar
import org.jetbrains.anko.custom.ankoView
import org.jetbrains.anko.design.bottomNavigationView
import org.jetbrains.anko.design.coordinatorLayout
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.sdk25.coroutines.onItemClick
import org.jetbrains.anko.sdk25.coroutines.onScrollListener
import org.jetbrains.anko.support.v4.swipeRefreshLayout
import org.w3c.dom.Text
import android.support.v4.content.ContextCompat.startActivity
import android.widget.EditText
import com.mes.jyd.base.scanActivity
import com.mes.jyd.delegate.ParaSave
import java.lang.Exception


class ProductActivity : scanActivity() {
    private lateinit var  vm: ProductViewModel
    //标题
    private val toolbarid = 0
    lateinit var  _toolbar: Toolbar

    lateinit var refreshLayout: SwipeRefreshLayout
    lateinit var listAdapter: ProductTaskAdapter
    lateinit var listView: ListView
    private lateinit var emptyView: TextView
    lateinit var  linearLayouttask: LinearLayout
    lateinit var  linearLayoutdetail: LinearLayout


    lateinit var refreshLayoutDetail: SwipeRefreshLayout
    lateinit var listAdapterdetail: ProductAdapter
    lateinit var listViewdetail: ListView

    lateinit var txtmsg:TextView
    lateinit var main:LinearLayout

    var page:Int=0//上次pdf翻到了第几页

    var isFront:Boolean=true

    //产线工位信息
    var ifchangeline:Boolean=false
    lateinit var dialogline: DialogInterface

    var lineid:Int=-1
    var linename:String=""
    var positionid:Int=0
    var positionname:String=""
    lateinit var _linetext:TextView
    lateinit var  _linevalue:EditText
    lateinit var _positiontext:TextView
    lateinit var  _positionvalue:EditText
    //条码信息
     var bc:String=""
     var userid:Int=-1 //用户Id
     var pageid:Int=-1 //当前所处页面视图 -1 无，0：生产计划视图  1 生产任务视图
     var taskpage:Int=1 //默认查询在第一页

    override fun onCreate(savedInstanceState: Bundle?) {
       // requestedOrientation=SCREEN_ORIENTATION_LANDSCAPE
        super.onCreate(savedInstanceState)
    }

    override fun showResult(barcode: String) {
       // txtmsg.text=barcode+"-"+isFront.toString()
        bc=barcode
        //触发扫码
        vm.productscan()
    }

    override fun initParams(args: Bundle?) {
        vm=ProductViewModel(this,this.ctx)
        listAdapter=ProductTaskAdapter(vm)
        listAdapterdetail=ProductAdapter(vm)
        ScanUtil(this.ctx)
        userid=ParaSave.getUserId(this).toInt()
    }

    override fun initView() {
        coordinatorLayout {
            //工具栏开始
            _toolbar=toolbar {
                id=toolbarid
                title="生产执行"

                backgroundColorResource= R.color.colorPrimary
                setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
                setNavigationOnClickListener {
                    finish()
                }
                //查询按钮
                inflateMenu(R.menu.product)
                onMenuItemClick { item->
                    when(item!!.itemId){
                        R.id.menu_paoduct_line->{
                            vm.changeline()
                        }
                        R.id.menu_paoduct_paper->{
                          //  val intent: Intent = Intent(ctx, PaperActivity::class.java).putExtra("type", 0).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                          /*  val intent: Intent = Intent(ctx, PaperActivity::class.java)
                            startActivity(ctx, intent, null)*/
                            val intent:Intent=Intent(ctx, PaperActivity::class.java)
                            intent.putExtra("filename","tt.pdf")
                            intent.putExtra("page",page)
                            startActivityForResult(intent,222)
                        }
                        R.id.menu_paoduct_error->{}
                    }
                }
                elevation=6f
                
            }.lparams{
                width= matchParent
            }
            //工具栏结束
            //主框架开始
            main=linearLayout {
                orientation=LinearLayout.VERTICAL
                linearLayouttask=  linearLayout {

                    refreshLayout = swipeRefreshLayout {
                        setProgressBackgroundColorSchemeResource(android.R.color.white)
                        setColorSchemeResources(
                            android.R.color.holo_blue_light,
                            android.R.color.holo_red_light,
                            android.R.color.holo_orange_light
                        )

                        listView = mlistView {
                            adapter = listAdapter
                            dividerHeight = 0
                        }

                        setOnRefreshListener {
                            if(pageid==0){
                               // refreshLayout.isRefreshing=false
                                vm.gettaskplan(positionid,1)
                            }
                            /*else
                                refreshLayout.isRefreshing=false*/
                         //   vm.changepage()
                        }
                    }.lparams {
                        width = matchParent
                        height = matchParent
                        topMargin = dip(50)
                        minimumHeight=dip(200)
                        //    bottom=dip(100)

                    }

                }.lparams{
                    width = matchParent
                    height=dip(250)
                   weight=1.0f

                }
                //详细开始
                linearLayoutdetail=  linearLayout {
                    visibility=View.GONE
                    refreshLayoutDetail = swipeRefreshLayout {
                        setProgressBackgroundColorSchemeResource(android.R.color.white)
                        setColorSchemeResources(
                            android.R.color.holo_blue_light,
                            android.R.color.holo_red_light,
                            android.R.color.holo_orange_light
                        )

                        listViewdetail = mlistView {
                            adapter = listAdapterdetail
                            dividerHeight = 0
                        }

                        setOnRefreshListener {
                            if(pageid==1){
                                vm.getdetail()
                            }
                        }




                    }.lparams {
                        width = matchParent
                        height = matchParent
                        topMargin = dip(50)
                        minimumHeight=dip(200)
                        //    bottom=dip(100)

                    }

                }.lparams{
                    width = matchParent
                    height=dip(250)
                    weight=1.0f
                }
                //主框架结束
                //底部提示信息开始
                linearLayout {
                    orientation = LinearLayout.VERTICAL
                    //gravity=Gravity.BOTTOM
                   /* button{
                        text="切换"
                        textSize=13f
                        onClick {
                            vm.showchange()
                        }
                    }.lparams(height = dip(30))*/

                    textView {
                        text = "提示信息"
                        textSize = 12f
                    }.lparams(height = dip(18))
                    txtmsg = textView {
                        text = "请扫描工位码"
                        textSize = 18f
                    }.lparams(height = dip(30))

                }.lparams(width= matchParent,height = dip(50)){
                    backgroundColor=Color.rgb(128,128,200)
                   // topMargin=dip(10)
                    margin=dip(10)

                }
                //底部提示信息结束
               /* emptyView = textView {
                    text = resources.getString(R.string.list_view_empty)
                    textSize = 30f
                    gravity = Gravity.CENTER
                }.lparams {
                    width = matchParent
                    height = matchParent
                    bottomMargin=dip(100)
                }*/
            }.lparams(width= matchParent,height = matchParent){

            }
         //   listView.emptyView = emptyView

        }
        var onMoreTime = 0L
        listView.addOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScrollStateChanged(view: AbsListView, scrollState: Int) {
                if (view.lastVisiblePosition == view.count - 1) {
                 //   vm.getdata()
                    // 原理：屏幕中最后显示的内个 item 是数据源位置的最后一个
                    // 那么就说明已经滑动到底部了
                    if (System.currentTimeMillis() - onMoreTime > 2500) {
                        //刷新按钮
                        try {
                            showloading()
                            if(pageid==0)
                               vm.gettaskplan(positionid,taskpage+1)
                            onMoreTime = System.currentTimeMillis()

                        }catch (e:Exception){

                        }finally {
                            dismissloading()
                        }
                    }
                }
            }

            override fun onScroll(view: AbsListView, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
                if (view.lastVisiblePosition == view.count - 1) {
                    // 和上面同理
                }
            }
        })
    }

    inline fun ViewManager.mlistView(init: com.mes.jyd.delegate.ListView.() -> Unit): com.mes.jyd.delegate.ListView {
        return ankoView({ com.mes.jyd.delegate.ListView(it) }, theme = 0, init = init)
    }

    override fun doBusiness() {
      //  vm.getdata()
        vm.changeline()
    }

    override fun onResume() {
        super.onResume()
       // scan()
        open()
        isFront=true

    }

    override fun onPause() {
        super.onPause()
        isFront=false
        close()
       // finish()
    }

    override fun ifscan(): Boolean {
        return isFront
    }

    override fun finish() {
        super.finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==222&&resultCode==RESULT_OK && data!=null)
            page=data.getIntExtra("page",0)

    }

}