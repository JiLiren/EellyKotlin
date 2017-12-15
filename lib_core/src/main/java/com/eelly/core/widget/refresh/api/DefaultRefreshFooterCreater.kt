package com.eelly.core.widget.refresh.api

import android.content.Context

/**
 * @author Vurtne on 14-Dec-17.
 */

interface DefaultRefreshFooterCreater {
    fun createRefreshFooter(context: Context, layout: IRefreshLayout): IRefreshFooter
}