package com.eelly.core.widget.refresh.impl

import android.support.annotation.ColorInt
import android.view.View
import android.view.ViewGroup.LayoutParams
import com.eelly.core.widget.refresh.RefreshLayout
import com.eelly.core.widget.refresh.api.IRefreshHeader
import com.eelly.core.widget.refresh.api.IRefreshKernel
import com.eelly.core.widget.refresh.api.IRefreshLayout
import com.eelly.core.widget.refresh.constant.RefreshState
import com.eelly.core.widget.refresh.constant.SpinnerStyle

/**
 * @author Vurtne on 14-Dec-17.
 */
class RefreshHeaderWrapper(val mWrapperView : View) : IRefreshHeader {

    private var mSpinnerStyle: SpinnerStyle? = null

    override fun getView(): View =  mWrapperView

    override fun onFinish(layout: IRefreshLayout, success: Boolean): Int {
        return 0
    }

    @Deprecated("")
    override fun setPrimaryColors(@ColorInt colors: IntArray) {

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
            if (params.height == LayoutParams.MATCH_PARENT) {
                mSpinnerStyle = SpinnerStyle.Scale
                return mSpinnerStyle!!
            }
        }
        mSpinnerStyle = SpinnerStyle.Translate
        return mSpinnerStyle!!
    }

    override fun onInitialized(kernel: IRefreshKernel, height: Int, extendHeight: Int) {
        val params = mWrapperView.layoutParams
        if (params is RefreshLayout.LayoutParams) {
            kernel.requestDrawBackgoundForHeader(params.backgroundColor)
        }
    }

    override fun isSupportHorizontalDrag(): Boolean {
        return false
    }

    override fun onHorizontalDrag(percentX: Float, offsetX: Int, offsetMax: Int) {}

    override fun onPullingDown(percent: Float, offset: Int, headHeight: Int, extendHeight: Int) {

    }

    override fun onReleasing(percent: Float, offset: Int, headHeight: Int, extendHeight: Int) {

    }

    override fun onRefreshReleased(layout: IRefreshLayout, headerHeight: Int, extendHeight: Int) {

    }

    override fun onStartAnimator(layout: IRefreshLayout, headHeight: Int, extendHeight: Int) {

    }

    override fun onStateChanged(refreshLayout: IRefreshLayout, oldState: RefreshState, newState: RefreshState) {

    }
}