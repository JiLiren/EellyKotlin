package com.eelly.core.widget.refresh.api;

import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * 刷新内容组件
 * @author Vurtne on 14-Dec-17
 */
public interface IRefreshContent {
    void moveSpinner(int spinner);
    boolean canRefresh();
    boolean canLoadMore();
    int getMeasuredWidth();
    int getMeasuredHeight();
    void measure(int widthSpec, int heightSpec);
    void layout(int left, int top, int right, int bottom);

    View getView();
    View getScrollableView();
    ViewGroup.LayoutParams getLayoutParams();

    void onActionDown(MotionEvent e);
    void onActionUpOrCancel();

    void fling(int velocity);
    void setUpComponent(IRefreshKernel kernel, View fixedHeader, View fixedFooter);
    void onInitialHeaderAndFooter(int headerHeight, int footerHeight);
    void setScrollBoundaryDecider(ScrollBoundaryDecider boundary);

    void setEnableLoadMoreWhenContentNotFull(boolean enable);

    AnimatorUpdateListener scrollContentWhenFinished(int spinner);
}
