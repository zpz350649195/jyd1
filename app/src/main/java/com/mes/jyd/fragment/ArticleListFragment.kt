package com.mes.jyd.fragment

import android.app.FragmentManager
import android.app.FragmentTransaction
import android.app.ListFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.Toast
import com.mes.jyd.R
import com.mes.jyd.adapter.a

class ArticleListFragment:ListFragment(){
    lateinit var adapter: ArrayAdapter<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //定义一个数组
        var data=ArrayList<String>()
        for (i in 1..20){
            data.add("smyh" + i)
        }
        //将数组加到ArrayAdapter当中
        adapter = ArrayAdapter<String>(activity,android.R.layout.simple_list_item_1, data)
                //绑定适配器时，必须通过ListFragment.setListAdapter()接口，而不是ListView.setAdapter()或其它方法
        var _a:BaseAdapter=a()
       // setListAdapter(_a)
        listAdapter=_a
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onListItemClick(l: ListView?, v: View?, position: Int, id: Long) {
        super.onListItemClick(l, v, position, id)
        var item = adapter.getItem(position)
        Toast.makeText(activity, item, 1).show()

        var manage:FragmentManager=fragmentManager
        var transaction:FragmentTransaction=manage.beginTransaction()
        var detail= detailFragment()
        transaction.replace(R.id.right1,detail,"detail")

      //  var item=adapter.getItem(position).toString()
        var args= Bundle()
        args.putString("item",item)
        detail.arguments=args
        transaction.commit()
        
    }
}