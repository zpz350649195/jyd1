package com.mes.jyd.adapter.check

import android.graphics.Color
import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import com.mes.jyd.R
import com.mes.jyd.util.general
import com.mes.jyd.viewModel.check.CheckPreviousDetailViewModel
import com.mes.jyd.viewModel.check.ProductInspectCheckViewModel
import com.mes.jyd.viewModel.check.RejectManageDetailViewModel
import org.jetbrains.anko.*
import org.jetbrains.anko.cardview.v7.cardView
import org.jetbrains.anko.sdk25.coroutines.onCheckedChange
import org.json.JSONObject

class CheckPreviousDetailAdapter(viewModel: CheckPreviousDetailViewModel): BaseAdapter() {
    //生产计划数据
    var vm=viewModel
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
                        relativeLayout {
                            //检验项
                            textView {
                                val t = " ${item.getString("checkdesc")}"
                                text = t
                                textSize = 23f
                                /*if(item.getString("ie")=="0"){//是否异常
                                    textColor = Color.argb(85, 0, 0, 0)
                                }else{
                                    textColor = Color.argb(85, 200, 0, 0)
                                }*/

                            }.lparams {
                                width = matchParent
                            }
                        }.lparams {
                            verticalMargin = dip(4)
                        }


                        //检测方法
                        textView {
                            text = general.getString(item, "checktool")
                            textSize = 17f
                            /*if(item.getString("ie")=="0"){//是否异常
                                textColor = Color.argb(85, 0, 0, 0)
                            }else{
                                textColor = Color.argb(85, 200, 0, 0)
                            }*/
                        }.lparams {
                            verticalMargin = dip(4)
                        }

                        if (item.getBoolean("isvalue")) {
                            //标准值
                            textView {
                                val t = "标准值: ${general.getString(item, "stdvalue")}"
                                text = t
                                textSize = 17f
                                textColor = Color.argb(85, 0, 0, 0)
                            }.lparams {
                                verticalMargin = dip(4)
                            }


                            relativeLayout {
                                linearLayout {
                                    orientation = LinearLayout.VERTICAL
                                    relativeLayout {
                                        //最大值
                                        textView {
                                            text = general.getString(item, "maxvalue")
                                            textSize = 15f
                                            typeface = Typeface.create("Roboto-medium", Typeface.NORMAL)
                                        }
                                        //最小值
                                        textView {
                                            text = general.getString(item, "minvalue")
                                            textSize = 15f
                                            typeface = Typeface.create("Roboto-medium", Typeface.NORMAL)
                                        }.lparams {
                                            alignParentRight()
                                        }
                                    }

                                    relativeLayout {
                                        //数量
                                        textView {
                                            text = "最大值"
                                            textSize = 13f
                                            typeface = Typeface.create("Roboto-medium", Typeface.NORMAL)
                                        }
                                        //已完成数
                                        textView {
                                            text = "最小值"
                                            textSize = 13f
                                        }.lparams {
                                            alignParentRight()
                                        }
                                    }
                                }

                            }.lparams {
                                bottomMargin = dip(16)
                            }
                        }

                        /*if (item.getBoolean("isvalue")) {
                            //检测结果
                            textView {
                                val t = "检验结果: ${general.getString(item, "relvalue")}"
                                text = t
                                textSize = 17f
                            }
                        }*/

                        relativeLayout {

                            textView {
                                val t = "综合判定"
                                text = t
                                textSize = 17f
                            }

                            var t1=general.getString(item, "checkresult")
                            item.put("checkresult",t1)
                            /*if(t1=="0")
                                item.put("checkresult",false)
                            else if(t1=="")
                                item.put("checkresult",false)
                            else
                                item.put("checkresult",true)*/

                            //是否合格
                            checkBox {
                                width = dip(50)
                                height = dip(50)
                                buttonDrawableResource = R.xml.checkbox_style
                                if(item.getString("checkresult")=="1"){
                                    isChecked=true
                                }

                                onCheckedChange { buttonView, isChecked ->
                                    if(item.getString("checkresult")=="1"){
                                        item.put("checkresult","0")
                                    }else{
                                        item.put("checkresult","1")
                                    }

                                }
                            }.lparams {
                                height = dip(50)
                                alignParentRight()
                            }

                            /*textView {
                                var t1=general.getString(item, "checkresult")
                                if(t1=="0")
                                     t1="不合格"
                                else if(t1=="")
                                    t1="无"
                                else
                                    t1="合格"
                                text = t1
                                textSize = 15f
                                typeface = Typeface.create("Roboto-medium", Typeface.NORMAL)
                                if(t1=="不合格"){
                                    textColor=Color.rgb(255,0,0)
                                }
                            }.lparams {
                                alignParentRight()
                            }*/

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
        list = vm.list
      //  notifyDataSetChanged()
        notifyDataSetInvalidated()
    }

}