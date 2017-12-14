package com.eelly.core.widget.refresh.api;

import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;

import com.eelly.core.widget.refresh.constant.RefreshState;
import com.eelly.core.widget.refresh.listener.OnLoadMoreListener;
import com.eelly.core.widget.refresh.listener.OnMultiPurposeListener;
import com.eelly.core.widget.refresh.listener.OnRefreshListener;
import com.eelly.core.widget.refresh.listener.OnRefreshLoadMoreListener;


/**
 * @author Vurtne on 14-Dec-17.
 */

public interface IRefreshLayout {

    /**
     * 设置footer高度
     * @param dp 高度
     * */
    IRefreshLayout setFooterHeight(float dp);

    /**
     * 设置footer高度
     * @param px 高度
     * */
    IRefreshLayout setFooterHeightPx(int px);

    /**
     * 设置Header高度
     * @param dp 高度
     * */
    IRefreshLayout setHeaderHeight(float dp);

    /**
     * 设置Header高度
     * @param px 高度
     * */
    IRefreshLayout setHeaderHeightPx(int px);

    /**
     * 显示拖动高度/真实拖动高度（默认0.5，阻尼效果）
     */
    IRefreshLayout setDragRate(float rate);

    /**
     * 设置下拉最大高度和Header高度的比率（将会影响可以下拉的最大高度）
     */
    IRefreshLayout setHeaderMaxDragRate(float rate);

    /**
     * 设置上啦最大高度和Footer高度的比率（将会影响可以上啦的最大高度）
     */
    IRefreshLayout setFooterMaxDragRate(float rate);

    /**
     * 设置 触发刷新距离 与 HeaderHieght 的比率
     */
    IRefreshLayout setHeaderTriggerRate(float rate);

    /**
     * 设置 触发加载距离 与 FooterHieght 的比率
     */
    IRefreshLayout setFooterTriggerRate(float rate);

    /**
     * 设置回弹显示插值器
     */
    IRefreshLayout setReboundInterpolator(Interpolator interpolator);

    /**
     * 设置回弹动画时长
     */
    IRefreshLayout setReboundDuration(int duration);

    /**
     * 设置是否启用上啦加载更多（默认启用）
     */
    IRefreshLayout setEnableLoadMore(boolean enable);

    /**
     * 是否启用下拉刷新（默认启用）
     */
    IRefreshLayout setEnableRefresh(boolean enable);

    /**
     * 设置是否启在下拉Header的同时下拉内容
     */
    IRefreshLayout setEnableHeaderTranslationContent(boolean enable);

    /**
     * 设置是否启在上拉Footer的同时上拉内容
     */
    IRefreshLayout setEnableFooterTranslationContent(boolean enable);

    /**
     * 设置是否开启在刷新时候禁止操作内容视图
     */
    IRefreshLayout setDisableContentWhenRefresh(boolean disable);

    /**
     * 设置是否开启在加载时候禁止操作内容视图
     */
    IRefreshLayout setDisableContentWhenLoading(boolean disable);

    /**
     * 设置是否监听列表在滚动到底部时触发加载事件（默认true）
     */
    IRefreshLayout setEnableAutoLoadMore(boolean enable);

    /**
     * 标记数据全部加载完成，将不能再次触发加载功能（true）
     * @deprecated 请使用 finishLoadmoreWithNoMoreData 和 resetNoMoreData 代替
     */
    @Deprecated
    IRefreshLayout setLoadMoreFinished(boolean finished);

    /**
     * 设置指定的Footer
     */
    IRefreshLayout setRefreshFooter(IRefreshFooter footer);

    /**
     * 设置指定的Footer
     */
    IRefreshLayout setRefreshFooter(IRefreshFooter footer, int width, int height);

    /**
     * 设置指定的Header
     */
    IRefreshLayout setRefreshHeader(IRefreshHeader header);

    /**
     * 设置指定的Header
     */
    IRefreshLayout setRefreshHeader(IRefreshHeader header, int width, int height);

    /**
     * 设置指定的Content
     */
    IRefreshLayout setRefreshContent(View content);

    /**
     * 设置指定的Content
     */
    IRefreshLayout setRefreshContent(View content, int width, int height);

    /**
     * 设置是否启用越界回弹
     */
    IRefreshLayout setEnableOverScrollBounce(boolean enable);

    /**
     * 设置是否开启纯滚动模式
     */
    IRefreshLayout setEnablePureScrollMode(boolean enable);

    /**
     * 设置是否在加载更多完成之后滚动内容显示新数据
     */
    IRefreshLayout setEnableScrollContentWhenLoaded(boolean enable);

    /**
     * 是否在刷新完成之后滚动内容显示新数据
     */
    IRefreshLayout setEnableScrollContentWhenRefreshed(boolean enable);

    /**
     * 设置在内容不满一页的时候，是否可以上拉加载更多
     */
    IRefreshLayout setEnableLoadMoreWhenContentNotFull(boolean enable);

    /**
     * 设置是否启用越界拖动（仿苹果效果）
     */
    IRefreshLayout setEnableOverScrollDrag(boolean enable);

    /**
     * 设置是否在全部加载结束之后Footer跟随内容
     */
    IRefreshLayout setEnableFooterFollowWhenLoadFinished(boolean enable);

    /**
     * 设置是会否启用嵌套滚动功能（默认关闭+智能开启）
     */
    IRefreshLayout setEnableNestedScroll(boolean enabled);

    /**
     * 单独设置刷新监听器
     */
    IRefreshLayout setOnRefreshListener(OnRefreshListener listener);

    /**
     * 单独设置加载监听器
     */
    IRefreshLayout setOnLoadMoreListener(OnLoadMoreListener listener);

    /**
     * 同时设置刷新和加载监听器
     */
    IRefreshLayout setOnRefreshLoadMoreListener(OnRefreshLoadMoreListener listener);

    /**
     * 设置多功能监听器
     */
    IRefreshLayout setOnMultiPurposeListener(OnMultiPurposeListener listener);

    /**
     * 设置主题颜色
     */
    IRefreshLayout setPrimaryColorsId(@ColorRes int... primaryColorId);

    /**
     * 设置主题颜色
     */
    IRefreshLayout setPrimaryColors(int... colors);

    /**
     * 设置滚动边界判断器
     */
    IRefreshLayout setScrollBoundaryDecider(ScrollBoundaryDecider boundary);

    /**
     * 完成刷新
     */
    IRefreshLayout finishRefresh();

    /**
     * 完成加载
     */
    IRefreshLayout finishLoadMore();

    /**
     * 完成刷新
     */
    IRefreshLayout finishRefresh(int delayed);

    /**
     * 完成加载
     * @param success 数据是否成功刷新 （会影响到上次更新时间的改变）
     */
    IRefreshLayout finishRefresh(boolean success);

    /**
     * 完成刷新
     */
    IRefreshLayout finishRefresh(int delayed, boolean success);

    /**
     * 完成加载
     */
    IRefreshLayout finishLoadMore(int delayed);

    /**
     * 完成加载
     */
    IRefreshLayout finishLoadMore(boolean success);

    /**
     * 完成加载
     */
    IRefreshLayout finishLoadMore(int delayed, boolean success);

    /**
     * 完成加载
     */
    IRefreshLayout finishLoadMore(int delayed, boolean success, boolean noMoreData);

    /**
     * 完成加载并标记没有更多数据
     */
    IRefreshLayout finishLoadMoreWithNoMoreData();

    /**
     * 恢复没有更多数据的原始状态
     */
    IRefreshLayout resetNoMoreData();

    /**
     * 获取当前 Header
     */
    @Nullable
    IRefreshHeader getRefreshHeader();

    /**
     * 获取当前 Footer
     */
    @Nullable
    IRefreshFooter getRefreshFooter();

    /**
     * 获取当前状态
     */
    RefreshState getState();

    /**
     * 获取实体布局视图
     */
    ViewGroup getLayout();

    /**
     * 是否正在刷新
     */
    boolean isRefreshing();

    /**
     * 是否正在加载
     */
    boolean isLoading();

    /**
     * 自动刷新
     */
    boolean autoRefresh();

    /**
     * 自动刷新
     * @param delayed 开始延时
     */
    boolean autoRefresh(int delayed);

    /**
     * 自动刷新
     * @param delayed 开始延时
     * @param duration 拖拽动画持续时间
     * @param dragrate 拉拽的高度比率（要求 ≥ 1 ）
     */
    boolean autoRefresh(int delayed, int duration, float dragrate);

    /**
     * 自动加载
     */
    boolean autoLoadMore();

    /**
     * 自动加载
     * @param delayed 开始延时
     */
    boolean autoLoadMore(int delayed);

    /**
     * 自动加载
     * @param delayed 开始延时
     * @param duration 拖拽动画持续时间
     * @param dragrate 拉拽的高度比率（要求 ≥ 1 ）
     */
    boolean autoLoadMore(int delayed, int duration, float dragrate);

    boolean isEnableRefresh();

    boolean isEnableLoadMore();

    boolean isLoadMoreFinished();

    boolean isEnableAutoLoadMore();

    boolean isEnableOverScrollBounce();

    boolean isEnablePureScrollMode();

    boolean isEnableScrollContentWhenLoaded();
}
