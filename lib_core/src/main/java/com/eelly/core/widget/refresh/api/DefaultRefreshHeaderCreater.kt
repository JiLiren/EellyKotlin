package com.eelly.core.widget.refresh.api

import android.content.Context

/**
 * @author vurtne on 2-Jan-18.
 */
interface DefaultRefreshHeaderCreater {
    fun createRefreshHeader(context: Context,refreshLayout: IRefreshLayout) : IRefreshHeader
}