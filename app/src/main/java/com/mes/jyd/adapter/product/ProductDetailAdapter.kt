package com.mes.jyd.adapter.product

import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.TextView
import com.mes.jyd.viewModel.product.ProductViewModel
import org.jetbrains.anko.*
import org.jetbrains.anko.cardview.v7.cardView
import org.json.JSONObject

class ProductDetailAdapter(viewModel: ProductViewModel):BaseAdapter() {
    //生产计划数据
    var vm=viewModel
    var list=vm.list2

    var titlesize=23f
    var contentsize=23f

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val item = getItem(position)
        return with(parent!!.context) {
            verticalLayout {
                cardView {
                    useCompatPadding = true
                    cardElevation = 4f
                    radius = 2f

                    linearLayout {
                        orientation = LinearLayout.VERTICAL
                        var _Text:TextView
                        relativeLayout {
                            _Text= textView {
                                val t = "生产工单:"
                                id=1
                                text = t
                                textSize = 23f
                                typeface= Typeface.create("Roboto-medium",Typeface.NORMAL)
                                /*if(item.getString("ie")=="0"){//是否异常
                                    textColor = Color.argb(85, 0, 0, 0)
                                }else{
                                    textColor = Color.argb(85, 200, 0, 0)
                                }*/
                            }.lparams{
                                verticalGravity=Gravity.CENTER_VERTICAL
                            }

                            textView {
                                val t = " ${item.getString("taskid")}"
                                text = t
                                textSize = 23f
                                /*if(item.getString("ie")=="0"){//是否异常
                                    textColor = Color.argb(85, 0, 0, 0)
                                }else{
                                    textColor = Color.argb(85, 200, 0, 0)
                                }*/

                            }.lparams {
                                rightOf(1)
                            }
                        }.lparams{
                            verticalMargin= dip(4)
                        }
                        //生产工单号

                        //生产工单号
                        textView {
                            val t = " 工序：${item.getString("technicsdemand")}"
                            text = t
                            textSize = 17f
                            /*if(item.getString("ie")=="0"){//是否异常
                                textColor = Color.argb(85, 0, 0, 0)
                            }else{
                                textColor = Color.argb(85, 200, 0, 0)
                            }*/
                        }.lparams {
                            verticalMargin = dip(4)
                        }

                        //物料编号
                        //显示第一个为零件号
                        textView {
                            val t = "产品编码: ${item.getString("mapno")}"
                            text = t
                            textSize = 20f
                            textColor = Color.argb(85, 0, 0, 0)
                        }.lparams {
                            verticalMargin = dip(4)
                        }
                        //物料名称
                        textView {
                            val t = "产品名称: ${item.getString("memo")}"
                            text = t
                            textSize = 17f

                        }.lparams {
                            verticalMargin = dip(1)
                        }
                        //数量
                        relativeLayout{
                            linearLayout{
                                orientation=LinearLayout.VERTICAL
                                relativeLayout {
                                    //数量
                                    textView {
                                        text=item.getString("plannum")
                                        textSize = 15f
                                        typeface= Typeface.create("Roboto-medium",Typeface.NORMAL)
                                    }
                                    //已完成数
                                    textView {
                                        text=item.getString("makenum")
                                        textSize = 15f
                                        typeface= Typeface.create("Roboto-medium",Typeface.NORMAL)
                                    }.lparams {
                                        alignParentRight()
                                    }
                                }

                                relativeLayout {
                                    //数量
                                    textView {
                                        text="数量"
                                        textSize = 13f
                                        typeface= Typeface.create("Roboto-medium",Typeface.NORMAL)
                                    }
                                    //已完成数
                                    textView {
                                        text="已完成数量"
                                        textSize = 13f
                                    }.lparams {
                                        alignParentRight()
                                    }
                                }
                            }

                        }.lparams{
                            bottomMargin = dip(16)
                        }

                    }.lparams {
                        horizontalPadding = dip(8)
                        topPadding = dip(8)
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
        list = vm.list2
        notifyDataSetChanged()
    }
}