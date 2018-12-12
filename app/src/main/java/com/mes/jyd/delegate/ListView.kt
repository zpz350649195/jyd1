package com.mes.jyd.delegate

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.support.annotation.AttrRes
import android.support.annotation.NonNull
import android.support.annotation.Nullable
import android.support.annotation.StyleRes
import android.util.AttributeSet
import android.widget.AbsListView


/**
 *
 * Created by pandanxin on 2018/3/25.
 */
class ListView : android.widget.ListView {
    private val onScrollListenerProxy = OnScrollListenerProxy()

    constructor(@NonNull context: Context) : super(context) {
        init()
    }

    constructor(@NonNull context: Context, @Nullable attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(@NonNull context: Context, @Nullable attrs: AttributeSet, @AttrRes defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(@NonNull context: Context, @Nullable attrs: AttributeSet, @AttrRes defStyleAttr: Int, @StyleRes defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        init()
    }

    private fun init() {
        super.setOnScrollListener(onScrollListenerProxy)
    }

    @Deprecated("")
    override fun setOnScrollListener(listener: AbsListView.OnScrollListener) {
        onScrollListenerProxy.setOnScrollListener(listener)
    }

    fun addOnScrollListener(listener: AbsListView.OnScrollListener) {
        onScrollListenerProxy.addOnScrollListener(listener)
    }

    fun removeOnScrollListener(listener: AbsListView.OnScrollListener) {
        onScrollListenerProxy.removeOnScrollListener(listener)
    }

    fun clearOnScrollListeners() {
        onScrollListenerProxy.clearOnScrollListeners()
    }

    private class OnScrollListenerProxy : AbsListView.OnScrollListener {
        private var onScrollListener: AbsListView.OnScrollListener? = null
        private var onScrollListenerList: MutableList<AbsListView.OnScrollListener>? = null
        override fun onScrollStateChanged(view: AbsListView, scrollState: Int) {
            if (onScrollListener != null) {
                onScrollListener!!.onScrollStateChanged(view, scrollState)
            }
            if (onScrollListenerList != null && onScrollListenerList!!.size > 0) {
                for (onScrollListener in onScrollListenerList!!) {
                    onScrollListener.onScrollStateChanged(view, scrollState)
                }
            }
        }

        override fun onScroll(view: AbsListView, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
            if (onScrollListener != null) {
                onScrollListener!!.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount)
            }
            if (onScrollListenerList != null && onScrollListenerList!!.size > 0) {
                for (onScrollListener in onScrollListenerList!!) {
                    onScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount)
                }
            }
        }

        internal fun setOnScrollListener(listener: AbsListView.OnScrollListener) {
            onScrollListener = listener
        }

        internal fun addOnScrollListener(listener: AbsListView.OnScrollListener) {
            if (onScrollListenerList == null) {
                onScrollListenerList = ArrayList()
            }
            onScrollListenerList!!.add(listener)
        }

        internal fun removeOnScrollListener(listener: AbsListView.OnScrollListener) {
            if (onScrollListenerList != null) {
                onScrollListenerList!!.remove(listener)
            }
        }

        internal fun clearOnScrollListeners() {
            if (onScrollListenerList != null) {
                onScrollListenerList!!.clear()
            }
        }
    }
}