package com.eelly.core.widget.refresh.listener

import com.eelly.core.widget.refresh.api.IRefreshLayout
import com.eelly.core.widget.refresh.constant.RefreshState

/**
 * @author Vurtne on 14-Dec-17.
 */
interface OnStateChangedListener {
    /**
     * 状态改变事件 [RefreshState]
     * @param refreshLayout IRefreshLayout
     * @param oldState 改变之前的状态
     * @param newState 改变之后的状态
     */
    fun onStateChanged(refreshLayout: IRefreshLayout, oldState: RefreshState, newState: RefreshState)
}