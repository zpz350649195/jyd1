package com.mes.jyd.adapter.io

import android.graphics.Color
import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import com.mes.jyd.util.general
import com.mes.jyd.viewModel.io.InStockViewModel
import com.mes.jyd.viewModel.product.ProductCheckViewModel
import com.mes.jyd.viewModel.product.ProductInspectViewModel
import org.jetbrains.anko.*
import org.jetbrains.anko.cardview.v7.cardView
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.json.JSONObject

class InStockAdapter(var vm: InStockViewModel): BaseAdapter() {
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

                        textView {
                            val t = "入库单号:${item.getString("instockno")}"
                            text = t
                            textSize = 17f
                        }.lparams {
                            verticalMargin = dip(2)
                        }

                        textView {
                            val t = "描述:${item.getString("instockdesc")}"
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

                        relativeLayout {
                            linearLayout {
                                orientation = LinearLayout.VERTICAL
                                relativeLayout {
                                    //订单数量
                                    textView {
                                        text = general.getString(item, "tasknum")
                                        textSize = 15f
                                        typeface = Typeface.create("Roboto-medium", Typeface.NORMAL)
                                    }
                                    //待入库数量
                                    textView {
                                        text = general.getString(item, "num")
                                        textSize = 15f
                                        typeface = Typeface.create("Roboto-medium", Typeface.NORMAL)
                                    }.lparams {
                                        alignParentRight()
                                    }
                                }

                                relativeLayout {
                                    //数量
                                    textView {
                                        text = "订单数量"
                                        textSize = 13f
                                        typeface = Typeface.create("Roboto-medium", Typeface.NORMAL)
                                    }
                                    //已完成数
                                    textView {
                                        text = "待入库数量"
                                        textSize = 13f
                                    }.lparams {
                                        alignParentRight()
                                    }
                                }
                            }

                        }.lparams {
                            bottomMargin = dip(16)
                        }
                        //点击按钮检验
                        button {
                            text="入库"

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