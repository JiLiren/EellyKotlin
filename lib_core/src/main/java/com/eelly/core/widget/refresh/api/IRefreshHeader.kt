package com.eelly.core.widget.refresh.api


/**
 * @author Vurtne on 14-Dec-17.
 */

interface IRefreshHeader :IRefreshInternal{
    /**
     * 手指拖动下拉（会连续多次调用）
     * @param percent 下拉的百分比 值 = offset/headerHeight (0 - percent - (headerHeight+extendHeight) / headerHeight )
     * @param offset 下拉的像素偏移量  0 - offset - (headerHeight+extendHeight)
     * @param headerHeight Header的高度
     * @param extendHeight Header的扩展高度
     */
    fun onPullingDown(percent: Float, offset: Int, headerHeight: Int, extendHeight: Int)

    /**
     * 手指释放之后的持续动画
     * @param percent 下拉的百分比 值 = offset/headerHeight (0 - percent - (headerHeight+extendHeight) / headerHeight )
     * @param offset 下拉的像素偏移量  0 - offset - (headerHeight+extendHeight)
     * @param headerHeight Header的高度
     * @param extendHeight Header的扩展高度
     */
    fun onReleasing(percent: Float, offset: Int, headerHeight: Int, extendHeight: Int)

    /**
     * 释放时刻（调用一次，将会触发加载）
     * @param layout RefreshLayout
     * @param headerHeight HeaderHeight
     * @param extendHeight extendHeaderHeight or extendFooterHeight
     */
    fun onRefreshReleased(layout: IRefreshLayout, headerHeight: Int, extendHeight: Int)
}