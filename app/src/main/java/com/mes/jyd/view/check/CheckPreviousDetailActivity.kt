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
import com.mes.jyd.adapter.check.CheckPreviousDetailAdapter
import com.mes.jyd.adapter.check.ProductInspectCheckAdapter
import com.mes.jyd.adapter.check.RejectManageDetailAdapter
import com.mes.jyd.base.BaseActivity
import com.mes.jyd.delegate.ParaSave
import com.mes.jyd.viewModel.check.CheckPreviousDetailViewModel
import com.mes.jyd.viewModel.check.ProductInspectCheckViewModel
import com.mes.jyd.viewModel.check.RejectManageDetailViewModel
import kotlinx.android.synthetic.main.activity_main.view.*
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.coroutines.onMenuItemClick
import org.jetbrains.anko.appcompat.v7.navigationIconResource
import org.jetbrains.anko.appcompat.v7.toolbar
import org.jetbrains.anko.design.coordinatorLayout
import org.jetbrains.anko.design.textInputEditText
import org.jetbrains.anko.design.textInputLayout
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.sdk25.coroutines.onFocusChange
import org.jetbrains.anko.sdk25.coroutines.onScrollListener
import org.jetbrains.anko.support.v4.swipeRefreshLayout
import org.json.JSONArray

class CheckPreviousDetailActivity:BaseActivity(){
    private lateinit var vm: CheckPreviousDetailViewModel
    private  lateinit var vw: CheckPreviousDetailActivity
    lateinit var listAdapter: CheckPreviousDetailAdapter

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
    lateinit var  layoutvalue:LinearLayout
    lateinit var  layoutcheck:LinearLayout

    var checkid:Int=0 //检测项主键
    var position:Int=-1 //本次检验的位置
    var ifchange=true //是否重新加载 如果重新加载默认选中第一条
    lateinit var btnSubmit:Button //提交按钮

    var userid=0
    var id=0

    var titletext="质量判定"
    var countcheck=0 //已检验数量
    var count=0 //总数


     var dialog: DialogInterface?=null
    var dialog1: DialogInterface?=null
    lateinit var sr: Spinner
    var name = ""
    lateinit var srAdapter: ArrayAdapter<String>
    lateinit var srString:ArrayList<String>
    lateinit var srArr:JSONArray
    var chooseIndex=-1//下拉选择选中的索引


    override fun onCreate(savedInstanceState: Bundle?) {
        userid= ParaSave.getUserId(this).toInt()
        id=intent.getIntExtra("id",0)
        super.onCreate(savedInstanceState)
    }


    override fun initParams(args: Bundle?) {
        vm = CheckPreviousDetailViewModel(this, this)
        vw=this
        listAdapter= CheckPreviousDetailAdapter(vm)

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

                inflateMenu(R.menu.oneout)
                var  menuItem=menu.findItem(R.id.menu_out_one)
                menuItem.title="提交"
                onMenuItemClick {
                        item->
                    when(item!!.itemId){
                        R.id.menu_out_one->{
                            //提交事件
                            alerrtDialog()
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
                    height= matchParent
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

    fun alerrtDialog(){
       dialog= alert{
           customView {
               okButton {
                 vm.submitarr()
               }
               cancelButton {
                   vw.showTextToast("用户取消操作")
                   dialog!!.cancel()
               }
               verticalLayout {
                   isFocusable = true
                   isFocusableInTouchMode = true
                   isCancelable = false
                   toolbar {
                       lparams(width = matchParent, height = wrapContent)
                       backgroundColor = ContextCompat.getColor(ctx, R.color.colorAccent)
                       title = "是否排查"

                       navigationIconResource=R.drawable.ic_arrow_back_black_24dp
                       setNavigationOnClickListener {
                           dialog!!.cancel()
                       }
                   }
                   verticalLayout {
                       linearLayout {
                           orientation = LinearLayout.VERTICAL
                           textView {
                               text="确定提交？"
                           }

                       }.lparams {
                           width = matchParent
                           height= wrapContent
                           topMargin = dip(16)
                       }
                   }
               }
           }
           onCancelled {

           }
        }.show()
    }



    fun alerrtDialog1(){
        dialog1= alert{
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