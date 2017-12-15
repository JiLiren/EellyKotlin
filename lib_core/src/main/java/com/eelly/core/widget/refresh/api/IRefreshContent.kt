package com.eelly.core.widget.refresh.api

import android.animation.ValueAnimator
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup

/**
 * 刷新内容组件
 * @author Vurtne on 14-Dec-17
 */

interface IRefreshContent {
    fun moveSpinner(spinner: Int)
    fun canRefresh(): Boolean
    fun canLoadMore(): Boolean
    fun getMeasuredWidth(): Int
    fun getMeasuredHeight(): Int
    fun measure(widthSpec: Int, heightSpec: Int)
    fun layout(left: Int, top: Int, right: Int, bottom: Int)

    fun getView(): View
    fun getScrollableView(): View
    fun getLayoutParams(): ViewGroup.LayoutParams

    fun onActionDown(e: MotionEvent)
    fun onActionUpOrCancel()

    fun fling(velocity: Int)
    fun setUpComponent(kernel: IRefreshKernel, fixedHeader: View, fixedFooter: View)
    fun onInitialHeaderAndFooter(headerHeight: Int, footerHeight: Int)
    fun setScrollBoundaryDecider(boundary: ScrollBoundaryDecider)

    fun setEnableLoadMoreWhenContentNotFull(enable: Boolean)

    fun scrollContentWhenFinished(spinner: Int): ValueAnimator.AnimatorUpdateListener
}