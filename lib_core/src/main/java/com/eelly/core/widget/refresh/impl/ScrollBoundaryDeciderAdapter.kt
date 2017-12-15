package com.eelly.core.widget.refresh.impl

import android.view.MotionEvent
import android.view.View
import com.eelly.core.widget.refresh.api.ScrollBoundaryDecider
import com.eelly.core.widget.refresh.tools.ScrollBoundaryUtil

/**
 * @author Vurtne on 14-Dec-17.
 */
class ScrollBoundaryDeciderAdapter : ScrollBoundaryDecider {
    //<editor-fold desc="Internal">
    protected var mActionEvent: MotionEvent?=null
    protected var boundary: ScrollBoundaryDecider? = null
    protected var mEnableLoadmoreWhenContentNotFull: Boolean = false

    internal fun setScrollBoundaryDecider(boundary: ScrollBoundaryDecider) {
        this.boundary = boundary
    }

    internal fun setActionEvent(event: MotionEvent) {
        mActionEvent = event
    }
    //</editor-fold>

    //<editor-fold desc="ScrollBoundaryDecider">
    override fun canRefresh(content: View): Boolean {
        return if (boundary != null) {
            boundary!!.canRefresh(content)
        } else ScrollBoundaryUtil.canRefresh(content, mActionEvent)
    }

    override fun canLoadmore(content: View): Boolean {
        if (boundary != null) {
            return boundary!!.canLoadmore(content)
        }
        return if (mEnableLoadmoreWhenContentNotFull) {
            !ScrollBoundaryUtil.canScrollDown(content, mActionEvent)
        } else ScrollBoundaryUtil.canLoadmore(content, mActionEvent)
    }

    fun setEnableLoadmoreWhenContentNotFull(enable: Boolean) {
        mEnableLoadmoreWhenContentNotFull = enable
    }
    //</editor-fold>
}