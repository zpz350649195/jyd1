package com.mes.jyd.view

import android.content.DialogInterface
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.TextInputLayout
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.Toolbar
import android.text.InputType
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.mes.jyd.R
import com.mes.jyd.adapter.ProductCheckAdapter
import com.mes.jyd.base.BaseActivity
import com.mes.jyd.viewModel.ProductCheckViewModel
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.toolbar
import org.jetbrains.anko.custom.style
import org.jetbrains.anko.design.coordinatorLayout
import org.jetbrains.anko.design.textInputEditText
import org.jetbrains.anko.design.textInputLayout
import org.jetbrains.anko.sdk25.coroutines.onCheckedChange
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.sdk25.coroutines.onFocusChange
import org.jetbrains.anko.sdk25.coroutines.onScrollListener
import org.jetbrains.anko.support.v4.swipeRefreshLayout
import java.util.*

class ProductCheckActivity:BaseActivity(){
    private lateinit var vm: ProductCheckViewModel
    private  lateinit var vw:ProductCheckActivity
    lateinit var listAdapter: ProductCheckAdapter

    lateinit var refreshLayout: SwipeRefreshLayout
    lateinit var mainLayout: CoordinatorLayout
     lateinit var toolbar: Toolbar

    lateinit var layoutCheck:LinearLayout
    lateinit var listView:ListView

    //底部对象
    lateinit var txtStandVaue:TextView //标准值
    lateinit var txtValue: EditText //检测值
    lateinit var checkValue:CheckBox
    lateinit var  _textInputLayout:TextInputLayout

     var checkid:Int=0 //检测项主键
    var position:Int=-1 //本次检验的位置
    var ifchange=true //是否重新加载 如果重新加载默认选中第一条
    lateinit var btnSubmit:Button //提交按钮

    var userid=0
    var pnid=0

    var titletext="生产过程检验"
    var countcheck=0 //已检验数量
    var count=0 //总数


    lateinit var scanAlert: DialogInterface
    var name = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        userid=intent.getIntExtra("userid",0)
        pnid=intent.getIntExtra("pnid",0)
        super.onCreate(savedInstanceState)
    }


    override fun initParams(args: Bundle?) {
        vm = ProductCheckViewModel(this, this)
        vw=this
        listAdapter= ProductCheckAdapter(vm)

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

                        txtStandVaue=textView{
                            text="标准值"
                            textSize=18f
                        }.lparams(width = matchParent){
                            height=dip(30)
                        }

                        _textInputLayout= textInputLayout {
                            txtValue = textInputEditText {
                                   hint = "实际值"
                                  inputType=InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_CLASS_NUMBER
                               // setText()
                                singleLine = true

                                onClick {
                                    HideKeyboard(vw)
                                }

                                onFocusChange {
                                        v, hasFocus
                                    ->
                                    if(hasFocus)
                                        HideKeyboard(vw)
                                }
                            }
                            visibility=View.GONE
                        }.lparams(width = matchParent){
                            height=dip(50)
                        }

                        checkValue= checkBox {
                            text="是否合格"
                            width=dip(50)

                            height=dip(50)
                            singleLine=true
                          //  setChecked(false)


                            buttonDrawableResource=R.xml.checkbox_style
                           // setChecked(false)
                            /*onCheckedChange { buttonView, isChecked ->
                                if(isChecked)
                                {
                                    txtStandVaue.text="取消"
                                }else
                                    txtStandVaue.text="选中"

                            }*/

                          //  setButtonDrawable(resources.getDrawable(R.xml.checkbox_style))
                           // setTypeface(Typeface.DEFAULT,R.style.MyCheckBox)
                           // R.style.MyCheckBox
                        }.lparams(width = matchParent){
                            height=dip(50)
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







}