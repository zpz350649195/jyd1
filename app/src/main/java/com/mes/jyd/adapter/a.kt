package com.mes.jyd.adapter

import android.database.DataSetObserver
import android.graphics.Color
import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.ListAdapter
import org.jetbrains.anko.*
import org.jetbrains.anko.cardview.v7.cardView
import org.json.JSONArray
import org.json.JSONObject

class a() : BaseAdapter() {

    //生产计划数据
    var list= getdata()

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
                        //生产工单号
                        textView {
                            val t = " ${item.getString("t")}"
                            text = t
                            textSize = 17f
                            /*if(item.getString("ie")=="0"){//是否异常
                                textColor = Color.argb(85, 0, 0, 0)
                            }else{
                                textColor = Color.argb(85, 200, 0, 0)
                            }*/
                        }.lparams {
                            verticalMargin = dip(1)
                        }

                        //物料编号
                        //显示第一个为零件号
                        textView {
                            val t = "物料编码: ${item.getString("m")}"
                            text = t
                            textSize = 17f
                            textColor = Color.argb(85, 0, 0, 0)
                        }.lparams {
                            verticalMargin = dip(4)
                        }
                        //物料名称
                        textView {
                            val t = "物料名称: ${item.getString("mn")}"
                            text = t
                            textSize = 17f

                        }.lparams {
                            verticalMargin = dip(1)
                        }
                        //数量
                        relativeLayout{
                            linearLayout{
                                orientation= LinearLayout.VERTICAL
                                relativeLayout {
                                    //数量
                                    textView {
                                        text=item.getString("n")
                                        textSize = 15f
                                        typeface= Typeface.create("Roboto-medium", Typeface.NORMAL)
                                    }
                                    //已完成数
                                }

                                relativeLayout {
                                    //数量
                                    textView {
                                        text="数量"
                                        textSize = 13f
                                        typeface= Typeface.create("Roboto-medium", Typeface.NORMAL)
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
        list = getdata()
    }

    fun getdata(): JSONArray {
        var list2 = JSONArray()
        var jo1 = JSONObject()
        jo1.put("t", "1322165465498641555")//条码
        jo1.put("m", "M1345646513")
        jo1.put("mn", "M134564M1345646513M1345646513M1345646513M1345646513M1345646513M1345646513M13456465136513")
        jo1.put("n", "300")

        list2.put(jo1)
        return list2
    }
}