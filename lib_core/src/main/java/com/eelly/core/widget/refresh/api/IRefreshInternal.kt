package com.eelly.core.widget.refresh.api

import android.support.annotation.ColorInt
import android.view.View
import com.eelly.core.widget.refresh.constant.SpinnerStyle
import com.eelly.core.widget.refresh.listener.OnStateChangedListener

/**
 * @author Vurtne on 14-Dec-17.
 */

interface IRefreshInternal : OnStateChangedListener {

    /**
     * 获取实体视图
     */
    fun getView(): View

    /**
     * 获取变换方式 [SpinnerStyle]
     */
    fun getSpinnerStyle(): SpinnerStyle

    /**
     * 设置主题颜色
     * @param colors 对应Xml中配置的 srlPrimaryColor srlAccentColor
     */
    fun setPrimaryColors(@ColorInt colors: IntArray)

    /**
     * 尺寸定义完成 （如果高度不改变（代码修改：setHeader），只调用一次, 在RefreshLayout#onMeasure中调用）
     * @param kernel RefreshKernel
     * @param height HeaderHeight or FooterHeight
     * @param extendHeight extendHeaderHeight or extendFooterHeight
     */
    fun onInitialized(kernel: IRefreshKernel, height: Int, extendHeight: Int)

    /**
     * 水平方向的拖动
     * @param percentX 下拉时，手指水平坐标对屏幕的占比（0 - percentX - 1）
     * @param offsetX 下拉时，手指水平坐标对屏幕的偏移（0 - offsetX - LayoutWidth）
     */
    fun onHorizontalDrag(percentX: Float, offsetX: Int, offsetMax: Int)

    /**
     * 开始动画
     * @param layout RefreshLayout
     * @param height HeaderHeight or FooterHeight
     * @param extendHeight extendHeaderHeight or extendFooterHeight
     */
    fun onStartAnimator(layout: IRefreshLayout, height: Int, extendHeight: Int)

    /**
     * 动画结束
     * @param layout RefreshLayout
     * @param success 数据是否成功刷新或加载
     * @return 完成动画所需时间 如果返回 Integer.MAX_VALUE 将取消本次完成事件，继续保持原有状态
     */
    fun onFinish(layout: IRefreshLayout, success: Boolean): Int

    /**
     * 是否支持水平方向的拖动（将会影响到onHorizontalDrag的调用）
     * @return 水平拖动需要消耗更多的时间和资源，所以如果不支持请返回false
     */
    fun isSupportHorizontalDrag(): Boolean
}