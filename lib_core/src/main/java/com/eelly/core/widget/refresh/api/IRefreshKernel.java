package com.eelly.core.widget.refresh.api;

import android.support.annotation.NonNull;

import com.eelly.core.widget.refresh.constant.RefreshState;


/**
 * @author Vurtne on 14-Dec-17.
 */

public interface IRefreshKernel {

    @NonNull
    IRefreshLayout getRefreshLayout();
    @NonNull
    IRefreshContent getRefreshContent();

    IRefreshKernel setState(@NonNull RefreshState state);

    //<editor-fold desc="视图位移 Spinner">

    /**
     * 开始执行二极刷新
     * @param open 是否展开
     */
    void startTwoLevel(boolean open);

    /**
     * 结束关闭二极刷新
     */
    void finishTwoLevel();

    /**
     * 移动视图到指定位置
     * moveSpinner 的取名来自 谷歌官方的 @{@link android.support.v4.widget.SwipeRefreshLayout#moveSpinner(float)}
     * @param spinner 位置 (px)
     * @param isAnimator 标记是否是动画执行
     */
    IRefreshKernel moveSpinner(int spinner, boolean isAnimator);

    /**
     * 执行动画使视图位移到指定的 位置
     * moveSpinner 的取名来自 谷歌官方的 @{@link android.support.v4.widget.SwipeRefreshLayout#moveSpinner(float)}
     * @param endSpinner 指定的结束位置 (px)
     */
    IRefreshKernel animSpinner(int endSpinner);

    //</editor-fold>

    //<editor-fold desc="请求事件">

    /**
     * 指定在下拉时候为 Header 绘制背景
     * @param backgroundColor 背景颜色
     */
    IRefreshKernel requestDrawBackgoundForHeader(int backgroundColor);
    /**
     * 指定在下拉时候为 Footer 绘制背景
     * @param backgroundColor 背景颜色
     */
    IRefreshKernel requestDrawBackgoundForFooter(int backgroundColor);
    /**
     * 请求事件
     */
    IRefreshKernel requestHeaderNeedTouchEventWhenRefreshing(boolean request);
    /**
     * 请求事件
     */
    IRefreshKernel requestFooterNeedTouchEventWhenLoading(boolean request);

    /**
     * 请求设置默认内容滚动设置
     */
    IRefreshKernel requestDefaultHeaderTranslationContent(boolean translation);

    /**
     * 请求重新测量
     */
    IRefreshKernel requestRemeasureHeightForHeader();
    /**
     * 请求重新测量
     */
    IRefreshKernel requestRemeasureHeightForFooter();

    /**
     * 设置二楼回弹时长
     */
    IRefreshKernel requestFloorDuration(int duration);
    //</editor-fold>
}
