package com.eelly.core.widget.refresh.api

import android.support.annotation.ColorRes
import android.view.View
import android.view.ViewGroup
import android.view.animation.Interpolator
import com.eelly.core.widget.refresh.constant.RefreshState
import com.eelly.core.widget.refresh.listener.OnLoadMoreListener
import com.eelly.core.widget.refresh.listener.OnMultiPurposeListener
import com.eelly.core.widget.refresh.listener.OnRefreshListener
import com.eelly.core.widget.refresh.listener.OnRefreshLoadMoreListener

/**
 * @author Vurtne on 14-Dec-17.
 */
interface IRefreshLayout {

    /**
     * 设置footer高度
     * @param dp 高度
     */
    fun setFooterHeight(dp: Float): IRefreshLayout

    /**
     * 设置footer高度
     * @param px 高度
     */
    fun setFooterHeightPx(px: Int): IRefreshLayout

    /**
     * 设置Header高度
     * @param dp 高度
     */
    fun setHeaderHeight(dp: Float): IRefreshLayout

    /**
     * 设置Header高度
     * @param px 高度
     */
    fun setHeaderHeightPx(px: Int): IRefreshLayout

    /**
     * 显示拖动高度/真实拖动高度（默认0.5，阻尼效果）
     */
    fun setDragRate(rate: Float): IRefreshLayout

    /**
     * 设置下拉最大高度和Header高度的比率（将会影响可以下拉的最大高度）
     */
    fun setHeaderMaxDragRate(rate: Float): IRefreshLayout

    /**
     * 设置上啦最大高度和Footer高度的比率（将会影响可以上啦的最大高度）
     */
    fun setFooterMaxDragRate(rate: Float): IRefreshLayout

    /**
     * 设置 触发刷新距离 与 HeaderHieght 的比率
     */
    fun setHeaderTriggerRate(rate: Float): IRefreshLayout

    /**
     * 设置 触发加载距离 与 FooterHieght 的比率
     */
    fun setFooterTriggerRate(rate: Float): IRefreshLayout

    /**
     * 设置回弹显示插值器
     */
    fun setReboundInterpolator(interpolator: Interpolator): IRefreshLayout

    /**
     * 设置回弹动画时长
     */
    fun setReboundDuration(duration: Int): IRefreshLayout

    /**
     * 设置是否启用上啦加载更多（默认启用）
     */
    fun setEnableLoadMore(enable: Boolean): IRefreshLayout

    /**
     * 是否启用下拉刷新（默认启用）
     */
    fun setEnableRefresh(enable: Boolean): IRefreshLayout

    /**
     * 设置是否启在下拉Header的同时下拉内容
     */
    fun setEnableHeaderTranslationContent(enable: Boolean): IRefreshLayout

    /**
     * 设置是否启在上拉Footer的同时上拉内容
     */
    fun setEnableFooterTranslationContent(enable: Boolean): IRefreshLayout

    /**
     * 设置是否开启在刷新时候禁止操作内容视图
     */
    fun setDisableContentWhenRefresh(disable: Boolean): IRefreshLayout

    /**
     * 设置是否开启在加载时候禁止操作内容视图
     */
    fun setDisableContentWhenLoading(disable: Boolean): IRefreshLayout

    /**
     * 设置是否监听列表在滚动到底部时触发加载事件（默认true）
     */
    fun setEnableAutoLoadMore(enable: Boolean): IRefreshLayout

    /**
     * 标记数据全部加载完成，将不能再次触发加载功能（true）
     */
    @Deprecated("请使用 finishLoadmoreWithNoMoreData 和 resetNoMoreData 代替")
    fun setLoadMoreFinished(finished: Boolean): IRefreshLayout

    /**
     * 设置指定的Footer
     */
    fun setRefreshFooter(footer: IRefreshFooter): IRefreshLayout

    /**
     * 设置指定的Footer
     */
    fun setRefreshFooter(footer: IRefreshFooter, width: Int, height: Int): IRefreshLayout

    /**
     * 设置指定的Header
     */
    fun setRefreshHeader(header: IRefreshHeader): IRefreshLayout

    /**
     * 设置指定的Header
     */
    fun setRefreshHeader(header: IRefreshHeader, width: Int, height: Int): IRefreshLayout

    /**
     * 设置指定的Content
     */
    fun setRefreshContent(content: View): IRefreshLayout

    /**
     * 设置指定的Content
     */
    fun setRefreshContent(content: View, width: Int, height: Int): IRefreshLayout

    /**
     * 设置是否启用越界回弹
     */
    fun setEnableOverScrollBounce(enable: Boolean): IRefreshLayout

    /**
     * 设置是否开启纯滚动模式
     */
    fun setEnablePureScrollMode(enable: Boolean): IRefreshLayout

    /**
     * 设置是否在加载更多完成之后滚动内容显示新数据
     */
    fun setEnableScrollContentWhenLoaded(enable: Boolean): IRefreshLayout

    /**
     * 是否在刷新完成之后滚动内容显示新数据
     */
    fun setEnableScrollContentWhenRefreshed(enable: Boolean): IRefreshLayout

    /**
     * 设置在内容不满一页的时候，是否可以上拉加载更多
     */
    fun setEnableLoadMoreWhenContentNotFull(enable: Boolean): IRefreshLayout

    /**
     * 设置是否启用越界拖动（仿苹果效果）
     */
    fun setEnableOverScrollDrag(enable: Boolean): IRefreshLayout

    /**
     * 设置是否在全部加载结束之后Footer跟随内容
     */
    fun setEnableFooterFollowWhenLoadFinished(enable: Boolean): IRefreshLayout

    /**
     * 设置是会否启用嵌套滚动功能（默认关闭+智能开启）
     */
    fun setEnableNestedScroll(enabled: Boolean): IRefreshLayout

    /**
     * 单独设置刷新监听器
     */
    fun setOnRefreshListener(listener: OnRefreshListener): IRefreshLayout

    /**
     * 单独设置加载监听器
     */
    fun setOnLoadMoreListener(listener: OnLoadMoreListener): IRefreshLayout

    /**
     * 同时设置刷新和加载监听器
     */
    fun setOnRefreshLoadMoreListener(listener: OnRefreshLoadMoreListener): IRefreshLayout

    /**
     * 设置多功能监听器
     */
    fun setOnMultiPurposeListener(listener: OnMultiPurposeListener): IRefreshLayout

    /**
     * 设置主题颜色
     */
    fun setPrimaryColorsId(@ColorRes primaryColorId: IntArray): IRefreshLayout

    /**
     * 设置主题颜色
     */
    fun setPrimaryColors(colors: IntArray): IRefreshLayout

    /**
     * 设置滚动边界判断器
     */
    fun setScrollBoundaryDecider(boundary: ScrollBoundaryDecider): IRefreshLayout

    /**
     * 完成刷新
     */
    fun finishRefresh(): IRefreshLayout

    /**
     * 完成加载
     */
    fun finishLoadMore(): IRefreshLayout

    /**
     * 完成刷新
     */
    fun finishRefresh(delayed: Int): IRefreshLayout

    /**
     * 完成加载
     * @param success 数据是否成功刷新 （会影响到上次更新时间的改变）
     */
    fun finishRefresh(success: Boolean): IRefreshLayout

    /**
     * 完成刷新
     */
    fun finishRefresh(delayed: Int, success: Boolean): IRefreshLayout

    /**
     * 完成加载
     */
    fun finishLoadMore(delayed: Int): IRefreshLayout

    /**
     * 完成加载
     */
    fun finishLoadMore(success: Boolean): IRefreshLayout

    /**
     * 完成加载
     */
    fun finishLoadMore(delayed: Int, success: Boolean): IRefreshLayout

    /**
     * 完成加载
     */
    fun finishLoadMore(delayed: Int, success: Boolean, noMoreData: Boolean): IRefreshLayout

    /**
     * 完成加载并标记没有更多数据
     */
    fun finishLoadMoreWithNoMoreData(): IRefreshLayout

    /**
     * 恢复没有更多数据的原始状态
     */
    fun resetNoMoreData(): IRefreshLayout

    /**
     * 获取当前 Header
     */
    fun getRefreshHeader(): IRefreshHeader?

    /**
     * 获取当前 Footer
     */
    fun getRefreshFooter(): IRefreshFooter?

    /**
     * 获取当前状态
     */
    fun getState(): RefreshState

    /**
     * 获取实体布局视图
     */
    fun getLayout(): ViewGroup

    /**
     * 是否正在刷新
     */
    fun isRefreshing(): Boolean

    /**
     * 是否正在加载
     */
    fun isLoading(): Boolean

    /**
     * 自动刷新
     */
    fun autoRefresh(): Boolean

    /**
     * 自动刷新
     * @param delayed 开始延时
     */
    fun autoRefresh(delayed: Int): Boolean

    /**
     * 自动刷新
     * @param delayed 开始延时
     * @param duration 拖拽动画持续时间
     * @param dragrate 拉拽的高度比率（要求 ≥ 1 ）
     */
    fun autoRefresh(delayed: Int, duration: Int, dragrate: Float): Boolean

    /**
     * 自动加载
     */
    fun autoLoadMore(): Boolean

    /**
     * 自动加载
     * @param delayed 开始延时
     */
    fun autoLoadMore(delayed: Int): Boolean

    /**
     * 自动加载
     * @param delayed 开始延时
     * @param duration 拖拽动画持续时间
     * @param dragrate 拉拽的高度比率（要求 ≥ 1 ）
     */
    fun autoLoadMore(delayed: Int, duration: Int, dragrate: Float): Boolean

    fun isEnableRefresh(): Boolean

    fun isEnableLoadMore(): Boolean

    fun isLoadMoreFinished(): Boolean

    fun isEnableAutoLoadMore(): Boolean

    fun isEnableOverScrollBounce(): Boolean

    fun isEnablePureScrollMode(): Boolean

    fun isEnableScrollContentWhenLoaded(): Boolean
}