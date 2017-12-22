package com.eelly.core.widget.refresh.listener;


import com.eelly.core.widget.refresh.api.IRefreshLayout;
import com.eelly.core.widget.refresh.constant.RefreshState;

/**
 * @author Vurtne on 14-Dec-17.
 */

public interface OnStateChangedListener {

    /**
     * 状态改变事件 {@link RefreshState}
     * @param refreshLayout IRefreshLayout
     * @param oldState 改变之前的状态
     * @param newState 改变之后的状态
     */
    void onStateChanged(IRefreshLayout refreshLayout, RefreshState oldState, RefreshState newState);
}
