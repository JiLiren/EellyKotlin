package com.eelly.core.widget.refresh.api

import com.eelly.core.widget.refresh.constant.RefreshState

/**
 * @author Vurtne on 14-Dec-17.
 */
interface IRefreshKernel {

    fun getRefreshLayout(): IRefreshLayout
    fun getRefreshContent(): IRefreshContent

    fun setState(state: RefreshState): IRefreshKernel

    //<editor-fold desc="视图位移 Spinner">

    /**
     * 开始执行二极刷新
     * @param open 是否展开
     */
    fun startTwoLevel(open: Boolean)

    /**
     * 结束关闭二极刷新
     */
    fun finishTwoLevel()

    /**
     * 移动视图到指定位置
     * moveSpinner 的取名来自 谷歌官方的 @[android.support.v4.widget.SwipeRefreshLayout.moveSpinner]
     * @param spinner 位置 (px)
     * @param isAnimator 标记是否是动画执行
     */
    fun moveSpinner(spinner: Int, isAnimator: Boolean): IRefreshKernel

    /**
     * 执行动画使视图位移到指定的 位置
     * moveSpinner 的取名来自 谷歌官方的 @[android.support.v4.widget.SwipeRefreshLayout.moveSpinner]
     * @param endSpinner 指定的结束位置 (px)
     */
    fun animSpinner(endSpinner: Int): IRefreshKernel

    //</editor-fold>

    //<editor-fold desc="请求事件">

    /**
     * 指定在下拉时候为 Header 绘制背景
     * @param backgroundColor 背景颜色
     */
    fun requestDrawBackgoundForHeader(backgroundColor: Int): IRefreshKernel

    /**
     * 指定在下拉时候为 Footer 绘制背景
     * @param backgroundColor 背景颜色
     */
    fun requestDrawBackgoundForFooter(backgroundColor: Int): IRefreshKernel

    /**
     * 请求事件
     */
    fun requestHeaderNeedTouchEventWhenRefreshing(request: Boolean): IRefreshKernel

    /**
     * 请求事件
     */
    fun requestFooterNeedTouchEventWhenLoading(request: Boolean): IRefreshKernel

    /**
     * 请求设置默认内容滚动设置
     */
    fun requestDefaultHeaderTranslationContent(translation: Boolean): IRefreshKernel

    /**
     * 请求重新测量
     */
    fun requestRemeasureHeightForHeader(): IRefreshKernel

    /**
     * 请求重新测量
     */
    fun requestRemeasureHeightForFooter(): IRefreshKernel

    /**
     * 设置二楼回弹时长
     */
    fun requestFloorDuration(duration: Int): IRefreshKernel
    //</editor-fold>
}