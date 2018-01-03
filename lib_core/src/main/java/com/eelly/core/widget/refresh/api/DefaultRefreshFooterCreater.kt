package com.eelly.core.widget.refresh.api

import android.content.Context

/**
 * @author vurtne on 2-Jan-18.
 */
interface DefaultRefreshFooterCreater {
    fun createRefreshFooter(context: Context, layout: IRefreshLayout): IRefreshFooter
}