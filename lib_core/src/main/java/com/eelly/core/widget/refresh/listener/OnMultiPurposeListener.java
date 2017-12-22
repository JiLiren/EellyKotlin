package com.eelly.core.widget.refresh.listener;


import com.eelly.core.widget.refresh.api.IRefreshFooter;
import com.eelly.core.widget.refresh.api.IRefreshHeader;

/**
 * @author Vurtne on 14-Dec-17.
 */

public interface OnMultiPurposeListener extends OnRefreshLoadMoreListener, OnStateChangedListener {

    void onHeaderPulling(IRefreshHeader header, float percent, int offset, int headerHeight, int extendHeight);
    void onHeaderReleased(IRefreshHeader header, int headerHeight, int extendHeight);
    void onHeaderReleasing(IRefreshHeader header, float percent, int offset, int headerHeight, int extendHeight);
    void onHeaderStartAnimator(IRefreshHeader header, int headerHeight, int extendHeight);
    void onHeaderFinish(IRefreshHeader header, boolean success);

    void onFooterPulling(IRefreshFooter footer, float percent, int offset, int footerHeight, int extendHeight);
    void onFooterReleased(IRefreshFooter footer, int footerHeight, int extendHeight);
    void onFooterReleasing(IRefreshFooter footer, float percent, int offset, int footerHeight, int extendHeight);
    void onFooterStartAnimator(IRefreshFooter footer, int footerHeight, int extendHeight);
    void onFooterFinish(IRefreshFooter footer, boolean success);
}
