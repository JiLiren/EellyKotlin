package com.eelly.core.widget.refresh.api

import android.view.View

/**
 * @author Vurtne on 14-Dec-17.
 */
interface ScrollBoundaryDecider {
    /**
     * 根据内容视图状态判断是否可以开始下拉刷新
     * @param content 内容视图
     * @return true 将会触发下拉刷新
     */
    fun canRefresh(content: View): Boolean

    /**
     * 根据内容视图状态判断是否可以开始上拉加载
     * @param content 内容视图
     * @return true 将会触发加载更多
     */
    fun canLoadmore(content: View): Boolean
}