package com.mes.jyd.view.io

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
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.mes.jyd.R
import com.mes.jyd.adapter.io.InStockDetailAdapter
import com.mes.jyd.adapter.product.ProductCheckAdapter
import com.mes.jyd.adapter.product.ProductInspectCheckAdapter
import com.mes.jyd.base.BaseActivity
import com.mes.jyd.delegate.ParaSave
import com.mes.jyd.entity.CheckItem
import com.mes.jyd.util.general
import com.mes.jyd.view.product.ProductInspectCheckActivity
import com.mes.jyd.viewModel.io.InStockDetailViewModel
import com.mes.jyd.viewModel.product.ProductCheckViewModel
import com.mes.jyd.viewModel.product.ProductInspectCheckViewModel
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.coroutines.onMenuItemClick
import org.jetbrains.anko.appcompat.v7.toolbar
import org.jetbrains.anko.design.coordinatorLayout
import org.jetbrains.anko.design.textInputEditText
import org.jetbrains.anko.design.textInputLayout
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.sdk25.coroutines.onFocusChange
import org.jetbrains.anko.sdk25.coroutines.onScrollListener
import org.jetbrains.anko.support.v4.swipeRefreshLayout
import org.json.JSONObject

class InStockDetailActivity:BaseActivity(){
    private lateinit var vm: InStockDetailViewModel
    private  lateinit var vw: InStockDetailActivity
    lateinit var listAdapter: InStockDetailAdapter

    lateinit var refreshLayout: SwipeRefreshLayout
    lateinit var mainLayout: CoordinatorLayout
    lateinit var toolbar: Toolbar

    lateinit var layoutCheck:LinearLayout
    lateinit var listView:ListView
    //底部对象
    lateinit var txtValue: EditText //检测值
    lateinit var checkValue:CheckBox

    var checkid:Int=0 //检测项主键
    var position:Int=-1 //本次检验的位置
    var ifchange=true //是否重新加载 如果重新加载默认选中第一条
    lateinit var btnSubmit:Button //提交按钮

    var userid=0
    var id=0

    var titletext="入库"
    var countcheck=0 //已检验数量
    var count=0 //总数
    var bc=""


     var dialog: DialogInterface?=null
    var dialoglgort: DialogInterface?=null
    lateinit var txtlgort: EditText //库位
    var name = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        userid= ParaSave.getUserId(this).toInt()
        id=intent.getIntExtra("id",0)
        bc=intent.getStringExtra("bc")
        super.onCreate(savedInstanceState)
    }


    override fun initParams(args: Bundle?) {
        vm = InStockDetailViewModel(this, this)
        vw=this
        listAdapter= InStockDetailAdapter(vm)


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
                inflateMenu(R.menu.oneout)
              var  menuItem=menu.findItem(R.id.menu_out_one)
                menuItem.title="提交"
                onMenuItemClick {
                    item->
                    when(item!!.itemId){
                        R.id.menu_out_one->{
                            //提交事件
                            alerrtDialoglgort()
                        }
                    }
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
                           // vm.getdata()
                            vw.refreshLayout.isRefreshing=false
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

    fun alerrtDialog(position:Int,item:JSONObject){
       dialog= alert{
           customView {
               verticalLayout {
                   isFocusable = true
                   isFocusableInTouchMode = true
                   isCancelable = false
                   toolbar {
                       lparams(width = matchParent, height = wrapContent)
                       backgroundColor = ContextCompat.getColor(ctx, R.color.colorAccent)
                       title = "修改"
                   }

                   verticalLayout {
                       linearLayout {
                           orientation=LinearLayout.VERTICAL

                           textView {
                               text = "待入库数量："+general.getString(item, "allnum")
                               textSize = 15f
                               typeface = Typeface.create("Roboto-medium", Typeface.NORMAL)
                           }

                           textInputLayout {
                               txtValue = textInputEditText {
                                   hint = "入库数量"
                                   inputType=InputType.TYPE_CLASS_NUMBER
                                   // setText()
                                   singleLine = true
                                   setText(item.getString("num"))

                                   /*onClick {
                                       HideKeyboard(vw)
                                   }

                                   onFocusChange {
                                           v, hasFocus
                                       ->
                                       if(hasFocus)
                                           HideKeyboard(vw)
                                   }*/
                               }
                           }.lparams {
                               width= matchParent
                           }
                       }.lparams {
                           width = matchParent
                           height = dip(100)
                           topMargin = dip(16)
                       }

                       relativeLayout {
                           button {
                               text = "取消"
                               onClick {
                                   dialog!!.cancel()
                               }
                           }

                           button {
                               text = "确定"
                               onClick {
                                   var num=txtValue.text.toString().toInt()
                                   var allnum=item.getInt("allnum")
                                   if(num<=0){
                                       Toast.makeText(vw,"入库数量必须大于0",Toast.LENGTH_SHORT).show()
                                       return@onClick
                                   }else if(num>allnum){
                                       Toast.makeText(vw,"入库数量不能大于待入库数量",Toast.LENGTH_SHORT).show()
                                       return@onClick
                                   }
                                    item.put("num",txtValue.text.toString())
                                    //重新熏染并定位
                                   listAdapter.rebuild()
                                   refreshLayout.isRefreshing=false
                                   listView.setSelection(position)
                                   dialog!!.cancel()
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


    fun alerrtDialoglgort(){
        dialoglgort= alert{
            customView {
                verticalLayout {
                    isFocusable = true
                    isFocusableInTouchMode = true
                    isCancelable = false
                    toolbar {
                        lparams(width = matchParent, height = wrapContent)
                        backgroundColor = ContextCompat.getColor(ctx, R.color.colorAccent)
                        title = "提交"
                    }

                    verticalLayout {
                        linearLayout {
                            orientation=LinearLayout.VERTICAL

                            textInputLayout {
                                txtlgort = textInputEditText {
                                    hint = "库位"
                                   // inputType=InputType.TYPE_CLASS_TEXT
                                    // setText()
                                    singleLine = true

                                }
                            }.lparams {
                                width= matchParent
                            }
                        }.lparams {
                            width = matchParent
                            height = dip(100)
                            topMargin = dip(16)
                        }

                        relativeLayout {
                            button {
                                text = "取消"
                                onClick {
                                    dialog!!.cancel()
                                }
                            }

                            button {
                                text = "确定"
                                onClick {
                                    var _lgort=txtlgort.text.toString()
                                    if(_lgort==""){
                                        Toast.makeText(vw,"库位不能为空",Toast.LENGTH_SHORT).show()
                                        return@onClick
                                    }

                                    vm.submitarr(_lgort)
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