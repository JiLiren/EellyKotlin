package com.eelly.core.widget.refresh.api;

import com.eelly.core.widget.refresh.constant.RefreshState;

/**
 * @author Vurtne on 14-Dec-17.
 */

public interface IRefreshHeader extends IRefreshInternal{

    /**
     * 手指拖动下拉（会连续多次调用）
     * @param percent 下拉的百分比 值 = offset/headerHeight (0 - percent - (headerHeight+extendHeight) / headerHeight )
     * @param offset 下拉的像素偏移量  0 - offset - (headerHeight+extendHeight)
     * @param headerHeight Header的高度
     * @param extendHeight Header的扩展高度
     */
    void onPullingDown(float percent, int offset, int headerHeight, int extendHeight);

    /**
     * 手指释放之后的持续动画
     * @param percent 下拉的百分比 值 = offset/headerHeight (0 - percent - (headerHeight+extendHeight) / headerHeight )
     * @param offset 下拉的像素偏移量  0 - offset - (headerHeight+extendHeight)
     * @param headerHeight Header的高度
     * @param extendHeight Header的扩展高度
     */
    void onReleasing(float percent, int offset, int headerHeight, int extendHeight);

    /**
     * 释放时刻（调用一次，将会触发加载）
     * @param layout RefreshLayout
     * @param headerHeight HeaderHeight
     * @param extendHeight extendHeaderHeight or extendFooterHeight
     */
    void onRefreshReleased(IRefreshLayout layout, int headerHeight, int extendHeight);

    void onStateChanged(IRefreshLayout refreshLayout, RefreshState oldState, RefreshState newState);
}