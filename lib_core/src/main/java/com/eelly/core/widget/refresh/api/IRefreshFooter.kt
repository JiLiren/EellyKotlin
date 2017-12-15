package com.eelly.core.widget.refresh.api

/**
 * @author Vurtne on 14-Dec-17.
 */

interface IRefreshFooter :IRefreshInternal{

    /**
     * 手指拖动下拉（会连续多次调用）
     * @param percent 下拉的百分比 值 = offset/footerHeight (0 - percent - (footerHeight+extendHeight) / footerHeight )
     * @param offset 下拉的像素偏移量  0 - offset - (footerHeight+extendHeight)
     * @param footerHeight Footer的高度
     * @param extendHeight Footer的扩展高度
     */
    fun onPullingUp(percent: Float, offset: Int, footerHeight: Int, extendHeight: Int)

    /**
     * 手指释放之后的持续动画（会连续多次调用）
     * @param percent 下拉的百分比 值 = offset/footerHeight (0 - percent - (footerHeight+extendHeight) / footerHeight )
     * @param offset 下拉的像素偏移量  0 - offset - (footerHeight+extendHeight)
     * @param footerHeight Footer的高度
     * @param extendHeight Footer的扩展高度
     */
    fun onPullReleasing(percent: Float, offset: Int, footerHeight: Int, extendHeight: Int)

    /**
     * 释放时刻（调用一次，将会触发加载）
     * @param layout RefreshLayout
     * @param footerHeight FooterHeight
     * @param extendHeight extendHeaderHeight or extendFooterHeight
     */
    fun onLoadMoreReleased(layout: IRefreshLayout, footerHeight: Int, extendHeight: Int)

    /**
     * 设置数据全部加载完成，将不能再次触发加载功能
     * @param finished finished
     * @return true 支持全部加载完成的状态显示 false 不支持
     */
    fun setLoadMoreFinished(finished: Boolean): Boolean
}