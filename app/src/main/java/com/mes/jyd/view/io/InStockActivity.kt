package com.mes.jyd.view.io

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
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
import com.mes.jyd.adapter.io.InStockAdapter
import com.mes.jyd.adapter.product.ProductInspectAdapter
import com.mes.jyd.base.scanActivity
import com.mes.jyd.delegate.ParaSave
import com.mes.jyd.entity.CheckItem
import com.mes.jyd.viewModel.io.InStockViewModel
import com.mes.jyd.viewModel.product.ProductInspectViewModel
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.toolbar
import org.jetbrains.anko.custom.ankoView
import org.jetbrains.anko.design.coordinatorLayout
import org.jetbrains.anko.design.floatingActionButton
import org.jetbrains.anko.design.textInputEditText
import org.jetbrains.anko.design.textInputLayout
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.sdk25.coroutines.onItemSelectedListener
import org.jetbrains.anko.support.v4.swipeRefreshLayout
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception

class InStockActivity:scanActivity() {
    //参数
    var _title="待入库清单"
    lateinit var listadapter: InStockAdapter
    lateinit var  vm:InStockViewModel

    var userid:Int=-1 //用户Id
    var page:Int=0
    var count=0 //已加载条数
    var counts=0 //总条数
    //布局对象
    lateinit var _toolbar: Toolbar
    lateinit var _listlayout: LinearLayout
    lateinit var  _listview: ListView
    lateinit var  refreshLayout:SwipeRefreshLayout

    lateinit var txtProduct: EditText //产品条码值
    var  state=0 //状态 0 弹出框未打开 1 弹出框已打开

    lateinit var sr: Spinner

    lateinit var srAdapter: ArrayAdapter<String>
    lateinit var srString:ArrayList<String>
    lateinit var srArr:JSONArray
    lateinit var _ctx:Context
    var bc=""





    override fun showResult(barcode: String) {
        bc=barcode
        //判断条码是否正确

    }

    override fun initParams(args: Bundle?) {
        //初始化参数
        vm= InStockViewModel(this,application.ctx)
        listadapter= InStockAdapter(vm)
        userid= ParaSave.getUserId(this).toInt()
        _ctx=application.ctx
        ScanUtil(application.ctx)
        srString=ArrayList()
        srArr= JSONArray()
        srAdapter= ArrayAdapter<String>(this@InStockActivity, android.R.layout.simple_spinner_dropdown_item, srString) //simple_spinner_dropdown_item

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
            //page=data.getIntExtra("type",0) //需要刷新
    }

}