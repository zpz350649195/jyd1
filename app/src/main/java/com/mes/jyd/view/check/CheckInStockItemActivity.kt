package com.mes.jyd.view.check

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.Toolbar
import android.view.Gravity
import android.view.View
import android.view.ViewManager
import android.widget.*
import com.mes.jyd.delegate.ListView
import com.mes.jyd.R
import com.mes.jyd.adapter.check.CheckInStockItemAdapter
import com.mes.jyd.adapter.check.ProductInspectAdapter
import com.mes.jyd.base.scanActivity
import com.mes.jyd.delegate.ParaSave
import com.mes.jyd.entity.CheckItem
import com.mes.jyd.util.general
import com.mes.jyd.viewModel.check.CheckInStockItemViewModel
import com.mes.jyd.viewModel.check.ProductInspectViewModel
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.coroutines.onMenuItemClick
import org.jetbrains.anko.appcompat.v7.navigationIconResource
import org.jetbrains.anko.appcompat.v7.toolbar
import org.jetbrains.anko.custom.ankoView
import org.jetbrains.anko.design.coordinatorLayout
import org.jetbrains.anko.design.floatingActionButton
import org.jetbrains.anko.design.textInputEditText
import org.jetbrains.anko.design.textInputLayout
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.support.v4.swipeRefreshLayout
import org.json.JSONArray
import java.lang.Exception

class CheckInStockItemActivity:scanActivity() {
    //参数
    var _title="入库检验"
    lateinit var listadapter: CheckInStockItemAdapter
    lateinit var  vm: CheckInStockItemViewModel

    var userid:Int=-1 //用户Id
    var page:Int=0
    var count=0 //已加载条数
    var counts=0 //总条数
    //布局对象
    lateinit var _toolbar: Toolbar
    lateinit var _listlayout: LinearLayout
    lateinit var  _listview: ListView
    lateinit var  refreshLayout:SwipeRefreshLayout

    lateinit var itemAdd: DialogInterface
    lateinit var txtAddTitle: TextView
    lateinit var txtProduct: EditText //产品条码值
    lateinit var txthint:EditText //提示信息
    var bc="" //保存上次扫码的结果
    var _bc="" //上个页面传入的条码
    var id=0
    var  state=0 //状态 0 弹出框未打开 1 弹出框已打开

    var chooseid=0

    var isnew=0 //是否新增
    lateinit var _ctx:Context


    override fun showResult(barcode: String) {
        if(barcode==""){
            showTextToast( "条码不能为空")
            return
        }
        if(state==1){//弹出框
            txthint.setText(barcode)

        }
        bc=barcode
        //调用接口获取详细
        vm.getdetail(chooseid,isnew,bc)

    }

    override fun initParams(args: Bundle?) {
        //初始化参数
        vm= CheckInStockItemViewModel(this, application.ctx)
        listadapter= CheckInStockItemAdapter(vm)
        userid= ParaSave.getUserId(this).toInt()
        _bc=intent.getStringExtra("bc")
        id=intent.getIntExtra("id",0)
        _ctx=application.ctx
        ScanUtil(application.ctx)
    }

    override fun initView() {
        //页面初始化
        coordinatorLayout {
            _toolbar=toolbar {
                title=_title

                backgroundColorResource= R.color.colorPrimary
                setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
                setNavigationOnClickListener {
                    finish()
                }
                inflateMenu(R.menu.oneout)
                var  menuItem=menu.findItem(R.id.menu_out_one)
                menuItem.title="新增"
                onMenuItemClick {
                        item->
                    when(item!!.itemId){
                        R.id.menu_out_one->{
                            //提交事件
                            dialog(1)
                        }
                    }
                }
            }.lparams {
                width= matchParent
            }

            //详细内容开始
            linearLayout {
                orientation=LinearLayout.VERTICAL
                refreshLayout= swipeRefreshLayout {
                    setProgressBackgroundColorSchemeResource(android.R.color.white)
                    setColorSchemeResources(
                        android.R.color.holo_blue_light,
                        android.R.color.holo_red_light,
                        android.R.color.holo_orange_light
                    )

                    _listview=mlistView {
                        adapter=listadapter
                        dividerHeight = 0
                    }

                    setOnRefreshListener {
                        vm.getdata(0) //0 下拉刷新
                        refreshLayout.isRefreshing=false
                    }

                }.lparams {
                    width= matchParent
                    height= matchParent
                    topMargin=dip(55)
                }

            }.lparams {
                width= matchParent
                height= matchParent
            }



            //底部添加浮动按钮
            floatingActionButton {
                backgroundColor = ContextCompat.getColor(this@CheckInStockItemActivity, R.color.colorAccent)
                rippleColor= ContextCompat.getColor(this.context,R.color.colorAccent)
                imageResource=R.drawable.ic_scan_add
                onClick {
                   launchScannerActivity()
                }
            }.lparams{
                margin = dip(16)
                gravity = Gravity.BOTTOM or Gravity.END
            }
        }

        var onMoreTime = 0L
        _listview.addOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScrollStateChanged(view: AbsListView, scrollState: Int) {
                if (view.lastVisiblePosition == view.count - 1) {
                    //   vm.getdata()
                    // 原理：屏幕中最后显示的内个 item 是数据源位置的最后一个
                    // 那么就说明已经滑动到底部了
                    if (System.currentTimeMillis() - onMoreTime > 2500) {
                        //刷新按钮
                        try {
                            showloading()
                            vm.getdata(1) //1 加载更多
                            onMoreTime = System.currentTimeMillis()

                        }catch (e: Exception){

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

        vm.getdata(0)
    }
    override fun onResume() {
        super.onResume()
        open()
    }

    override fun onPause() {
        super.onPause()
        close()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==222&&resultCode==RESULT_OK && data!=null){
            page=0
            vm.getdata(0)
        }
    }

    fun dialog(tp:Int) {//tp: 0 检验 1 新增
        if(tp==0){
            isnew=0
        }else
            isnew=1

        itemAdd = alert {
            isCancelable=false
            customView {
                verticalLayout {
                    toolbar {
                        lparams(width = matchParent, height = wrapContent)
                        backgroundColor = ContextCompat.getColor(ctx, R.color.colorAccent)

                        if(tp==0){
                            title = "检验扫码"
                        }
                        else {
                            title = "新增"
                        }

                        navigationIconResource=R.drawable.ic_arrow_back_black_24dp
                        setNavigationOnClickListener {
                            itemAdd.cancel()
                        }
                    }
                    verticalLayout {

                        imageView {
                            imageResource = R.drawable.ic_scan
                            onClick {
                                launchScannerActivity()
                            }
                        }.lparams {
                            margin = dip(10)
                            padding = dip(3)
                            height = dip(60)
                            height = dip(60)
                            gravity = Gravity.CENTER
                        }

                        textView {
                            text = "请扫批次/序列号条码"
                            textSize = 21f
                            typeface = Typeface.create(
                                "Roboto-medium",
                                Typeface.NORMAL
                            )
                            gravity = Gravity.CENTER
                        }.lparams(width = matchParent) {
                            verticalMargin = dip(10)
                            horizontalGravity = Gravity.CENTER_HORIZONTAL
                        }

                        textInputLayout {
                            txthint = textInputEditText {
                                hint = "提示信息"
                                singleLine = true
                                isEnabled = false
                                setText(bc)
                            }
                        }.lparams(width = matchParent)
                    }
                }
            }
            onCancelled {
                //注销扫码服务
                //  close()
                state=0
                isnew=0
                chooseid=0
                bc=""
            }

        }.show()
        state=1
    }

}