package com.mes.jyd.view.product

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.Toolbar
import android.text.InputType
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.ViewManager
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.mes.jyd.R
import com.mes.jyd.adapter.product.ProductDetailAdapter
import com.mes.jyd.adapter.product.ProductTaskAdapter
import com.mes.jyd.delegate.ListView
import com.mes.jyd.viewModel.product.ProductViewModel
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.coroutines.onMenuItemClick
import org.jetbrains.anko.appcompat.v7.toolbar
import org.jetbrains.anko.custom.ankoView
import org.jetbrains.anko.design.coordinatorLayout
import org.jetbrains.anko.support.v4.swipeRefreshLayout
import com.mes.jyd.base.scanActivity
import com.mes.jyd.delegate.ParaSave
import com.mes.jyd.util.general
import org.jetbrains.anko.appcompat.v7.navigationIconResource
import org.jetbrains.anko.design.textInputEditText
import org.jetbrains.anko.design.textInputLayout
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.sdk25.coroutines.onFocusChange
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
    lateinit var listAdapterdetail: ProductDetailAdapter
    lateinit var listViewdetail: ListView

    lateinit var empty:TextView

    lateinit var txtmsg:TextView
    lateinit var main:LinearLayout

    var page:Int=0//上次pdf翻到了第几页

    var isFront:Boolean=true


    //产线工位信息
    var ifchangeline:Boolean=false
    lateinit var dialogline: DialogInterface

    //报工填写数量
    lateinit var  dialognum:DialogInterface
    lateinit var  txtnum:EditText

    var isnum=0

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
        userid=ParaSave.getUserId(this).toInt()
        super.onCreate(savedInstanceState)
    }

    override fun showResult(barcode: String) {
       // txtmsg.text=barcode+"-"+isFront.toString()
        bc=barcode
        //触发扫码
        vm.productscan(0)
    }

    override fun initParams(args: Bundle?) {
        vm= ProductViewModel(this, this.ctx)
        listAdapter= ProductTaskAdapter(vm)
        listAdapterdetail= ProductDetailAdapter(vm)
        ScanUtil(this.ctx)
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
                            changeline()
                        }
                        R.id.menu_paoduct_paper->{
                          //  val intent: Intent = Intent(ctx, ProductPaperActivity::class.java).putExtra("type", 0).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                          /*  val intent: Intent = Intent(ctx, ProductPaperActivity::class.java)
                            startActivity(ctx, intent, null)*/
                            val intent:Intent=Intent(ctx, ProductPaperActivity::class.java)
                            intent.putExtra("filename","tt.pdf")
                            intent.putExtra("page",page)
                            startActivityForResult(intent,222)
                        }
                        R.id.menu_paoduct_check->{
                            val intent:Intent=Intent(ctx, ProductCheckActivity::class.java)
                            intent.putExtra("userid",userid)
                            intent.putExtra("pnid",positionid)
                            startActivity( intent, null)
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
                        topMargin = dip(60)
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
                        topMargin = dip(60)
                        minimumHeight=dip(200)
                        //    bottom=dip(100)

                    }

                }.lparams{
                    width = matchParent
                    height=dip(250)
                    weight=1.0f
                }

                //空视图
                empty=textView {
                    text="没有数据"

                }.lparams{
                    width = matchParent
                    height=dip(250)
                    weight=1.0f
                }
                //主框架结束
                //底部提示信息开始

                linearLayout {
                    orientation = LinearLayout.HORIZONTAL

                    linearLayout {
                        orientation = LinearLayout.VERTICAL

                       /* textView {
                            text = "提示信息"
                            textSize = 12f
                        }.lparams(height = dip(18))
                        txtmsg = textView {
                            text = "请扫描工位码"
                            textSize = 18f
                        }.lparams(height = dip(30))*/

                        var msg="请扫描工位码"

                        txtmsg=editText {
                            isFocusable=false
                            minLines=3
                            isEnabled=false
                            setText(msg)
                            gravity=Gravity.END
                        }.lparams {
                            height= wrapContent
                        }

                    }.lparams(width= matchParent,height = dip(80)){
                        backgroundColor=Color.rgb(115,230,115)
                        // topMargin=dip(10)
                        margin=dip(10)
                        weight=1.0f
                    }
                    //按钮
                    linearLayout{
                        orientation=LinearLayout.VERTICAL
                        imageView {
                            imageResource=R.drawable.ic_scan
                            onClick {
                                launchScannerActivity()
                            }
                        }.lparams {
                            margin = dip(3)
                            padding=dip(3)
                            height = dip(60)
                            height = dip(60)
                        }

                    }.lparams{
                        height= matchParent
                        weight=6.0f
                    }

                }.lparams(width= matchParent,height = dip(80)){
                    backgroundColor= Color.rgb(128,128,200)
                    topMargin=dip(2)
                }
                //底部提示信息结束
            }.lparams(width= matchParent,height = matchParent){

            }
         //   listView.emptyView = emptyView

        }

        listView.emptyView=empty
        listViewdetail.emptyView=empty

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
        changeline()
    }

    override fun onResume() {
        super.onResume()
       // scan()
        open()
        isFront=true
        //禁用键盘
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

    fun hidekey(){

        /*var manager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        manager.hideSoftInputFromWindow(
            this.currentFocus.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )*/
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==222&&resultCode==RESULT_OK && data!=null)
            page=data.getIntExtra("page",0)

    }


    fun changeline(){
        //更改产线
        ifchangeline=true
        dialogline = alert {
            customView {

                verticalLayout {

                    toolbar {
                        isFocusable=false
                        lparams(width = matchParent, height = wrapContent)
                        backgroundColor = ContextCompat.getColor(ctx, R.color.colorAccent)
                        title = "产线工位信息"

                        navigationIconResource=R.drawable.ic_arrow_back_black_24dp
                        setNavigationOnClickListener {
                            dialogline.cancel()
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
                            text = general.getScanTag(1)
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

                        _linetext = textView {
                            text = "产线" //tagObj.scanTag(12)
                            textSize = 15f
                            typeface = Typeface.create("Roboto-medium", Typeface.NORMAL)
                            gravity = Gravity.LEFT
                        }.lparams(width = matchParent) {

                            topMargin = dip(12)
                            horizontalGravity = Gravity.CENTER_HORIZONTAL
                        }

                        textInputLayout {
                            _linevalue = textInputEditText {
                                // hint = "产线"
                                isFocusable=false
                                setText(linename)
                                singleLine = false
                                isEnabled = false
                            }
                        }.lparams(width = matchParent)

                        _positiontext = textView {
                            text = "工位" //tagObj.scanTag(12)
                            textSize = 15f
                            typeface = Typeface.create("Roboto-medium", Typeface.NORMAL)
                            gravity = Gravity.LEFT
                        }.lparams(width = matchParent) {
                            topMargin = dip(12)
                            horizontalGravity = Gravity.CENTER_HORIZONTAL
                        }

                        textInputLayout {
                            _positionvalue = textInputEditText {
                                //   hint = "工位"
                                isFocusable=false
                                setText(positionname)
                                singleLine = false
                                isEnabled = false
                            }
                        }.lparams(width = matchParent)

                    }
                }
            }
            onCancelled {
                ifchangeline=false
            }

            onKeyPressed {
                    dialog, keyCode, e ->
                if(keyCode==132){
                    scan()
                }
                true
            }


        }.show()
    }


    fun writenum(){
        //更改产线
        isnum=1
        dialognum = alert {
            customView {
                isCancelable=false
                verticalLayout {

                    toolbar {
                        lparams(width = matchParent, height = wrapContent)
                        backgroundColor = ContextCompat.getColor(ctx, R.color.colorAccent)
                        title = "报工数量填写"

                        navigationIconResource=R.drawable.ic_arrow_back_black_24dp
                        setNavigationOnClickListener {
                            dialognum.cancel()
                        }
                    }

                    verticalLayout {

                        textView {
                            text = "数量"
                            textSize = 21f
                            typeface = Typeface.create(
                                "Roboto-medium",
                                Typeface.NORMAL
                            )
                        }.lparams(width = matchParent) {
                            verticalMargin = dip(10)
                        }

                        txtnum = textInputEditText {
                            singleLine = true
                            inputType=InputType.TYPE_CLASS_NUMBER

                        }.lparams {
                            width= matchParent
                        }
                        relativeLayout {
                            button {
                                text = "提交"
                                onClick {
                                    var num = txtnum.text.toString()
                                    if (num == "") {
                                        Toast.makeText(this@ProductActivity, "数量必须填写", Toast.LENGTH_SHORT).show()
                                        return@onClick
                                    }
                                    var _num = 0
                                    try {
                                        _num = num.toInt()
                                    } catch (e: Exception) {
                                        Toast.makeText(this@ProductActivity, "数量必须是数字", Toast.LENGTH_SHORT).show()
                                        return@onClick
                                    }
                                    vm.productscan(_num)
                                }
                            }.lparams {
                                alignParentRight()
                            }
                        }
                    }
                }
            }
            onCancelled {
                isnum=0
            }


           /* onKeyPressed {
                    dialog, keyCode, e->
                    if (keyCode == 132) {
                        scan()
                        true
                    }
                   false
            }*/


        }.show()
    }

}