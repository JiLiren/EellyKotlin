package com.eelly.core.widget.refresh.listener

import com.eelly.core.widget.refresh.api.IRefreshFooter
import com.eelly.core.widget.refresh.api.IRefreshHeader

/**
 * @author Vurtne on 14-Dec-17.
 */
interface OnMultiPurposeListener {
    fun onHeaderPulling(header: IRefreshHeader, percent: Float, offset: Int, headerHeight: Int, extendHeight: Int)
    fun onHeaderReleased(header: IRefreshHeader, headerHeight: Int, extendHeight: Int)
    fun onHeaderReleasing(header: IRefreshHeader, percent: Float, offset: Int, headerHeight: Int, extendHeight: Int)
    fun onHeaderStartAnimator(header: IRefreshHeader, headerHeight: Int, extendHeight: Int)
    fun onHeaderFinish(header: IRefreshHeader, success: Boolean)

    fun onFooterPulling(footer: IRefreshFooter, percent: Float, offset: Int, footerHeight: Int, extendHeight: Int)
    fun onFooterReleased(footer: IRefreshFooter, footerHeight: Int, extendHeight: Int)
    fun onFooterReleasing(footer: IRefreshFooter, percent: Float, offset: Int, footerHeight: Int, extendHeight: Int)
    fun onFooterStartAnimator(footer: IRefreshFooter, footerHeight: Int, extendHeight: Int)
    fun onFooterFinish(footer: IRefreshFooter, success: Boolean)
}