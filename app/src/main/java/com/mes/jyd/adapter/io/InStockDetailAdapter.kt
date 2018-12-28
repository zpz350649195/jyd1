package com.mes.jyd.adapter.io

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import com.mes.jyd.R
import com.mes.jyd.util.general
import com.mes.jyd.viewModel.io.InStockDetailViewModel
import org.jetbrains.anko.*
import org.jetbrains.anko.cardview.v7.cardView
import org.jetbrains.anko.sdk25.coroutines.onCheckedChange
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.json.JSONObject

class InStockDetailAdapter(viewModel: InStockDetailViewModel): BaseAdapter() {
    //生产计划数据
    var vm=viewModel
    var list=vm.list

    var titlesize=23f
    var contentsize=23f
     var linear:LinearLayout?=null




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

                        relativeLayout {
                            textView {
                                val t = " ${item.getString("taskid")}"
                                text = t
                                textSize = 18f
                                /*if(item.getString("ie")=="0"){//是否异常
                                    textColor = Color.argb(85, 0, 0, 0)
                                }else{
                                    textColor = Color.argb(85, 200, 0, 0)
                                }*/

                            }.lparams {
                                width = wrapContent
                              //  rightMargin=dip(50)
                            }

                        }.lparams {
                            width= matchParent
                            verticalMargin = dip(4)
                        }

                        textView {
                            val t = "产品编码: ${general.getString(item, "mapno")}"
                            text = t
                            textSize = 17f
                            textColor = Color.argb(85, 0, 0, 0)
                        }.lparams {
                            verticalMargin = dip(4)
                        }

                        textView {
                            val t = "描述: ${general.getString(item, "memo")}"
                            text = t
                            textSize = 17f
                            textColor = Color.argb(85, 0, 0, 0)
                        }.lparams {
                            verticalMargin = dip(4)
                        }

                        textView {
                            val t = "待入库数量: ${general.getString(item, "allnum")}"
                            text = t
                            textSize = 17f
                            textColor = Color.argb(85, 0, 0, 0)
                        }.lparams {
                            verticalMargin = dip(4)
                        }



                        relativeLayout {
                            textView {
                                val t = "本次入库数量: ${general.getString(item, "num")}"
                                text = t
                                textSize = 17f
                                textColor = Color.argb(85, 0, 0, 0)
                            }.lparams {
                                verticalMargin = dip(4)
                            }

                        }.lparams {
                            width= matchParent
                        }

                        relativeLayout {
                            //是否合格
                            button {
                                text="修改"
                                onClick {
                                    vm.vw.alerrtDialog(position,item)
                                }
                            }.lparams {
                            }

                            checkBox {
                                width = dip(50)
                                height = dip(50)
                                buttonDrawableResource = R.xml.checkbox_style
                                if(item.getBoolean("ischeck")){
                                    isChecked=true
                                }

                                onCheckedChange { buttonView, isChecked ->
                                    if(item.getBoolean("ischeck")){
                                        item.put("ischeck",false)
                                    }else{
                                        item.put("ischeck",true)
                                    }

                                }
                            }.lparams {
                                height = dip(50)
                                alignParentRight()
                            }
                        }
                    }.lparams {
                        horizontalPadding = dip(8)
                        topPadding = dip(8)
                        width= matchParent
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