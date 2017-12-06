package com.eelly.core.tools

import android.annotation.SuppressLint
import android.os.Build
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.webkit.WebView
import android.widget.AbsListView
import android.widget.AdapterView
import android.widget.ScrollView

/**
 * @author Vurtne on 6-Dec-17.
 */
class HeaderScrollHelper {


    /**
     *
     * 当前sdk版本，用于判断api版本
     * */
    private val mSysVersion: Int

    private var mCurrentScrollableContainer: ScrollableContainer? = null

    init {
        mSysVersion = Build.VERSION.SDK_INT
    }

    /** 包含有 ScrollView ListView RecyclerView 的组件  */
    interface ScrollableContainer {

        /** @return ScrollView ListView RecyclerView 或者其他的布局的实例
         */
        val scrollableView: View
    }

    fun setCurrentScrollableContainer(scrollableContainer: ScrollableContainer) {
        this.mCurrentScrollableContainer = scrollableContainer
    }

    private fun getScrollableView(): View? {
        if (mCurrentScrollableContainer == null) {
            return null
        }
        return mCurrentScrollableContainer!!.scrollableView
    }

    /**
     * 判断是否滑动到顶部方法,ScrollAbleLayout根据此方法来做一些逻辑判断
     * 目前只实现了AdapterView,ScrollView,RecyclerView
     * 需要支持其他view可以自行补充实现
     */
    fun isTop(): Boolean {
        val scrollableView = getScrollableView() ?: return false
        if (scrollableView is AdapterView<*>) {
            return isAdapterViewTop(scrollableView)
        }
        if (scrollableView is ScrollView) {
            return isScrollViewTop(scrollableView)
        }
        if (scrollableView is RecyclerView) {
            return isRecyclerViewTop(scrollableView)
        }
        if (scrollableView is WebView) {
            return isWebViewTop(scrollableView)
        }
        return false
    }

    fun isNoData(): Boolean {
        val scrollableView = getScrollableView()
        if (scrollableView is RecyclerView) {
            return isRecyclerViewNoData(scrollableView as RecyclerView?)
        }
        return false
    }

    private fun isRecyclerViewTop(recyclerView: RecyclerView?): Boolean {
        if (recyclerView != null) {
            val layoutManager = recyclerView.layoutManager
            if (layoutManager is LinearLayoutManager) {
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                val childAt = recyclerView.getChildAt(0)
                if (childAt == null || (firstVisibleItemPosition == 0 || firstVisibleItemPosition == 1) && childAt.top == 0) {
                    return true
                }

            }
        }
        return false
    }

    private fun isRecyclerViewNoData(recyclerView: RecyclerView?): Boolean {
        if (recyclerView != null) {
            val layoutManager = recyclerView.layoutManager
            val adapter = recyclerView.adapter
            val childCount = recyclerView.childCount
            if (layoutManager == null || adapter == null || childCount == 0) {
                return true
            }
        }
        return false
    }

    private fun isAdapterViewTop(adapterView: AdapterView<*>?): Boolean {
        if (adapterView != null) {
            val firstVisiblePosition = adapterView.firstVisiblePosition
            val childAt = adapterView.getChildAt(0)
            if (childAt == null || firstVisiblePosition == 0 && childAt.top == 0) {
                return true
            }
        }
        return false
    }

    private fun isScrollViewTop(scrollView: ScrollView?): Boolean {
        if (scrollView != null) {
            val scrollViewY = scrollView.scrollY
            return scrollViewY <= 0
        }
        return false
    }

    private fun isWebViewTop(scrollView: WebView?): Boolean {
        if (scrollView != null) {
            val scrollViewY = scrollView.scrollY
            return scrollViewY <= 0
        }
        return false
    }

    /**
     * 将特定的view按照初始条件滚动
     *
     * @param velocityY 初始滚动速度
     * @param distance  需要滚动的距离
     * @param duration  允许滚动的时间
     */
    @SuppressLint("NewApi")
    fun smoothScrollBy(velocityY: Int, distance: Int, duration: Int) {
        val scrollableView = getScrollableView()
        if (scrollableView is AbsListView) {
            val absListView = scrollableView
            if (mSysVersion >= 21) {
                absListView.fling(velocityY)
            } else {
                absListView.smoothScrollBy(distance, duration)
            }
        } else (scrollableView as? ScrollView)?.fling(velocityY) ?: ((scrollableView as? RecyclerView)?.fling(0, velocityY) ?: if (scrollableView is WebView) {
            scrollableView.flingScroll(0, velocityY)
        })
    }
}