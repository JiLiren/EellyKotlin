package com.eelly.core.widget.refresh.impl;

import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import com.eelly.core.widget.refresh.RefreshLayout;
import com.eelly.core.widget.refresh.api.IRefreshHeader;
import com.eelly.core.widget.refresh.api.IRefreshKernel;
import com.eelly.core.widget.refresh.api.IRefreshLayout;
import com.eelly.core.widget.refresh.constant.RefreshState;
import com.eelly.core.widget.refresh.constant.SpinnerStyle;

/**
 * @author Vurtne on 14-Dec-17.
 */


public class RefreshHeaderWrapper implements IRefreshHeader {

    private View mWrapperView;
    private SpinnerStyle mSpinnerStyle;

    public RefreshHeaderWrapper(View wrapper) {
        this.mWrapperView = wrapper;
    }

    @Override
    @NonNull
    public View getView() {
        return mWrapperView;
    }

    @Override
    public int onFinish(IRefreshLayout layout, boolean success) {
        return 0;
    }

    @Override
    @Deprecated
    public void setPrimaryColors(@ColorInt int ... colors) {

    }

    @NonNull
    @Override
    public SpinnerStyle getSpinnerStyle() {
        if (mSpinnerStyle != null) {
            return mSpinnerStyle;
        }
        ViewGroup.LayoutParams params = mWrapperView.getLayoutParams();
        if (params instanceof RefreshLayout.LayoutParams) {
            mSpinnerStyle = (( RefreshLayout.LayoutParams) params).spinnerStyle;
            if (mSpinnerStyle != null) {
                return mSpinnerStyle;
            }
        }
        if (params != null) {
            if (params.height == ViewGroup.LayoutParams.MATCH_PARENT) {
                return mSpinnerStyle = SpinnerStyle.Scale;
            }
        }
        return mSpinnerStyle = SpinnerStyle.Translate;
    }

    @Override
    public void onInitialized(IRefreshKernel kernel, int height, int extendHeight) {
        ViewGroup.LayoutParams params = mWrapperView.getLayoutParams();
        if (params instanceof RefreshLayout.LayoutParams) {
            kernel.requestDrawBackgoundForHeader(((RefreshLayout.LayoutParams) params).backgroundColor);
        }
    }

    @Override
    public boolean isSupportHorizontalDrag() {
        return false;
    }

    @Override
    public void onHorizontalDrag(float percentX, int offsetX, int offsetMax) {
    }

    @Override
    public void onPullingDown(float percent, int offset, int headHeight, int extendHeight) {

    }

    @Override
    public void onReleasing(float percent, int offset, int headHeight, int extendHeight) {

    }

    @Override
    public void onRefreshReleased(IRefreshLayout layout, int headerHeight, int extendHeight) {
        
    }

    @Override
    public void onStartAnimator(IRefreshLayout layout, int headHeight, int extendHeight) {

    }

    @Override
    public void onStateChanged(IRefreshLayout refreshLayout, RefreshState oldState, RefreshState newState) {

    }
}
