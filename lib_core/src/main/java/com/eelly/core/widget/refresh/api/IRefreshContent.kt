package com.eelly.core.widget.refresh.api

import android.animation.ValueAnimator.AnimatorUpdateListener
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup.LayoutParams

/**
 * @author vurtne on 2-Jan-18.
 */
interface IRefreshContent {
    /**
     * @param spinner 微调
     * */
    fun moveSpinner(spinner : Int)
    /**
     * 是否可刷新
     * */
    fun canRefresh(): Boolean
    /**
     * 是否可加载更多
     * */
    fun canLoadMore(): Boolean
    /**
     * 获取宽度
     * */
    fun getMeasuredWidth(): Int
    /**
     * 获取高度
     * */
    fun getMeasuredHeight(): Int
    /**
     * 测量
     * */
    fun measure(widthSpec: Int, heightSpec: Int)
    /**
     * 布局
     * */
    fun layout(left: Int, top: Int, right: Int, bottom: Int)
    /**
     * 获取布局
     * */
    fun getView(): View
    /**
     * 获取可滑动布局
     * */
    fun getScrollableView(): View
    /**
     * 获取Params
     * */
    fun getLayoutParams(): LayoutParams
    /**
     * 触摸到
     * */
    fun onActionDown(e: MotionEvent)
    /**
     * 手指离开或者取消
     * */
    fun onActionUpOrCancel()
    fun fling(velocity: Int)
    fun setUpComponent(kernel: IRefreshKernel, fixedHeader: View, fixedFooter: View)
    fun onInitialHeaderAndFooter(headerHeight: Int, footerHeight: Int)
    fun setScrollBoundaryDecider(boundary: ScrollBoundaryDecider)
    fun setEnableLoadMoreWhenContentNotFull(enable: Boolean)
    fun scrollContentWhenFinished(spinner: Int): AnimatorUpdateListener
}