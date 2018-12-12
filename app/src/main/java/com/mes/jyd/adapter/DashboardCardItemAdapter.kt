package com.mes.jyd.adapter

import android.support.v4.content.ContextCompat
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.mes.jyd.viewModel.NavigationViewModel
import org.jetbrains.anko.*

/**
 * 操作类-仪表分组子项
 * Created by pandanxin on 2017/11/26.
 */
class DashboardCardItemAdapter(val vm: NavigationViewModel) : BaseAdapter() {

    var list = vm.list
    private val imageid=1

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val item = getItem(position)
        return with(parent!!.context) {
            relativeLayout {
                verticalPadding = dip(16)
//                backgroundColor=ContextCompat.getColor(ctx,R.color.white)
                imageView {
                    id = imageid
//                    imageResource = item["item_imageid"] as Int
                    image=ContextCompat.getDrawable(ctx,item["item_imageid"] as Int)

                }.lparams {
                    height = dip(48)
                    width = dip(48)
                    centerHorizontally()
                }

                textView {
                    text = item["item_name"].toString()
                    textSize = 16f

                }.lparams{
                    below(imageid)
                    topMargin = dip(4)
                    centerHorizontally()
                }
            }
        }
    }

    override fun getItem(position: Int): Map<String, Any> {
        return list[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return list.size
    }

    fun rebuild(){
        list=vm.list
        notifyDataSetChanged()
    }

}
