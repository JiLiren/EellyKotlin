package com.eelly.core.widget.refresh.impl

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.PointF
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CoordinatorLayout
import android.support.v4.view.NestedScrollingChild
import android.support.v4.view.NestedScrollingParent
import android.support.v4.view.ScrollingView
import android.support.v4.view.ViewPager
import android.support.v4.widget.ListViewCompat.scrollListBy
import android.support.v4.widget.NestedScrollView
import android.support.v4.widget.Space
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.webkit.WebView
import android.widget.AbsListView
import android.widget.FrameLayout
import android.widget.ListView
import android.widget.ScrollView
import com.eelly.core.widget.refresh.api.IRefreshContent
import com.eelly.core.widget.refresh.api.IRefreshKernel
import com.eelly.core.widget.refresh.api.IRefreshLayout
import com.eelly.core.widget.refresh.api.ScrollBoundaryDecider
import com.eelly.core.widget.refresh.tools.ScrollBoundaryUtil
import java.util.concurrent.LinkedBlockingQueue

/**
 * @author Vurtne on 14-Dec-17.
 */
class RefreshContentWrapper : IRefreshContent {

    protected var mHeaderHeight = Integer.MAX_VALUE
    protected var mFooterHeight = mHeaderHeight - 1
    protected var mScrollableView: View? = null
    protected var mFixedHeader: View? = null
    protected var mFixedFooter: View? = null
    protected var mEnableRefresh = true
    protected var mEnableLoadmore = true
    protected var mMotionEvent: MotionEvent? = null
    protected var mBoundaryAdapter = ScrollBoundaryDeciderAdapter()

    /**
     * 直接内容视图
     */
    protected var mContentView: View? = null
    /**
     * 被包裹的原真实视图
     */
    protected var mRealContentView: View? = null

    constructor(view: View){
        mRealContentView = view
        mContentView = view
    }

    constructor(context: Context){
        mRealContentView = View(context)
        mContentView = mRealContentView
    }

    //<editor-fold desc="findScrollableView">
    protected fun findScrollableView(content: View?, kernel: IRefreshKernel) {
        var content = content
        mScrollableView = null
        while (mScrollableView == null || mScrollableView is NestedScrollingParent && mScrollableView
                !is NestedScrollingChild) {
            content = findScrollableViewInternal(content as ViewGroup, mScrollableView == null)
            if (content === mScrollableView) {
                break
            }
            try {//try 不能删除，不然会出现兼容性问题
                if (content is CoordinatorLayout) {
                    kernel.getRefreshLayout().setEnableNestedScroll(false)
                    wrapperCoordinatorLayout(content as ViewGroup, kernel.getRefreshLayout())
                }
            } catch (ignored: Throwable) {
            }

            mScrollableView = content
        }
    }

//    for (int i = layout.getChildCount() - 1; i >= 0; i--) {
//        View view = layout.getChildAt(i);
//        if (view instanceof AppBarLayout) {
//            ((AppBarLayout) view).addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
//                @Override
//                public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
//                    mEnableRefresh = verticalOffset >= 0;
//                    mEnableLoadmore = refreshLayout.isEnableLoadMore() && (appBarLayout.getTotalScrollRange()
//                            + verticalOffset) <= 0;
//                }
//            });
//        }
//    }
    protected fun wrapperCoordinatorLayout(layout: ViewGroup, refreshLayout: IRefreshLayout) {
        (layout.childCount - 1 downTo 0)
                .map { layout.getChildAt(it) }
                .forEach {
                    (it as? AppBarLayout)?.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
                        mEnableRefresh = verticalOffset >= 0
                        mEnableLoadmore = refreshLayout.isEnableLoadMore() &&
                                appBarLayout.totalScrollRange + verticalOffset <= 0
                    }
                }
    }

    protected fun findScrollableViewInternal(content: View, selfable: Boolean): View? {
        var scrollableView: View? = null
        val views = LinkedBlockingQueue(listOf(content))
        while (!views.isEmpty() && scrollableView == null) {
            val view = views.poll()
            if (view != null) {
                if ((selfable || view !== content) && isScrollableView(view)) {
                    scrollableView = view
                } else if (view is ViewGroup) {
                    (0 until view.childCount).mapTo(views) { view.getChildAt(it) }
                }
            }
        }
        return if (scrollableView == null) content else scrollableView
    }

    protected fun isScrollableView(view: View): Boolean {
        return (view is AbsListView
                || view is ScrollView
                || view is ScrollingView
                || view is NestedScrollingChild
                || view is NestedScrollingParent
                || view is WebView
                || view is ViewPager)
    }

    protected fun findScrollableViewByEvent(content: View, event: MotionEvent?, orgScrollableView: View): View {
        var event = event
        if (content is ViewGroup && event != null) {
            val childCount = content.childCount
            val point = PointF()
            for (i in childCount downTo 1) {
                val child = content.getChildAt(i - 1)
                if (ScrollBoundaryUtil.isTransformedTouchPointInView(content, child, event!!.x, event.y, point)) {
                    return if (child !is ViewPager && isScrollableView(child)) {
                        child
                    } else {
                        event = MotionEvent.obtain(event)
                        event!!.offsetLocation(point.x, point.y)
                        findScrollableViewByEvent(child, event, orgScrollableView)
                    }
                }
            }
        }
        return orgScrollableView
    }
    //</editor-fold>

    //<editor-fold desc="implements">
    override fun getView(): View = mContentView!!

    override fun moveSpinner(spinner: Int) {
        mRealContentView!!.translationY = spinner.toFloat()
        if (mFixedHeader != null) {
            mFixedHeader!!.translationY = Math.max(0, spinner).toFloat()
        }
        if (mFixedFooter != null) {
            mFixedFooter!!.translationY = Math.min(0, spinner).toFloat()
        }
    }

    override fun canRefresh(): Boolean {
        return mEnableRefresh && mBoundaryAdapter.canRefresh(mContentView)
    }

    override fun canLoadMore(): Boolean {
        return mEnableLoadmore && mBoundaryAdapter.canLoadmore(mContentView)
    }

    override fun measure(widthSpec: Int, heightSpec: Int) {
        mContentView!!.measure(widthSpec, heightSpec)
    }

    override fun getLayoutParams(): ViewGroup.LayoutParams {
        return mContentView!!.layoutParams
    }

    override fun getMeasuredWidth(): Int {
        return mContentView!!.measuredWidth
    }

    override fun getMeasuredHeight(): Int {
        return mContentView!!.measuredHeight
    }

    override fun layout(left: Int, top: Int, right: Int, bottom: Int) {
        mContentView!!.layout(left, top, right, bottom)
    }

    override fun getScrollableView(): View {
        return mScrollableView!!
    }

    override fun onActionDown(e: MotionEvent) {
        mMotionEvent = MotionEvent.obtain(e)
        mMotionEvent!!.offsetLocation((-mContentView!!.left).toFloat(), (-mContentView!!.top).toFloat())
        mBoundaryAdapter.setActionEvent(mMotionEvent)
        mScrollableView = findScrollableViewByEvent(mContentView!!, mMotionEvent, mScrollableView!!)
    }

    override fun onActionUpOrCancel() {
        mMotionEvent = null
    }

    override fun fling(velocity: Int) {
        if (mScrollableView is ScrollView) {
            (mScrollableView as ScrollView).fling(velocity)
        } else if (mScrollableView is AbsListView) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                (mScrollableView as AbsListView).fling(velocity)
            }
        } else if (mScrollableView is WebView) {
            (mScrollableView as WebView).flingScroll(0, velocity)
        } else if (mScrollableView is RecyclerView) {
            (mScrollableView as RecyclerView).fling(0, velocity)
        } else if (mScrollableView is NestedScrollView) {
            (mScrollableView as NestedScrollView).fling(velocity)
        }
    }

    override fun setUpComponent(kernel: IRefreshKernel, fixedHeader: View?, fixedFooter: View?) {
        findScrollableView(mContentView, kernel)

        if (fixedHeader != null || fixedFooter != null) {
            mFixedHeader = fixedHeader
            mFixedFooter = fixedFooter
            val frameLayout = FrameLayout(mContentView.getContext())
            kernel.getRefreshLayout().getLayout().removeView(mContentView)
            val layoutParams = mContentView.getLayoutParams()
            frameLayout.addView(mContentView, MATCH_PARENT, MATCH_PARENT)
            kernel.getRefreshLayout().getLayout().addView(frameLayout, layoutParams)
            mContentView = frameLayout
            if (fixedHeader != null) {
                fixedHeader.isClickable = true
                val lp = fixedHeader.layoutParams
                val parent = fixedHeader.parent as ViewGroup
                val index = parent.indexOfChild(fixedHeader)
                parent.removeView(fixedHeader)
                lp.height = measureViewHeight(fixedHeader)
                parent.addView(Space(mContentView!!.context), index, lp)
                frameLayout.addView(fixedHeader)
            }
            if (fixedFooter != null) {
                fixedFooter.isClickable = true
                val lp = fixedFooter.layoutParams
                val parent = fixedFooter.parent as ViewGroup
                val index = parent.indexOfChild(fixedFooter)
                parent.removeView(fixedFooter)
                val flp = FrameLayout.LayoutParams(lp)
                lp.height = measureViewHeight(fixedFooter)
                parent.addView(Space(mContentView!!.context), index, lp)
                flp.gravity = Gravity.BOTTOM
                frameLayout.addView(fixedFooter, flp)
            }
        }
    }

    override fun onInitialHeaderAndFooter(headerHeight: Int, footerHeight: Int) {
        mHeaderHeight = headerHeight
        mFooterHeight = footerHeight
    }

    override fun setScrollBoundaryDecider(boundary: ScrollBoundaryDecider) {
        if (boundary is ScrollBoundaryDeciderAdapter) {
            mBoundaryAdapter = boundary
        } else {
            mBoundaryAdapter.setScrollBoundaryDecider(boundary)
        }
    }

    override fun setEnableLoadMoreWhenContentNotFull(enable: Boolean) {
        mBoundaryAdapter.setEnableLoadmoreWhenContentNotFull(enable)
    }

    override fun scrollContentWhenFinished(spinner: Int): ValueAnimator.AnimatorUpdateListener {
        if (mScrollableView != null && spinner != 0) {
            if (spinner < 0 && ScrollBoundaryUtil.canScrollDown(mScrollableView)
                    || spinner > 0 && ScrollBoundaryUtil.canScrollUp(mScrollableView)) {
                return object : ValueAnimator.AnimatorUpdateListener {
                    internal var lastValue = spinner
                    override fun onAnimationUpdate(animation: ValueAnimator) {
                        val value = animation.animatedValue as Int
                        try {
                            if (mScrollableView is AbsListView) {
                                scrollListBy(mScrollableView as ListView, value - lastValue)
                            } else {
                                mScrollableView!!.scrollBy(0, value - lastValue)
                            }
                        } catch (ignored: Throwable) {
                            //根据用户反馈，此处可能会有BUG
                        }

                        lastValue = value
                    }
                }
            }
        }
        return null!!
    }
    //</editor-fold>


    //<editor-fold desc="protected">
    protected fun measureViewHeight(view: View): Int {
        var p: ViewGroup.LayoutParams? = view.layoutParams
        if (p == null) {
            p = ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        }
        val childHeightSpec: Int
        val childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0, p.width)
        childHeightSpec = if (p.height > 0) {
            View.MeasureSpec.makeMeasureSpec(p.height, View.MeasureSpec.EXACTLY)
        } else {
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        }
        view.measure(childWidthSpec, childHeightSpec)
        return view.measuredHeight
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    protected fun scrollListBy(listView: AbsListView, y: Int) {
        if (Build.VERSION.SDK_INT >= 19) {
            // Call the framework version directly
            listView.scrollListBy(y)
        } else if (listView is ListView) {
            // provide backport on earlier versions
            val firstPosition = listView.getFirstVisiblePosition()
            if (firstPosition == ListView.INVALID_POSITION) {
                return
            }

            val firstView = listView.getChildAt(0) ?: return

            val newTop = firstView.top - y
            listView.setSelectionFromTop(firstPosition, newTop)
        } else {
            listView.smoothScrollBy(y, 0)
        }
    }
    //</editor-fold>

}