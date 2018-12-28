package com.mes.jyd.adapter.check

import android.graphics.Color
import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import com.mes.jyd.util.general
import com.mes.jyd.viewModel.check.CheckInStockViewModel
import com.mes.jyd.viewModel.check.ProductInspectViewModel
import org.jetbrains.anko.*
import org.jetbrains.anko.cardview.v7.cardView
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.json.JSONObject

class CheckInStockAdapter(var vm: CheckInStockViewModel): BaseAdapter() {
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
                        //入库单号
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

                        textView {
                            val t = "工单数量:${item.getString("tasknum")}"
                            text = t
                            textSize = 17f
                        }.lparams {
                            verticalMargin = dip(2)
                        }

                        textView {
                            val t = "总数量:${item.getString("allnum")}"
                            text = t
                            textSize = 17f
                        }.lparams {
                            verticalMargin = dip(2)
                        }

                        textView {
                            val t = "应检数量:${item.getString("checknum")}"
                            text = t
                            textSize = 17f
                        }.lparams {
                            verticalMargin = dip(2)
                        }



                        relativeLayout {
                            linearLayout {
                                orientation = LinearLayout.VERTICAL
                                relativeLayout {

                                    textView {
                                        text = general.getString(item, "username")
                                        textSize = 15f
                                        typeface = Typeface.create("Roboto-medium", Typeface.NORMAL)
                                    }

                                    textView {
                                        text = general.getString(item, "crdate")
                                        textSize = 15f
                                        typeface = Typeface.create("Roboto-medium", Typeface.NORMAL)
                                    }.lparams {
                                        alignParentRight()
                                    }
                                }

                                /*relativeLayout {

                                    textView {
                                        text = "申请人"
                                        textSize = 13f
                                        typeface = Typeface.create("Roboto-medium", Typeface.NORMAL)
                                    }

                                    textView {
                                        text = "申请时间"
                                        textSize = 13f
                                    }.lparams {
                                        alignParentRight()
                                    }
                                }
                            }*/

                            }.lparams {
                                bottomMargin = dip(16)
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


                        //点击按钮检验
                        button {
                            text = "检验"

                            onClick {
                                //跳转
                                vm.intentto(item.getInt("id"),"")
                            }
                        }.lparams {
                            minimumWidth = dip(100)
                            bottomMargin = dip(15)
                        }

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