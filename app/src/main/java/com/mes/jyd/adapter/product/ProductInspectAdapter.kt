package com.mes.jyd.adapter.product

import android.graphics.Color
import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import com.mes.jyd.util.general
import com.mes.jyd.viewModel.product.ProductCheckViewModel
import com.mes.jyd.viewModel.product.ProductInspectViewModel
import org.jetbrains.anko.*
import org.jetbrains.anko.cardview.v7.cardView
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.json.JSONObject

class ProductInspectAdapter(var vm: ProductInspectViewModel): BaseAdapter() {
    //生产计划数据
    var list=vm.list

    var titlesize=23f
    var contentsize=23f
     var linear:LinearLayout?=null
    var linear1:LinearLayout?=null

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val item = getItem(position)
        return with(parent!!.context) {
            verticalLayout {
                cardView {
                    useCompatPadding = true
                    cardElevation = 8f
                    radius = 2f

                    linearLayout {
                        orientation = LinearLayout.VERTICAL
                        //生产工单号
                        textView {
                            val t = " ${item.getString("taskid")}(${item.getString("typename")})"
                            text = t
                            textSize = 17f
                        }.lparams {
                            verticalMargin = dip(2)
                        }
                        //工位信息
                        textView {
                            val t = "工位:${item.getString("positiondesc")}"
                            text = t
                            textSize = 17f
                        }.lparams {
                            verticalMargin = dip(2)
                        }

                        //工序信息
                        textView {
                            val t = "工序:${item.getString("procname")}"
                            text = t
                            textSize = 17f
                        }.lparams {
                            verticalMargin = dip(2)
                        }

                        textView {
                            val t = "产品编码: ${item.getString("mapno")}"
                            text = t
                            textSize = 17f
                            textColor = Color.argb(85, 0, 0, 0)
                        }.lparams {
                            verticalMargin = dip(8)
                        }
                        //物料名称
                        textView {
                            val t = "产品名称: ${item.getString("memo")}"
                            text = t
                            textSize = 17f

                        }.lparams {
                            verticalMargin = dip(2)
                        }
                        //点击按钮检验
                        button {
                            text="检验"

                            onClick {
                                //跳转
                                vm.intentto(item.getInt("id"))
                            }
                        }.lparams {
                            minimumWidth=dip(100)
                            bottomMargin=dip(15)
                        }


                    }.lparams {
                        horizontalPadding = dip(8)
                        topPadding = dip(8)
                        /*leftPadding=dip(5)
                        rightPadding=dip(5)*/
                       // backgroundColor=Color.rgb(128,128,128)
                       /* if(item.getInt("id")==1)
                         backgroundColor=Color.rgb(0,0,255)
                        else
                            backgroundColor=Color.rgb(255,0,255)*/
                    }
                }
            }
        }
    }



    override fun getItem(position: Int): JSONObject {
        return list.getJSONObject(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return list.length()
    }

    fun rebuild() {
        list = vm.list
      //  notifyDataSetChanged()
        notifyDataSetInvalidated()
    }

}