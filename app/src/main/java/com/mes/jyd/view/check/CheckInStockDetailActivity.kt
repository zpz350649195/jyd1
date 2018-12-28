package com.mes.jyd.view.check

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.TextInputLayout
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.Toolbar
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.widget.*
import com.mes.jyd.R
import com.mes.jyd.adapter.check.CheckInStockDetailAdapter
import com.mes.jyd.adapter.check.ProductInspectCheckAdapter
import com.mes.jyd.base.BaseActivity
import com.mes.jyd.delegate.ParaSave
import com.mes.jyd.viewModel.check.CheckInStockDetailViewModel
import com.mes.jyd.viewModel.check.ProductInspectCheckViewModel
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.toolbar
import org.jetbrains.anko.design.coordinatorLayout
import org.jetbrains.anko.design.textInputEditText
import org.jetbrains.anko.design.textInputLayout
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.sdk25.coroutines.onFocusChange
import org.jetbrains.anko.sdk25.coroutines.onScrollListener
import org.jetbrains.anko.support.v4.swipeRefreshLayout

class CheckInStockDetailActivity:BaseActivity(){
    private lateinit var vm: CheckInStockDetailViewModel
    private  lateinit var vw: CheckInStockDetailActivity
    lateinit var listAdapter: CheckInStockDetailAdapter

    lateinit var refreshLayout: SwipeRefreshLayout
    lateinit var mainLayout: CoordinatorLayout
     lateinit var toolbar: Toolbar

    lateinit var layoutCheck:LinearLayout
    lateinit var listView:ListView

    //底部对象
    lateinit var txtStandVaue:TextView //标准值
    lateinit var txtValue: EditText //检测值
    lateinit var checkValue:CheckBox
    lateinit var checkValue1:CheckBox //是否排查
    lateinit var  _textInputLayout:TextInputLayout
    lateinit var  layoutvalue:LinearLayout
    lateinit var  layoutcheck:LinearLayout

    var checkid:Int=0 //检测项主键
    var position:Int=-1 //本次检验的位置
    var ifchange=true //是否重新加载 如果重新加载默认选中第一条
    lateinit var btnSubmit:Button //提交按钮

    var userid=0
    var id=0

    var titletext="入库检验"
    var countcheck=0 //已检验数量
    var count=0 //总数


     var dialog: DialogInterface?=null
    var name = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        userid= ParaSave.getUserId(this).toInt()
        id=intent.getIntExtra("id",0)
        super.onCreate(savedInstanceState)
    }


    override fun initParams(args: Bundle?) {
        vm = CheckInStockDetailViewModel(this, this)
        vw=this
        listAdapter= CheckInStockDetailAdapter(vm)

        /*var bundle=intent.getBundleExtra("bb")
        var arrlist=bundle.getSerializable("list") as ArrayList<CheckItem>
        var json=arrlist[0]
        Toast.makeText(this,json.name,Toast.LENGTH_SHORT).show()*/

    }

    override fun initView() {
        val list = vm.list
        mainLayout=coordinatorLayout {
            toolbar=toolbar {
                title = titletext
                backgroundColorResource = R.color.colorPrimary

                elevation = 8f

                setNavigationIcon(R.drawable.ic_arrow_back_black_24dp)
                setNavigationOnClickListener {
                    finish()
                }

                /*inflateMenu(R.menu.oneout)

                onMenuItemClick {
                    item->
                    when(item!!.itemId){
                        R.id.menu_out_one->{
                            //提交事件
                        }
                    }
                }*/
            }.lparams(width = matchParent)
            //主框架开始
            linearLayout {
                orientation= LinearLayout.VERTICAL
                layoutCheck=  linearLayout {
                    refreshLayout = swipeRefreshLayout {

                        setProgressBackgroundColorSchemeResource(android.R.color.white)
                        setColorSchemeResources(
                            android.R.color.holo_blue_light,
                            android.R.color.holo_red_light,
                            android.R.color.holo_orange_light
                        )

                        listView = listView {
                            adapter = listAdapter
                            dividerHeight = 0

                            
                            addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
                                if(ifchange) {
                                    if (vw.listAdapter.linear != null) {
                                        vw.listAdapter.linear!!.callOnClick()
                                        vw.ifchange=false
                                    }
                                }
                                /*else{

                                }*/
                            }

                            onScrollListener {
                                /*onScroll { absListView, i, i, i ->

                                }*/
                                onScrollStateChanged { absListView, i ->
                                    if(i==AbsListView.OnScrollListener.SCROLL_STATE_IDLE){
                                        if (vw.listAdapter.linear != null) {
                                            vw.listAdapter.linear!!.backgroundColor=Color.argb(100,125,125,125)
                                        }
                                    }
                                }
                            }



                        }

                        setOnRefreshListener {
                            //刷新检验项
                            vm.getdata()
                        }
                    }.lparams {
                        width = matchParent
                        height = matchParent
                        topMargin = dip(55)
                        minimumHeight=dip(200)
                        //    bottom=dip(100)

                    }

                }.lparams{
                    width = matchParent
                    height=dip(250)
                    weight=1.0f
                    /*leftPadding=dip(5)
                    rightPadding=dip(5)*/

                }
                //主框架结束
                //底部提示信息开始
                linearLayout {
                    orientation = LinearLayout.HORIZONTAL


                    linearLayout{
                        orientation=LinearLayout.VERTICAL

                        layoutvalue=linearLayout {
                            orientation = LinearLayout.VERTICAL
                           // visibility = View.GONE
                            txtStandVaue = textView {
                                text = "标准值"
                                textSize = 18f
                            }.lparams(width = matchParent) {
                                height = dip(30)
                            }

                            _textInputLayout = textInputLayout {
                                txtValue = textInputEditText {
                                    hint = "实际值"
                                    inputType = InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_CLASS_NUMBER
                                    // setText()
                                    singleLine = true

                                    /*onClick {
                                        HideKeyboard(vw)
                                    }

                                    onFocusChange { v, hasFocus
                                        ->
                                        if (hasFocus)
                                            HideKeyboard(vw)
                                    }*/
                                }
                            }.lparams(width = matchParent) {
                                height = dip(50)
                            }
                        }.lparams{
                            width= matchParent
                            height=dip(80)
                        }

                        layoutcheck =  linearLayout {
                            orientation=LinearLayout.VERTICAL
                            gravity=Gravity.CENTER_VERTICAL
                          //  visibility=View.GONE
                            linearLayout {
                                orientation = LinearLayout.HORIZONTAL

                                textView {
                                    text = "合格:"
                                }

                                checkValue = checkBox {
                                    text = ""
                                    width = dip(50)
                                    height = dip(50)
                                    singleLine = true
                                    buttonDrawableResource = R.xml.checkbox_style
                                }.lparams(width = matchParent) {
                                    height = dip(50)
                                }

                            }.lparams{
                                width= matchParent
                                height=dip(50)
                            }
                        }.lparams{
                            width= matchParent
                            height=dip(80)
                        }


                    }.lparams{
                        height= matchParent
                        weight=4f
                    }
                    //按钮
                    linearLayout{
                        orientation=LinearLayout.VERTICAL
                        button {
                            text="提交"
                            onClick {
                                /*listAdapter.linear!!.setVisibility(View.GONE)
                                listAdapter.linear1!!.callOnClick()*/
                                vm.savecheck()

                            }
                        }.lparams {
                            margin = dip(3)
                            padding=dip(3)
                            height = dip(60)
                            height = dip(60)
                        }

                    }.lparams{
                        height= matchParent
                    }

                }.lparams(width= matchParent,height = dip(80)){
                    backgroundColor= Color.rgb(128,128,200)
                     topMargin=dip(2)
                }
                //底部提示信息结束
            }.lparams(width= matchParent,height = matchParent){

            }
        }
    }

    override fun doBusiness() {
       vm.getdata()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun finish() {
        var _intent= Intent()
        setResult(Activity.RESULT_OK,_intent)

        super.finish()
    }

    fun alerrtDialog(){
       dialog= alert{
           customView {
               verticalLayout {
                   isFocusable = true
                   isFocusableInTouchMode = true
                   isCancelable = false
                   toolbar {
                       lparams(width = matchParent, height = wrapContent)
                       backgroundColor = ContextCompat.getColor(ctx, R.color.colorAccent)
                       title = "确认框"
                   }
                   // backgroundColor=Color.rgb(128,128,128)
                   /*textView {
                       text = "确认框"
                       textSize = 21f
                       textColor = R.color.colorAccent
                       typeface = Typeface.create("Roboto-medium", Typeface.NORMAL)

                       gravity = Gravity.START
                   }.lparams(width = matchParent) {
                       topMargin = dip(16)
                       horizontalMargin = dip(16)
                       horizontalGravity = Gravity.START
                   }*/

                   verticalLayout {
                       linearLayout {
                           orientation = LinearLayout.HORIZONTAL
                           textView {
                               text = "是否排查" //tagObj.scanTag(12)
                               textSize = 21f
                               typeface = Typeface.create("Roboto-medium", Typeface.NORMAL)
                           }.lparams {
                               verticalMargin = dip(24)
                               verticalGravity = Gravity.CENTER
                           }

                           checkValue1=checkBox {
                               width = dip(50)
                               height = dip(50)
                               buttonDrawableResource = R.xml.checkbox_style
                           }.lparams {
                               height = dip(50)
                           }

                       }.lparams {
                           width = matchParent
                           height = dip(100)
                           topMargin = dip(16)
                       }

                       relativeLayout {

                           button {
                               text = "提交"
                               onClick {
                                    var ifcheck=checkValue1.isChecked
                                 //  vm.change()
                                   var isc=0
                                   if(ifcheck)
                                       isc=1
                                   vm.checks(isc)
                                   //dialog!!.cancel()
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

           }
        }.show()
    }





}