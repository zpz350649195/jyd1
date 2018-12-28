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
import com.mes.jyd.adapter.check.ProductInspectCheckAdapter
import com.mes.jyd.adapter.check.RejectManageDetailAdapter
import com.mes.jyd.base.BaseActivity
import com.mes.jyd.delegate.ParaSave
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

class RejectManageDetailActivity:BaseActivity(){
    private lateinit var vm: RejectManageDetailViewModel
    private  lateinit var vw: RejectManageDetailActivity
    lateinit var listAdapter: RejectManageDetailAdapter

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

    var titletext="质量确认"
    var countcheck=0 //已检验数量
    var count=0 //总数


     var dialog: DialogInterface?=null
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
        vm = RejectManageDetailViewModel(this, this)
        vw=this
        listAdapter= RejectManageDetailAdapter(vm)

       vm.getarr()
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
               verticalLayout {
                   isFocusable = true
                   isFocusableInTouchMode = true
                   isCancelable = false
                   toolbar {
                       lparams(width = matchParent, height = wrapContent)
                       backgroundColor = ContextCompat.getColor(ctx, R.color.colorAccent)
                       title = "提交界面"

                       navigationIconResource=R.drawable.ic_arrow_back_black_24dp
                       setNavigationOnClickListener {
                           dialog!!.cancel()
                       }
                   }
                   verticalLayout {
                       linearLayout {
                           orientation = LinearLayout.VERTICAL
                           textView {
                               text="质量决策："
                           }
                           sr = spinner{
                               adapter = srAdapter
                               setSelection(chooseIndex)
                               onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                                   override fun onItemSelected(
                                       parent: AdapterView<*>?,
                                       view: View?,
                                       position: Int,
                                       id: Long
                                   ) {
                                       chooseIndex = position
                                       // txthint.setText(position.toString())
                                   }

                                   override fun onNothingSelected(parent: AdapterView<*>?) {
                                   }
                               }
                           }.lparams {
                               width = matchParent
                               verticalMargin=dip(5)
                           }

                           txtValue = textInputEditText {
                               hint = "备注"
                               inputType = InputType.TYPE_CLASS_TEXT
                               // setText()
                               singleLine = false
                           }

                       }.lparams {
                           width = matchParent
                           height= wrapContent
                           topMargin = dip(16)
                       }

                       relativeLayout {

                           button {
                               text = "提交"
                               onClick {
                                   vm.rejectmanage(txtValue.text.toString())
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