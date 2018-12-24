package com.mes.jyd.view.product

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
import com.mes.jyd.adapter.product.ProductInspectAdapter
import com.mes.jyd.base.scanActivity
import com.mes.jyd.delegate.ParaSave
import com.mes.jyd.entity.CheckItem
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

class ProductInspectActivity:scanActivity() {
    //参数
    var _title="巡检"
    lateinit var listadapter: ProductInspectAdapter
    lateinit var  vm:ProductInspectViewModel

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
    var  state=0 //状态 0 弹出框未打开 1 弹出框已打开

    lateinit var sr: Spinner

    lateinit var srAdapter: ArrayAdapter<String>
    lateinit var srString:ArrayList<String>
    lateinit var srArr:JSONArray
     var chooseIndex=-1//下拉选择选中的索引

    lateinit var itemArr:ArrayList<CheckItem>

    lateinit var _ctx:Context

    lateinit var _bottomview:LinearLayout




    override fun showResult(barcode: String) {
        if(state==0){
            showAlert()
            txtProduct.setText(barcode)
        }else{
            txtProduct.setText(barcode)
        }

        //根据产品条码获取工序信息
        vm.gettech(barcode)
      //  sr.setSelection(0,true)


    }

    override fun initParams(args: Bundle?) {
        //初始化参数
        vm= ProductInspectViewModel(this,application.ctx)
        listadapter= ProductInspectAdapter(vm)
        userid= ParaSave.getUserId(this).toInt()
        _ctx=application.ctx
        ScanUtil(application.ctx)
        srString=ArrayList()
        srArr= JSONArray()
        srAdapter= ArrayAdapter<String>(this@ProductInspectActivity, android.R.layout.simple_spinner_dropdown_item, srString) //simple_spinner_dropdown_item

       // srAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
/*
        srArr= ArrayList()
        itemArr=ArrayList()
        var a=CheckItem("test","12")
        itemArr.add(a)
        a=CheckItem("test1","122")
        itemArr.add(a)*/

        /*var b=intent.extras
        var c=b.get("ss") as ArrayList<JSONObject>*/
     //   var a:ArrayList<JSONObject>=intent.getParcelableArrayListExtra("ss")
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



            //底部添加浮动按钮
            floatingActionButton {
                backgroundColor = ContextCompat.getColor(this@ProductInspectActivity, R.color.colorAccent)
                rippleColor= ContextCompat.getColor(this.context,R.color.colorAccent)
                imageResource=R.drawable.ic_add_24dp
                onClick {
                    vm.emptyspinner()
                    showAlert()
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

       /* _bottomview=linearLayout {
            textView {
                text="上拉加载更多数据"
            }.lparams {
                width= wrapContent
                height= wrapContent
            }
        }
        _listview.addFooterView(_bottomview)*/
        vm.getdata(0)
    }

    fun showAlert() {
        itemAdd = alert {
            isCancelable=false
            customView {
                verticalLayout {
                   /* backgroundColor = Color.rgb(200, 200, 200)
                    isFocusable = true
                    isFocusableInTouchMode = true
                    lparams {
                        verticalMargin = dip(8)
                    }
                    txtAddTitle = textView {
                        text = "请扫产品条码"
                        textSize = 21f
                        textColor = R.color.colorAccent
                        typeface = Typeface.create("Roboto-medium", Typeface.NORMAL)

                        backgroundColor = ContextCompat.getColor(ctx, R.color.colorAccent)
                        gravity = Gravity.START
                    }.lparams(width = matchParent) {
                        *//*topMargin = dip(4)
                        horizontalMargin = dip(16)*//*
                        leftPadding = dip(5)
                        topPadding = dip(5)
                        horizontalGravity = Gravity.START
                    }*/

                    toolbar {
                        lparams(width = matchParent, height = wrapContent)
                        backgroundColor = ContextCompat.getColor(ctx, R.color.colorAccent)
                        title = "请扫产品条码"
                    }
                    verticalLayout {
                        textInputLayout {
                            txtProduct = textInputEditText {
                                hint = "产品条码"
                                singleLine = true
                                isEnabled = false
                            }
                        }.lparams(width = matchParent)
                        textView {
                            text = "请选择工序:" //tagObj.scanTag(12)
                            textSize = 16f
                            gravity = Gravity.START
                        }.lparams(width = matchParent) {
                            horizontalGravity = Gravity.START
                        }

                        sr = spinner{
                            //  dropDownWidth=dip(200)
                            adapter = srAdapter
                           // dropDownVerticalOffset=
                            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                                override fun onItemSelected(
                                    parent: AdapterView<*>?,
                                    view: View?,
                                    position: Int,
                                    id: Long
                                ) {
                                    chooseIndex = position
                                    txthint.setText(position.toString())
                                }

                                override fun onNothingSelected(parent: AdapterView<*>?) {
                                }
                            }

                            /* onItemSelectedListener {

                        }*/
                        }.lparams {
                            width = matchParent

                        }


                        textInputLayout {
                            txthint = textInputEditText {
                                hint = "提示信息"
                                singleLine = true
                                isEnabled = false
                            }
                        }.lparams(width = matchParent)

                        relativeLayout {
                            button {
                                text = "返回"
                                onClick {
                                    itemAdd.cancel()
                                }
                            }.lparams {

                            }

                          /*  button {
                                text = "查询"
                                onClick {

                                }
                            }.lparams {
                                alignParentRight()
                            }*/

                            button {
                                text = "新增"
                                onClick {
                                    /*var _intent = Intent(ctx, ProductInspectCheckActivity::class.java)

                                    var bundle = Bundle()
                                    bundle.putSerializable("list", itemArr)
                                    _intent.putExtra("bb", bundle)
                                    startActivity(_intent)*/
                                    //获取数据
                                    vm.getadddata()
                                    //   vm.getCustomByNo(txtCustomNo.text.toString())
                                }
                            }.lparams {
                                alignParentRight()
                            }
                        }.lparams(width = matchParent) {
                            horizontalMargin = dip(16)
                            verticalMargin = dip(24)
                        }
                    }
                }
            }
            onCancelled {
                //注销扫码服务
              //  close()
                state=0
            }

        }.show()
        state=1
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