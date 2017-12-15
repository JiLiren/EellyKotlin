package com.eelly.core.widget.refresh.impl

import android.support.annotation.ColorInt
import android.view.View
import com.eelly.core.widget.refresh.RefreshLayout
import com.eelly.core.widget.refresh.api.IRefreshFooter
import com.eelly.core.widget.refresh.api.IRefreshKernel
import com.eelly.core.widget.refresh.api.IRefreshLayout
import com.eelly.core.widget.refresh.constant.RefreshState
import com.eelly.core.widget.refresh.constant.SpinnerStyle

/**
 * @author Vurtne on 14-Dec-17.
 */

class RefreshFooterWrapper(val mWrapperView: View) : IRefreshFooter {

    private var mSpinnerStyle: SpinnerStyle? = null

    override fun getView(): View {
        return mWrapperView
    }

    override fun onFinish(layout: IRefreshLayout, success: Boolean): Int = 0

    @Deprecated("")
    override fun setPrimaryColors(@ColorInt vararg colors: Int) {

    }

    override fun getSpinnerStyle(): SpinnerStyle {
        if (mSpinnerStyle != null) {
            return mSpinnerStyle!!
        }
        val params = mWrapperView.layoutParams
        if (params is RefreshLayout.LayoutParams) {
            mSpinnerStyle = params.spinnerStyle
            if (mSpinnerStyle != null) {
                return mSpinnerStyle!!
            }
        }
        if (params != null) {
            if (params.height == 0) {
                return mSpinnerStyle = SpinnerStyle.Scale
            }
        }
        return mSpinnerStyle = SpinnerStyle.Translate
    }

    override fun onInitialized(kernel: IRefreshKernel, height: Int, extendHeight: Int) {
        val params = mWrapperView.layoutParams
        if (params is RefreshLayout.LayoutParams) {
            kernel.requestDrawBackgoundForFooter(params.backgroundColor)
        }
    }

    override fun isSupportHorizontalDrag(): Boolean {
        return false
    }

    override fun onHorizontalDrag(percentX: Float, offsetX: Int, offsetMax: Int) {}

    override fun onPullingUp(percent: Float, offset: Int, footerHeight: Int, extendHeight: Int) {

    }

    override fun onPullReleasing(percent: Float, offset: Int, footerHeight: Int, extendHeight: Int) {

    }

    override fun onLoadMoreReleased(layout: IRefreshLayout, footerHeight: Int, extendHeight: Int) {

    }

    override fun onStartAnimator(layout: IRefreshLayout, footerHeight: Int, extendHeight: Int) {

    }

    override fun setLoadMoreFinished(finished: Boolean): Boolean {
        return false
    }

    override fun onStateChanged(refreshLayout: IRefreshLayout, oldState: RefreshState, newState: RefreshState) {
    }
}