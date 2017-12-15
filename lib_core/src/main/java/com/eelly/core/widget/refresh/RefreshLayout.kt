package com.eelly.core.widget.refresh

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Build
import android.os.Handler
import android.support.annotation.ColorRes
import android.support.v4.content.ContextCompat
import android.support.v4.view.*
import android.util.AttributeSet
import android.view.*
import android.view.View.MeasureSpec.AT_MOST
import android.view.View.MeasureSpec.EXACTLY
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator
import android.webkit.WebView
import android.widget.AbsListView
import android.widget.ScrollView
import android.widget.Scroller
import com.eelly.core.R
import com.eelly.core.util.DeviceUtil
import com.eelly.core.widget.refresh.api.*
import com.eelly.core.widget.refresh.constant.DimensionStatus
import com.eelly.core.widget.refresh.constant.RefreshState
import com.eelly.core.widget.refresh.constant.SpinnerStyle
import com.eelly.core.widget.refresh.impl.RefreshContentWrapper
import com.eelly.core.widget.refresh.impl.RefreshFooterWrapper
import com.eelly.core.widget.refresh.impl.RefreshHeaderWrapper
import com.eelly.core.widget.refresh.listener.OnLoadMoreListener
import com.eelly.core.widget.refresh.listener.OnMultiPurposeListener
import com.eelly.core.widget.refresh.listener.OnRefreshListener
import com.eelly.core.widget.refresh.tools.DelayedRunable
import android.view.View.MeasureSpec.makeMeasureSpec
import android.view.View.MeasureSpec.getSize
import android.view.MotionEvent.obtain
import com.eelly.core.widget.refresh.listener.OnRefreshLoadMoreListener
import com.eelly.core.widget.refresh.tools.ViscousFluidInterpolator
import java.util.ArrayList

/**
 * @author Vurtne on 5-Dec-17.
 */
class RefreshLayout : ViewGroup(), IRefreshLayout, NestedScrollingParent, NestedScrollingChild {

    //<editor-fold desc="属性变量 property and variable">

    //<editor-fold desc="滑动属性">
    protected var mTouchSlop: Int = 0
    /**
     * 当前的 Spinner
     */
    protected var mSpinner: Int = 0
    /**
     * 最后的，的Spinner
     */
    protected var mLastSpinner: Int = 0
    /**
     * 触摸时候，的Spinner
     */
    protected var mTouchSpinner: Int = 0
    /**
     * 二楼展开时长
     */
    protected var mFloorDuration = 250
    /**
     * 回弹动画时长
     */
    protected var mReboundDuration = 250
    /**
     * 屏幕高度
     */
    protected var mScreenHeightPixels: Int = 0
    protected var mTouchX: Float = 0.toFloat()
    protected var mTouchY: Float = 0.toFloat()
    /**
     * 用于实现Header的左右拖动效果
     */
    protected var mLastTouchX: Float = 0.toFloat()
    /**
     * 用于实现多点触摸
     */
    protected var mLastTouchY: Float = 0.toFloat()
    protected var mDragRate = .5f
    protected var mIsBeingDragged: Boolean = false
    protected var mVerticalDragged: Boolean = false
    protected var mHorizontalDragged: Boolean = false
    /**
     * 父类是否处理触摸事件
     */
    protected var mSuperDispatchTouchEvent: Boolean = false
    protected var mReboundInterpolator: Interpolator ? = null
    /**
     * 固定在头部的视图Id
     */
    protected var mFixedHeaderViewId: Int = 0
    /**
     * 固定在底部的视图Id
     */
    protected var mFixedFooterViewId: Int = 0

    protected var mMinimumVelocity: Int = 0
    protected var mMaximumVelocity: Int = 0
    protected var mScroller: Scroller ? = null
    protected var mVelocityTracker: VelocityTracker ? = null

    //</editor-fold>

    //<editor-fold desc="功能属性">
    protected var mPrimaryColors: IntArray? = null
    protected var mEnableRefresh = true
    protected var mEnableLoadmore = false
    /**
     * 是否启用内容视图拖动效果
     */
    protected var mEnableHeaderTranslationContent = true
    /**
     * 是否启用内容视图拖动效果
     */
    protected var mEnableFooterTranslationContent = true
    /**
     * 是否在全部加载结束之后Footer跟随内容
     */
    protected var mEnableFooterFollowWhenLoadFinished = false
    /**
     * 是否在编辑模式下开启预览功能
     */
    protected var mEnablePreviewInEditMode = true
    /**
     * 是否启用越界回弹
     */
    protected var mEnableOverScrollBounce = true
    /**
     * 是否启用越界拖动（仿苹果效果
     */
    protected var mEnableOverScrollDrag = true
    /**
     * 是否在列表滚动到底部时自动加载更多
     */
    protected var mEnableAutoLoadmore = true
    /**
     * 是否开启纯滚动模式
     */
    protected var mEnablePureScrollMode = false
    /**
     * 是否在加载更多完成之后滚动内容显示新数据
     */
    protected var mEnableScrollContentWhenLoaded = true
    /**
     * 是否在刷新完成之后滚动内容显示新数据
     */
    protected var mEnableScrollContentWhenRefreshed = true
    /**
     * 在内容不满一页的时候，是否可以上拉加载更多
     */
    protected var mEnableLoadMoreWhenContentNotFull = true
    /**
     * 是否开启在刷新时候禁止操作内容视图
     */
    protected var mDisableContentWhenRefresh = false
    /**
     * 是否开启在刷新时候禁止操作内容视图
     */
    protected var mDisableContentWhenLoading = false
    /**
     * 数据是否全部加载完成，如果完成就不能在触发加载事件
     */
    protected var mLoadMoreFinished = false
    /**
     * 是否手动设置过Loadmore，用于智能开启
     */
    protected var mManualLoadMore = false
    /**
     * 是否手动设置过 NestedScrolling，用于智能开启
     */
    protected var mManualNestedScrolling = false
    /**
     * 是否手动设置过内容视图拖动效果
     */
    protected var mManualHeaderTranslationContent = false
    //</editor-fold>

    //<editor-fold desc="监听属性">
    protected var mRefreshListener: OnRefreshListener? = null
    protected var mLoadMoreListener: OnLoadMoreListener? = null
    protected var mOnMultiPurposeListener: OnMultiPurposeListener? = null
    protected var mScrollBoundaryDecider: ScrollBoundaryDecider ? = null
    //</editor-fold>

    //<editor-fold desc="嵌套滚动">
    protected var mParentScrollConsumed = IntArray(2)
    protected var mParentOffsetInWindow = IntArray(2)
    protected var mTotalUnconsumed: Int = 0
    protected var mNestedScrollInProgress: Boolean = false
    protected var mNestedScrollingChildHelper: NestedScrollingChildHelper ? = null
    protected var mNestedScrollingParentHelper: NestedScrollingParentHelper ? = null
    //</editor-fold>

    //<editor-fold desc="内部视图">
    /**
     * 头部高度
     */
    protected var mHeaderHeight: Int = 0
    protected var mHeaderHeightStatus = DimensionStatus.DefaultUnNotify
    /**
     * 底部高度
     */
    protected var mFooterHeight: Int = 0
    protected var mFooterHeightStatus = DimensionStatus.DefaultUnNotify

    /**
     * 扩展高度
     */
    protected var mHeaderExtendHeight: Int = 0
    /**
     * 扩展高度
     */
    protected var mFooterExtendHeight: Int = 0
    /**
     * 最大拖动比率(最大高度/Header高度)
     */
    protected var mHeaderMaxDragRate = 2.5f
    /**
     * 最大拖动比率(最大高度/Footer高度)
     */
    protected var mFooterMaxDragRate = 2.5f
    /**
     * 触发刷新距离 与 HeaderHieght 的比率
     */
    protected var mHeaderTriggerRate = 1.0f
    /**
     * 触发加载距离 与 FooterHieght 的比率
     */
    protected var mFooterTriggerRate = 1.0f
    /**
     * 下拉头部视图
     */
    protected var mRefreshHeader: IRefreshHeader? = null
    /**
     * 上拉底部视图
     */
    protected var mRefreshFooter: IRefreshFooter? = null
    /**
     * 显示内容视图
     */
    protected var mRefreshContent: IRefreshContent? = null
    //</editor-fold>

    protected var mPaint: Paint? = null
    protected var mHandler: Handler? = null
    protected var mKernel: IRefreshKernel ? = null
    protected var mDelayedRunables: MutableList<DelayedRunable>? = null

    /**
     * 主状态
     */
    protected var mState = RefreshState.None
    /**
     * 副状态（主状态刷新时候的滚动状态）
     */
    protected var mViceState = RefreshState.None

    /**
     * 竖直通信证（用于特殊事件的权限判定）
     */
    protected var mVerticalPermit = false

    protected var mLastLoadingTime: Long = 0
    protected var mLastRefreshingTime: Long = 0

    /**
     * 为Header绘制纯色背景
     */
    protected var mHeaderBackgroundColor = 0
    protected var mFooterBackgroundColor = 0

    /**
     * 为游戏Header提供独立事件
     */
    protected var mHeaderNeedTouchEventWhenRefreshing: Boolean = false
    protected var mFooterNeedTouchEventWhenLoading: Boolean = false

    protected var sManualFooterCreater = false

    protected var sFooterCreater: DefaultRefreshFooterCreater = object : DefaultRefreshFooterCreater {
        override fun createRefreshFooter(context: Context, layout: IRefreshLayout): IRefreshFooter {
            return DefaultFooter(context)
        }
    }

    protected var sHeaderCreater: DefaultRefreshHeaderCreater = object : DefaultRefreshHeaderCreater {
        override fun createRefreshHeader(context: Context, layout: IRefreshLayout): IRefreshHeader {
            return DefaultHeader(context)
        }
    }

    //</editor-fold>

    //<editor-fold desc="构造方法 construction methods">
    constructor(context: Context):this(context,null)

    constructor(context: Context, attrs: AttributeSet?):this(context, attrs,0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int):super(context, attrs, defStyleAttr){
        this.initView(context, attrs)
    }

    private fun initView(context: Context, attrs: AttributeSet?) {
        clipToPadding = false

        val configuration = ViewConfiguration.get(context)

        mScroller = Scroller(context)
        mKernel = RefreshKernelImpl(this)
        mVelocityTracker = VelocityTracker.obtain()
        mScreenHeightPixels = context.resources.displayMetrics.heightPixels
        mReboundInterpolator = ViscousFluidInterpolator()
        mTouchSlop = configuration.scaledTouchSlop
        mMinimumVelocity = configuration.scaledMinimumFlingVelocity
        mMaximumVelocity = configuration.scaledMaximumFlingVelocity

        mNestedScrollingParentHelper = NestedScrollingParentHelper(this)
        mNestedScrollingChildHelper = NestedScrollingChildHelper(this)

        val ta = context.obtainStyledAttributes(attrs, R.styleable.RefreshLayout)

        ViewCompat.setNestedScrollingEnabled(this, ta.getBoolean(R.styleable.RefreshLayout_srlEnableNestedScrolling, false))
        mDragRate = ta.getFloat(R.styleable.RefreshLayout_srlDragRate, mDragRate)
        mHeaderMaxDragRate = ta.getFloat(R.styleable.RefreshLayout_srlHeaderMaxDragRate, mHeaderMaxDragRate)
        mFooterMaxDragRate = ta.getFloat(R.styleable.RefreshLayout_srlFooterMaxDragRate, mFooterMaxDragRate)
        mHeaderTriggerRate = ta.getFloat(R.styleable.RefreshLayout_srlHeaderTriggerRate, mHeaderTriggerRate)
        mFooterTriggerRate = ta.getFloat(R.styleable.RefreshLayout_srlFooterTriggerRate, mFooterTriggerRate)
        mEnableRefresh = ta.getBoolean(R.styleable.RefreshLayout_srlEnableRefresh, mEnableRefresh)
        mReboundDuration = ta.getInt(R.styleable.RefreshLayout_srlReboundDuration, mReboundDuration)
        mEnableLoadmore = ta.getBoolean(R.styleable.RefreshLayout_srlEnableLoadMore, mEnableLoadmore)
        mHeaderHeight = ta.getDimensionPixelOffset(R.styleable.RefreshLayout_srlHeaderHeight,
                DeviceUtil.dip2Px(context, 100f))
        mFooterHeight = ta.getDimensionPixelOffset(R.styleable.RefreshLayout_srlFooterHeight,
                DeviceUtil.dip2Px(context, 60f))
        mDisableContentWhenRefresh = ta.getBoolean(R.styleable.RefreshLayout_srlDisableContentWhenRefresh, mDisableContentWhenRefresh)
        mDisableContentWhenLoading = ta.getBoolean(R.styleable.RefreshLayout_srlDisableContentWhenLoading, mDisableContentWhenLoading)
        mEnableHeaderTranslationContent = ta.getBoolean(R.styleable.RefreshLayout_srlEnableHeaderTranslationContent, mEnableHeaderTranslationContent)
        mEnableFooterTranslationContent = ta.getBoolean(R.styleable.RefreshLayout_srlEnableFooterTranslationContent, mEnableFooterTranslationContent)
        mEnablePreviewInEditMode = ta.getBoolean(R.styleable.RefreshLayout_srlEnablePreviewInEditMode, mEnablePreviewInEditMode)
        mEnableAutoLoadmore = ta.getBoolean(R.styleable.RefreshLayout_srlEnableAutoLoadMore, mEnableAutoLoadmore)
        mEnableOverScrollBounce = ta.getBoolean(R.styleable.RefreshLayout_srlEnableOverScrollBounce, mEnableOverScrollBounce)
        mEnablePureScrollMode = ta.getBoolean(R.styleable.RefreshLayout_srlEnablePureScrollMode, mEnablePureScrollMode)
        mEnableScrollContentWhenLoaded = ta.getBoolean(R.styleable.RefreshLayout_srlEnableScrollContentWhenLoaded, mEnableScrollContentWhenLoaded)
        mEnableScrollContentWhenRefreshed = ta.getBoolean(R.styleable.RefreshLayout_srlEnableScrollContentWhenRefreshed, mEnableScrollContentWhenRefreshed)
        mEnableLoadMoreWhenContentNotFull = ta.getBoolean(R.styleable.RefreshLayout_srlEnableLoadMoreWhenContentNotFull, mEnableLoadMoreWhenContentNotFull)
        mEnableFooterFollowWhenLoadFinished = ta.getBoolean(R.styleable.RefreshLayout_srlEnableFooterFollowWhenLoadFinished, mEnableFooterFollowWhenLoadFinished)
        mEnableOverScrollDrag = ta.getBoolean(R.styleable.RefreshLayout_srlEnableOverScrollDrag, mEnableOverScrollDrag)
        mFixedHeaderViewId = ta.getResourceId(R.styleable.RefreshLayout_srlFixedHeaderViewId, View.NO_ID)
        mFixedFooterViewId = ta.getResourceId(R.styleable.RefreshLayout_srlFixedFooterViewId, View.NO_ID)

        mManualLoadMore = ta.hasValue(R.styleable.RefreshLayout_srlEnableLoadMore)
        mManualNestedScrolling = ta.hasValue(R.styleable.RefreshLayout_srlEnableNestedScrolling)
        mManualHeaderTranslationContent = ta.hasValue(R.styleable.RefreshLayout_srlEnableHeaderTranslationContent)
        mHeaderHeightStatus = if (ta.hasValue(R.styleable.RefreshLayout_srlHeaderHeight)) DimensionStatus.XmlLayoutUnNotify else mHeaderHeightStatus
        mFooterHeightStatus = if (ta.hasValue(R.styleable.RefreshLayout_srlFooterHeight)) DimensionStatus.XmlLayoutUnNotify else mFooterHeightStatus

        mHeaderExtendHeight = Math.max(mHeaderHeight * (mHeaderMaxDragRate - 1), 0f).toInt()
        mFooterExtendHeight = Math.max(mFooterHeight * (mFooterMaxDragRate - 1), 0f).toInt()

        val accentColor = ta.getColor(R.styleable.RefreshLayout_srlAccentColor, 0)
        val primaryColor = ta.getColor(R.styleable.RefreshLayout_srlPrimaryColor, 0)
        if (primaryColor != 0) {
            mPrimaryColors = if (accentColor != 0) {
                intArrayOf(primaryColor, accentColor)
            } else {
                intArrayOf(primaryColor)
            }
        } else if (accentColor != 0) {
            mPrimaryColors = intArrayOf(0, accentColor)
        }

        ta.recycle()

    }
    //</editor-fold>

    //<editor-fold desc="生命周期 life cycle">
    override fun onFinishInflate() {
        super.onFinishInflate()
        val count = childCount
        if (count > 3) {
            throw RuntimeException("最多只支持3个子View，Most only support three sub view")
        }

        //定义为确认的子View索引
        val uncertains = BooleanArray(count)
        //第一次查找确认的 子View
        for (i in 0 until count) {
            val view = getChildAt(i)
            if (view is IRefreshHeader && mRefreshHeader == null) {
                mRefreshHeader = view
            } else if (view is IRefreshFooter && mRefreshFooter == null) {
                mEnableLoadmore = mEnableLoadmore || !mManualLoadMore
                mRefreshFooter = view
            } else if (mRefreshContent == null && (view is AbsListView
                    || view is WebView
                    || view is ScrollView
                    || view is ScrollingView
                    || view is NestedScrollingChild
                    || view is NestedScrollingParent
                    || view is ViewPager)) {
                mRefreshContent = RefreshContentWrapper(view)
            } else {
                //标记未确认
                uncertains[i] = true
            }
        }
        //如果有 未确认（uncertains）的子View 通过智能算法计算
        for (i in 0 until count) {
            if (uncertains[i]) {
                val view = getChildAt(i)
                if (count == 1 && mRefreshContent == null) {
                    mRefreshContent = RefreshContentWrapper(view)
                } else if (i == 0 && mRefreshHeader == null) {
                    mRefreshHeader = RefreshHeaderWrapper(view)
                } else if (count == 2 && mRefreshContent == null) {
                    mRefreshContent = RefreshContentWrapper(view)
                } else if (i == 2 && mRefreshFooter == null) {
                    mEnableLoadmore = mEnableLoadmore || !mManualLoadMore
                    mRefreshFooter = RefreshFooterWrapper(view)
                } else if (mRefreshContent == null) {
                    mRefreshContent = RefreshContentWrapper(view)
                } else if (i == 1 && count == 2 && mRefreshFooter == null) {
                    mEnableLoadmore = mEnableLoadmore || !mManualLoadMore
                    mRefreshFooter = RefreshFooterWrapper(view)
                }
            }
        }

        if (isInEditMode) {
            if (mPrimaryColors != null) {
                if (mRefreshHeader != null) {
                    mRefreshHeader!!.setPrimaryColors(mPrimaryColors!!)
                }
                if (mRefreshFooter != null) {
                    mRefreshFooter!!.setPrimaryColors(mPrimaryColors!!)
                }
            }

            //重新排序
            if (mRefreshContent != null) {
                bringChildToFront(mRefreshContent!!.getView())
            }
            if (mRefreshHeader != null && mRefreshHeader!!.getSpinnerStyle() !== SpinnerStyle.FixedBehind) {
                bringChildToFront(mRefreshHeader!!.getView())
            }
            if (mRefreshFooter != null && mRefreshFooter!!.getSpinnerStyle() !== SpinnerStyle.FixedBehind) {
                bringChildToFront(mRefreshFooter!!.getView())
            }
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (isInEditMode) {
            return
        }
        if (mHandler == null) {
            mHandler = Handler()
        }

        if (mDelayedRunables != null) {
            for (runable in mDelayedRunables!!) {
                mHandler!!.postDelayed(runable, runable.delayMillis)
            }
            mDelayedRunables!!.clear()
            mDelayedRunables = null
        }

        if (mRefreshHeader == null) {
            mRefreshHeader = sHeaderCreater.createRefreshHeader(context, this)
            if (mRefreshHeader!!.getView().layoutParams !is ViewGroup.MarginLayoutParams) {
                if (mRefreshHeader!!.getSpinnerStyle() === SpinnerStyle.Scale) {
                    addView(mRefreshHeader!!.getView(), MATCH_PARENT, MATCH_PARENT)
                } else {
                    addView(mRefreshHeader!!.getView(), MATCH_PARENT, WRAP_CONTENT)
                }
            }
        }
        if (mRefreshFooter == null) {
            mRefreshFooter = sFooterCreater.createRefreshFooter(context, this)
            mEnableLoadmore = mEnableLoadmore || !mManualLoadMore && sManualFooterCreater
            if (mRefreshFooter!!.getView().layoutParams !is ViewGroup.MarginLayoutParams) {
                if (mRefreshFooter!!.getSpinnerStyle() === SpinnerStyle.Scale) {
                    addView(mRefreshFooter!!.getView(), MATCH_PARENT, MATCH_PARENT)
                } else {
                    addView(mRefreshFooter!!.getView(), MATCH_PARENT, WRAP_CONTENT)
                }
            }
        }

        var i = 0
        val len = childCount
        while (mRefreshContent == null && i < len) {
            val view = getChildAt(i)
            if ((mRefreshHeader == null || view !== mRefreshHeader!!.getView()) &&
                    (mRefreshFooter == null || view !== mRefreshFooter!!.getView())) {
                mRefreshContent = RefreshContentWrapper(view)
            }
            i++
        }
        if (mRefreshContent == null) {
            mRefreshContent = RefreshContentWrapper(context)
        }

        val fixedHeaderView = if (mFixedHeaderViewId > 0) findViewById<View>(mFixedHeaderViewId) else null
        val fixedFooterView = if (mFixedFooterViewId > 0) findViewById<View>(mFixedFooterViewId) else null

        mRefreshContent!!.setScrollBoundaryDecider(mScrollBoundaryDecider!!)
        mRefreshContent!!.setEnableLoadMoreWhenContentNotFull(mEnableLoadMoreWhenContentNotFull)
        mRefreshContent!!.setUpComponent(mKernel!!, fixedHeaderView!!, fixedFooterView!!)

        if (mSpinner != 0) {
            notifyStateChanged(RefreshState.None)
            mSpinner = 0
            mRefreshContent!!.moveSpinner( 0)
        }

        //重新排序
        bringChildToFront(mRefreshContent!!.getView())
        if (mRefreshHeader!!.getSpinnerStyle() !== SpinnerStyle.FixedBehind) {
            bringChildToFront(mRefreshHeader!!.getView())
        }
        if (mRefreshFooter!!.getSpinnerStyle() !== SpinnerStyle.FixedBehind) {
            bringChildToFront(mRefreshFooter!!.getView())
        }

        if (mRefreshListener == null) {
            mRefreshListener = object : OnRefreshListener() {
                override fun onRefresh(layout: IRefreshLayout) {
                    layout.finishRefresh(3000)
                }
            }
        }
        if (mLoadMoreListener == null) {
            mLoadMoreListener = object : OnLoadMoreListener() {
                override fun onLoadMore(layout: IRefreshLayout) {
                    layout.finishLoadMore(2000)
                }
            }
        }
        if (mPrimaryColors != null) {
            mRefreshHeader!!.setPrimaryColors(mPrimaryColors)
            mRefreshFooter!!.setPrimaryColors(mPrimaryColors)
        }
        if (!mManualNestedScrolling && !isNestedScrollingEnabled) {
            var parent: ViewParent? = parent
            while (parent != null) {
                if (parent is NestedScrollingParent) {
                    isNestedScrollingEnabled = true
                    mManualNestedScrolling = false
                    break
                }
                parent = parent.parent
            }
        }
    }

    override fun onMeasure(widthMeasureSpec:Int,heightMeasureSpec:Int) {
        var minimumHeight = 0
        val isInEditMode = isInEditMode && mEnablePreviewInEditMode
        var i = 0
        val len = childCount
        while (i < len) {
            val child = getChildAt(i)
            if (mRefreshHeader != null && mRefreshHeader!!.getView() === child) {
                val headerView = mRefreshHeader!!.getView()
                val lp = headerView.layoutParams as LayoutParams
                val widthSpec = ViewGroup.getChildMeasureSpec(widthMeasureSpec,
                        lp.leftMargin + lp.rightMargin, lp.width)
                var heightSpec = heightMeasureSpec

                if (mHeaderHeightStatus.gteReplaceWith(DimensionStatus.XmlLayoutUnNotify)) run {
                    heightSpec = makeMeasureSpec(Math.max(mHeaderHeight - lp.bottomMargin, 0), EXACTLY)
                    headerView.measure(widthSpec, heightSpec)
                } else if (mRefreshHeader!!.getSpinnerStyle() === SpinnerStyle.MatchLayout) run {
                    var headerHeight = 0
                    if (!mHeaderHeightStatus.notifyed) {
                        measureChild(headerView, widthSpec, heightSpec)
                        headerHeight = headerView.measuredHeight
                    }
                    headerView.measure(widthSpec, makeMeasureSpec(getSize(heightSpec), EXACTLY))
                    if (headerHeight > 0 && headerHeight != headerView.measuredHeight) {
                        mHeaderHeight = headerHeight + lp.bottomMargin
                    }
                } else if (lp.height > 0) run {
                    if (mHeaderHeightStatus.canReplaceWith(DimensionStatus.XmlExactUnNotify)) {
                        mHeaderHeight = lp.height + lp.bottomMargin
                        mHeaderHeightStatus = DimensionStatus.XmlExactUnNotify
                    }
                    heightSpec = makeMeasureSpec(lp.height, EXACTLY)
                    headerView.measure(widthSpec, heightSpec)
                } else if (lp.height == WRAP_CONTENT) run {
                    heightSpec = makeMeasureSpec(Math.max(getSize(heightMeasureSpec) - lp.bottomMargin, 0), AT_MOST)
                    headerView.measure(widthSpec, heightSpec)
                    val measuredHeight = headerView.measuredHeight
                    if (measuredHeight > 0 && mHeaderHeightStatus.canReplaceWith(DimensionStatus.XmlWrapUnNotify)) {
                        mHeaderHeightStatus = DimensionStatus.XmlWrapUnNotify
                        mHeaderHeight = headerView.measuredHeight + lp.bottomMargin
                    } else if (measuredHeight <= 0) {
                        heightSpec = makeMeasureSpec(Math.max(mHeaderHeight - lp.bottomMargin, 0), EXACTLY)
                        headerView.measure(widthSpec, heightSpec)
                    }
                } else if (lp.height == MATCH_PARENT) run {
                    heightSpec = makeMeasureSpec(Math.max(mHeaderHeight - lp.bottomMargin, 0), EXACTLY)
                    headerView.measure(widthSpec, heightSpec)
                } else run { headerView.measure(widthSpec, heightSpec) }
                if (mRefreshHeader!!.getSpinnerStyle() === SpinnerStyle.Scale && !isInEditMode) {
                    val height = Math.max(0, if (isEnableRefresh()) mSpinner else 0)
                    heightSpec = makeMeasureSpec(Math.max(height - lp.bottomMargin, 0), EXACTLY)
                    headerView.measure(widthSpec, heightSpec)
                }

                if (!mHeaderHeightStatus.notifyed) {
                    mHeaderHeightStatus = mHeaderHeightStatus.notifyed()
                    mHeaderExtendHeight = Math.max(mHeaderHeight * (mHeaderMaxDragRate - 1), 0f).toInt()
                    mRefreshHeader!!.onInitialized(mKernel!!, mHeaderHeight, mHeaderExtendHeight)
                }

                if (isInEditMode && isEnableRefresh()) {
                    minimumHeight += headerView.measuredHeight
                }
            }
            if (mRefreshFooter != null && mRefreshFooter!!.getView() === child) {
                val footerView = mRefreshFooter!!.getView()
                val lp = footerView.layoutParams as LayoutParams
                val widthSpec = ViewGroup.getChildMeasureSpec(widthMeasureSpec, lp.leftMargin + lp.rightMargin, lp.width)
                var heightSpec = heightMeasureSpec
                if (mFooterHeightStatus.gteReplaceWith(DimensionStatus.XmlLayoutUnNotify)) run {
                    heightSpec = makeMeasureSpec(Math.max(mFooterHeight - lp.topMargin, 0), EXACTLY)
                    footerView.measure(widthSpec, heightSpec)
                } else if (mRefreshFooter!!.getSpinnerStyle() === SpinnerStyle.MatchLayout) run {
                    var footerHeight = 0
                    if (!mFooterHeightStatus.notifyed) {
                        measureChild(footerView, widthSpec, heightSpec)
                        footerHeight = footerView.measuredHeight
                    }
                    footerView.measure(widthSpec, makeMeasureSpec(getSize(heightSpec), EXACTLY))
                    if (footerHeight > 0 && footerHeight != footerView.measuredHeight) {
                        mHeaderHeight = footerHeight + lp.bottomMargin
                    }
                } else if (lp.height > 0) run {
                    if (mFooterHeightStatus.canReplaceWith(DimensionStatus.XmlExactUnNotify)) {
                        mFooterHeight = lp.height + lp.topMargin
                        mFooterHeightStatus = DimensionStatus.XmlExactUnNotify
                    }
                    heightSpec = makeMeasureSpec(lp.height, EXACTLY)
                    footerView.measure(widthSpec, heightSpec)
                } else if (lp.height == WRAP_CONTENT) {
                    heightSpec = makeMeasureSpec(Math.max(getSize(heightMeasureSpec) - lp.topMargin, 0), AT_MOST)
                    footerView.measure(widthSpec, heightSpec)
                    val measuredHeight = footerView.measuredHeight
                    if (measuredHeight > 0 && mFooterHeightStatus.canReplaceWith(DimensionStatus.XmlWrapUnNotify)) {
                        mFooterHeightStatus = DimensionStatus.XmlWrapUnNotify
                        mFooterHeight = footerView.measuredHeight + lp.topMargin
                    } else if (measuredHeight <= 0) {
                        heightSpec = makeMeasureSpec(Math.max(mFooterHeight - lp.topMargin, 0), EXACTLY)
                        footerView.measure(widthSpec, heightSpec)
                    }
                } else if (lp.height == MATCH_PARENT) {
                    heightSpec = makeMeasureSpec(Math.max(mFooterHeight - lp.topMargin, 0), EXACTLY)
                    footerView.measure(widthSpec, heightSpec)
                } else {
                    footerView.measure(widthSpec, heightSpec)
                }

                if (mRefreshFooter!!.getSpinnerStyle() === SpinnerStyle.Scale && !isInEditMode) {
                    val height = Math.max(0, if (mEnableLoadmore) -mSpinner else 0)
                    heightSpec = makeMeasureSpec(Math.max(height - lp.topMargin, 0), EXACTLY)
                    footerView.measure(widthSpec, heightSpec)
                }

                if (!mFooterHeightStatus.notifyed) {
                    mFooterHeightStatus = mFooterHeightStatus.notifyed()
                    mFooterExtendHeight = Math.max(mFooterHeight * (mFooterMaxDragRate - 1), 0f).toInt()
                    mRefreshFooter!!.onInitialized(mKernel!!, mFooterHeight, mFooterExtendHeight)
                }

                if (isInEditMode && mEnableLoadmore) {
                    minimumHeight += footerView.measuredHeight
                }
            }
            if (mRefreshContent != null && mRefreshContent!!.getView() === child) {
                val lp = mRefreshContent!!.getLayoutParams() as LayoutParams
                val widthSpec = ViewGroup.getChildMeasureSpec(widthMeasureSpec,
                        paddingLeft + paddingRight +
                        lp.leftMargin + lp.rightMargin, lp.width)
                val heightSpec = ViewGroup.getChildMeasureSpec(heightMeasureSpec,
                        paddingTop + paddingBottom +
                        lp.topMargin + lp.bottomMargin +
                        (if (isInEditMode && isEnableRefresh() && (mEnableHeaderTranslationContent
                                || mRefreshHeader!!.getSpinnerStyle() === SpinnerStyle.FixedBehind)) mHeaderHeight else 0) +
                                if (isInEditMode && isEnableLoadMore() && (mEnableFooterTranslationContent
                                        || mRefreshFooter!!.getSpinnerStyle() === SpinnerStyle.FixedBehind)) mFooterHeight else 0, lp.height)
                mRefreshContent!!.measure(widthSpec, heightSpec)
                mRefreshContent!!.onInitialHeaderAndFooter(mHeaderHeight, mFooterHeight)
                minimumHeight += mRefreshContent!!.getMeasuredHeight()
            }
            i++
        }
        setMeasuredDimension(View.resolveSize(suggestedMinimumWidth, widthMeasureSpec),
                View.resolveSize(minimumHeight, heightMeasureSpec))
        mLastTouchX = (measuredWidth / 2).toFloat()

    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val paddingLeft = paddingLeft
        val paddingTop = paddingTop


        var i = 0
        val len = childCount
        while (i < len) {
            val child = getChildAt(i)

            if (mRefreshContent != null && mRefreshContent!!.getView() === child) {
                val isInEditMode = isInEditMode && mEnablePreviewInEditMode
                val lp = mRefreshContent!!.getLayoutParams() as LayoutParams
                val left = paddingLeft + lp.leftMargin
                var top = paddingTop + lp.topMargin
                val right = left + mRefreshContent!!.getMeasuredWidth()
                var bottom = top + mRefreshContent!!.getMeasuredHeight()
                if (isInEditMode && isEnableRefresh() && (mEnableHeaderTranslationContent ||
                        mRefreshHeader!!.getSpinnerStyle() === SpinnerStyle.FixedBehind)) {
                    top = top + mHeaderHeight
                    bottom = bottom + mHeaderHeight
                }

                mRefreshContent!!.layout(left, top, right, bottom)
            }
            if (mRefreshHeader != null && mRefreshHeader!!.getView() === child) {
                val isInEditMode = isInEditMode && mEnablePreviewInEditMode && isEnableRefresh()
                val headerView = mRefreshHeader!!.getView()
                val lp = headerView.layoutParams as LayoutParams
                val left = lp.leftMargin
                var top = lp.topMargin
                val right = left + headerView.measuredWidth
                var bottom = top + headerView.measuredHeight
                if (!isInEditMode) {
                    if (mRefreshHeader!!.getSpinnerStyle() === SpinnerStyle.Translate) {
                        top = top - mHeaderHeight// + Math.max(0, isEnableRefresh() ? mSpinner : 0);
                        bottom = top + headerView.measuredHeight
                    } else if (mRefreshHeader!!.getSpinnerStyle() === SpinnerStyle.Scale) {
                        bottom = top + Math.max(Math.max(0, if (isEnableRefresh()) mSpinner else 0) - lp.bottomMargin, 0)
                    }
                }
                headerView.layout(left, top, right, bottom)
            }
            if (mRefreshFooter != null && mRefreshFooter!!.getView() === child) {
                val isInEditMode = isInEditMode && mEnablePreviewInEditMode && isEnableLoadMore()
                val footerView = mRefreshFooter!!.getView()
                val lp = footerView.layoutParams as LayoutParams
                val style = mRefreshFooter!!.getSpinnerStyle()
                val left = lp.leftMargin
                var top = lp.topMargin + measuredHeight - lp.bottomMargin

                if (isInEditMode
                        || style === SpinnerStyle.FixedFront
                        || style === SpinnerStyle.FixedBehind) {
                    top = top - mFooterHeight
                } else if (style === SpinnerStyle.Scale/* || style == SpinnerStyle.Translate*/) {
                    top = top - Math.max(Math.max(if (isEnableLoadMore()) -mSpinner else 0, 0) - lp.topMargin, 0)
                }

                val right = left + footerView.measuredWidth
                val bottom = top + footerView.measuredHeight
                footerView.layout(left, top, right, bottom)
            }
            i++
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        moveSpinner(0, false)
        notifyStateChanged(RefreshState.None)
        mHandler!!.removeCallbacksAndMessages(null)
        mHandler = null
        mManualLoadMore = true
        mManualNestedScrolling = true
    }

    override fun dispatchDraw(canvas: Canvas) {
        val isInEditMode = mEnablePreviewInEditMode && isInEditMode
        if (isEnableRefresh() && mHeaderBackgroundColor != 0 && (mSpinner > 0 || isInEditMode)) {
            mPaint!!.color = mHeaderBackgroundColor
            canvas.drawRect(0f, 0f, width.toFloat(), (
                    if (isInEditMode) mHeaderHeight else mSpinner).toFloat(), mPaint)
        } else if (isEnableLoadMore() && mFooterBackgroundColor != 0 && (mSpinner < 0 || isInEditMode)) {
            val height = height
            mPaint!!.color = mFooterBackgroundColor
            canvas.drawRect(0f, (height - if (
                    isInEditMode) mFooterHeight else -mSpinner).toFloat(), width.toFloat(), height.toFloat(), mPaint)
        }
        super.dispatchDraw(canvas)
    }

    override fun computeScroll() {
        val lastCurY = mScroller!!.currY
        if (mScroller!!.computeScrollOffset()) {
            val finay = mScroller!!.finalY
            if (finay > 0 && mRefreshContent!!.canLoadMore() || finay < 0 && mRefreshContent!!.canRefresh()) {
                if (mVerticalPermit) {
                    val velocity: Int
                    if (Build.VERSION.SDK_INT >= 14) {
                        velocity = mScroller!!.currVelocity.toInt()
                    } else {
                        velocity = (finay - mScroller!!.currY) / (mScroller!!.getDuration() - mScroller!!.timePassed())
                    }
                    val lastTime = AnimationUtils.currentAnimationTimeMillis() - 1000 *
                            Math.abs(mScroller!!.getCurrY() - lastCurY) / velocity
                    if (finay > 0) {// 手势向上划 Footer
                        if (isEnableLoadMore() || mEnableOverScrollDrag) {
                            if (mEnableAutoLoadmore && isEnableLoadMore() && !mLoadMoreFinished) {
                                animSpinnerBounce(-(mFooterHeight * Math.pow(1.0 * velocity / mMaximumVelocity, 0.5)).toInt())
                                if (!mState.opening && mState !== RefreshState.Loading && mState !== RefreshState.LoadFinish) {
                                    setStateDirectLoding()
                                }
                            } else if (mEnableOverScrollBounce) {
                                animSpinnerBounce(-(mFooterHeight * Math.pow(1.0 * velocity / mMaximumVelocity, 0.5)).toInt())
                            }
                        }
                    } else {// 手势向下划 Header
                        if (isEnableRefresh() || mEnableOverScrollDrag) {
                            if (mEnableOverScrollBounce) {
                                animSpinnerBounce((mHeaderHeight * Math.pow(1.0 * velocity / mMaximumVelocity, 0.5)).toInt())
                            }
                        }
                    }
                    //关闭竖直通行证
                    mVerticalPermit = false
                }
                mScroller!!.forceFinished(true)
            } else {
                //打开竖直通行证
                mVerticalPermit = true
                invalidate()
            }
        }
    }

    //</editor-fold>

    //<editor-fold desc="滑动判断 judgement of slide">
    var mFalsifyEvent: MotionEvent? = null

    override fun dispatchTouchEvent(e: MotionEvent): Boolean {

        //<editor-fold desc="多点触摸计算代码">
        //---------------------------------------------------------------------------
        //多点触摸计算代码
        //---------------------------------------------------------------------------
        val action = e.actionMasked
        val pointerUp = action == MotionEvent.ACTION_POINTER_UP
        val skipIndex = if (pointerUp) e.actionIndex else -1

        // Determine focal point
        var sumX = 0f
        var sumY = 0f
        val count = e.pointerCount
        for (i in 0 until count) {
            if (skipIndex == i) {
                continue
            }
            sumX += e.getX(i)
            sumY += e.getY(i)
        }
        val div = if (pointerUp) count - 1 else count
        val touchX = sumX / div
        val touchY = sumY / div
        if ((action == MotionEvent.ACTION_POINTER_UP || action == MotionEvent.ACTION_POINTER_DOWN) && mIsBeingDragged) {
            mTouchY += touchY - mLastTouchY
        }
        mLastTouchX = touchX
        mLastTouchY = touchY
        //---------------------------------------------------------------------------
        //</editor-fold>

        if (mRefreshContent != null) {
            //为 RefreshContent 传递当前触摸事件的坐标，用于智能判断对应坐标位置View的滚动边界和相关信息
            when (action) {
                MotionEvent.ACTION_DOWN -> {
                    mVelocityTracker!!.clear()
                    mVelocityTracker!!.addMovement(e)
                    mRefreshContent!!.onActionDown(e)
                    mScroller!!.forceFinished(true)
                }
                MotionEvent.ACTION_MOVE -> mVelocityTracker!!.addMovement(e)
                MotionEvent.ACTION_UP -> {
                    mVelocityTracker!!.computeCurrentVelocity(1000, mMaximumVelocity.toFloat())
                    mRefreshContent!!.onActionUpOrCancel()
                }
                MotionEvent.ACTION_CANCEL -> mRefreshContent!!.onActionUpOrCancel()
                else -> {
                }
            }
        }
        if (reboundAnimator != null && !interceptAnimator(action)
                || mState === RefreshState.Loading && mDisableContentWhenLoading
                || mState === RefreshState.Refreshing && mDisableContentWhenRefresh) {
            return false
        }
        if (mNestedScrollInProgress) {//嵌套滚动时，补充竖直方向不滚动，但是水平方向滚动，需要通知 onHorizontalDrag
            val totalUnconsumed = this.mTotalUnconsumed
            val ret = superDispatchTouchEvent(e)

            if (action == MotionEvent.ACTION_MOVE) {
                if (totalUnconsumed == mTotalUnconsumed) {
                    val offsetX = mLastTouchX.toInt()
                    val offsetMax = width
                    val percentX = mLastTouchX / offsetMax
                    if (isEnableRefresh() && mSpinner > 0 && mRefreshHeader != null &&
                            mRefreshHeader!!.isSupportHorizontalDrag()) {
                        mRefreshHeader!!.onHorizontalDrag(percentX, offsetX, offsetMax)
                    } else if (isEnableLoadMore() && mSpinner < 0 && mRefreshFooter != null &&
                            mRefreshFooter!!.isSupportHorizontalDrag()) {
                        mRefreshFooter!!.onHorizontalDrag(percentX, offsetX, offsetMax)
                    }
                }
            }
            return ret
        } else if (!isEnabled
                || !isEnableRefresh() && !isEnableLoadMore() && !mEnableOverScrollDrag
                || mHeaderNeedTouchEventWhenRefreshing && (mState === RefreshState.Refreshing
                || mState === RefreshState.RefreshFinish)
                || mFooterNeedTouchEventWhenLoading && (mState === RefreshState.Loading
                || mState === RefreshState.LoadFinish)) {
            return superDispatchTouchEvent(e)
        }

        when (action) {
            MotionEvent.ACTION_DOWN -> {
                mTouchX = touchX
                mTouchY = touchY
                mLastSpinner = 0
                mTouchSpinner = mSpinner
                mIsBeingDragged = false
                mSuperDispatchTouchEvent = superDispatchTouchEvent(e)
                if (mState === RefreshState.TwoLevel && mTouchY < 5 * measuredHeight / 6) {
                    mHorizontalDragged = true
                    return mSuperDispatchTouchEvent
                }
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = touchX - mTouchX
                var dy = touchY - mTouchY
                if (!mIsBeingDragged && !mHorizontalDragged) {
                    if (mVerticalDragged || Math.abs(dy) >= mTouchSlop && Math.abs(dx) < Math.abs(dy)) {
                        //滑动允许最大角度为45度
                        mVerticalDragged = true
                        if (dy > 0 && (mSpinner < 0 || (isEnableRefresh() || mEnableOverScrollDrag) &&
                                mRefreshContent!!.canRefresh())) {
                            mIsBeingDragged = true
                            mTouchY = touchY - mTouchSlop
                        } else if (dy < 0 && (mSpinner > 0 || (isEnableLoadMore() || mEnableOverScrollDrag) &&
                                mRefreshContent!!.canLoadMore())) {
                            mIsBeingDragged = true
                            mTouchY = touchY + mTouchSlop
                        }
                        if (mIsBeingDragged) {
                            dy = touchY - mTouchY
                            if (mSuperDispatchTouchEvent) {
                                e.action = MotionEvent.ACTION_CANCEL
                                superDispatchTouchEvent(e)
                            }
                            if (mSpinner > 0 || mSpinner == 0 && dy > 0) {
                                setStatePullDownToRefresh()
                            } else {
                                setStatePullUpToLoad()
                            }
                            parent.requestDisallowInterceptTouchEvent(true)
                        }
                    } else if (Math.abs(dx) >= mTouchSlop && Math.abs(dx) > Math.abs(dy) && !mVerticalDragged) {
                        mHorizontalDragged = true
                    }
                }
                if (mIsBeingDragged) {
                    var spinner = dy.toInt() + mTouchSpinner
                    if (getViceState().isHeader() && (spinner < 0 || mLastSpinner < 0) ||
                            getViceState().isFooter() && (spinner > 0 || mLastSpinner > 0)) {
                        mLastSpinner = spinner
                        val time = e.eventTime
                        if (mFalsifyEvent == null) {
                            mFalsifyEvent = obtain(time, time, MotionEvent.ACTION_DOWN, mTouchX + dx, mTouchY, 0)
                            superDispatchTouchEvent(mFalsifyEvent!!)
                        }
                        val em = obtain(time, time, MotionEvent.ACTION_MOVE, mTouchX + dx, mTouchY + spinner, 0)
                        if (mFalsifyEvent != null) {
                            superDispatchTouchEvent(em)
                        }
                        if (spinner > 0 && (isEnableRefresh() || mEnableOverScrollDrag) && mRefreshContent!!.canRefresh()) {
                            mLastTouchY = touchY
                            mTouchY = mLastTouchY
                            spinner = 0
                            mTouchSpinner = spinner
                            setStatePullDownToRefresh()
                        } else if (spinner < 0 && (isEnableLoadMore() || mEnableOverScrollDrag) &&
                                mRefreshContent!!.canLoadMore()) {
                            mLastTouchY = touchY
                            mTouchY = mLastTouchY
                            spinner = 0
                            mTouchSpinner = spinner
                            setStatePullUpToLoad()
                        }
                        if (getViceState().isHeader() && spinner < 0 || getViceState().isFooter() && spinner > 0) {
                            if (mSpinner != 0) {
                                moveSpinnerInfinitely(0f)
                            }
                            return true
                        } else if (mFalsifyEvent != null) {
                            mFalsifyEvent = null
                            em.action = MotionEvent.ACTION_CANCEL
                            superDispatchTouchEvent(em)
                        }
                    }
                    moveSpinnerInfinitely(spinner.toFloat())
                    return true
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                mVerticalDragged = false
                //关闭竖直拖动
                mHorizontalDragged = false
                //关闭水平拖动
                if (mFalsifyEvent != null) {
                    mFalsifyEvent = null
                    val time = e.eventTime
                    val ec = obtain(time, time, action, mTouchX, touchY, 0)
                    superDispatchTouchEvent(ec)
                }
                if (overSpinner()) {
                    mIsBeingDragged = false
                    //关闭拖动状态
                    return true
                } else if (mState !== mViceState && mSpinner != 0 && mState !== RefreshState.TwoLevel) {
                    // 解决刷新时，惯性丢失问题
                    val velocity = -mVelocityTracker!!.yVelocity
                    if (Math.abs(velocity) > mMinimumVelocity && velocity * mSpinner > 0) {
                        animSpinner(0)
                        if (mRefreshContent != null) {
                            mRefreshContent!!.fling(velocity.toInt())
                        }
                    }
                }
                mIsBeingDragged = false
                //关闭拖动状态
            }
            else -> {
            }
        }
        return superDispatchTouchEvent(e)
    }

    protected fun superDispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.actionMasked == MotionEvent.ACTION_UP) {
            val velocity = -mVelocityTracker!!.yVelocity
            if (Math.abs(velocity) > mMinimumVelocity) {
                if (mSpinner == 0 && mTouchSpinner == 0) {
                    //关闭竖直通行证
                    mVerticalPermit = false
                    mScroller!!.fling(0, scrollY, 0, velocity.toInt(), 0,
                            0, -Integer.MAX_VALUE, Integer.MAX_VALUE)
                    mScroller!!.computeScrollOffset()
                    invalidate()
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    /**
     * 在动画执行时，触摸屏幕，打断动画，转为拖动状态
     */
    protected fun interceptAnimator(action: Int): Boolean {
        if (reboundAnimator != null && action == MotionEvent.ACTION_DOWN) {
            if (mState === RefreshState.LoadFinish || mState === RefreshState.RefreshFinish) {
                return false
            }
            if (mState === RefreshState.PullDownCanceled) {
                setStatePullDownToRefresh()
            } else if (mState === RefreshState.PullUpCanceled) {
                setStatePullUpToLoad()
            }
            reboundAnimator!!.cancel()
            reboundAnimator = null
            return true
        }
        return false
    }

    /**
     * 这段代码来自谷歌官方的 SwipeRefreshLayout
     * 应用场景已经在英文注释中解释清楚
     * 大部分第三方下拉刷新库都保留了这段代码，本库也不例外
     */
    override fun requestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
        // if this is a List < L or another view that doesn't support nested
        // scrolling, ignore this request so that the vertical scroll event
        // isn't stolen
        val target = mRefreshContent!!.getScrollableView()
        if ((Build.VERSION.SDK_INT >= 21 || target !is AbsListView) &&
                (target == null || ViewCompat.isNestedScrollingEnabled(target))) {
            super.requestDisallowInterceptTouchEvent(disallowIntercept)
            //} else {
            // Nope.
        }
    }


    //</editor-fold>

    //<editor-fold desc="状态更改 state changes">
    protected fun notifyStateChanged(state: RefreshState) {
        val oldState = mState
        if (oldState !== state) {
            mState = state
            mViceState = state
            if (mRefreshFooter != null) {
                mRefreshFooter!!.onStateChanged(this, oldState, state)
            }
            if (mRefreshHeader != null) {
                mRefreshHeader!!.onStateChanged(this, oldState, state)
            }
            if (mOnMultiPurposeListener != null) {
                mOnMultiPurposeListener!!.onStateChanged(this, oldState, state)
            }
        }
    }

    protected fun setStatePullUpToLoad() {
        if (isEnableLoadMore() && !mLoadMoreFinished && !mState.opening) {
            notifyStateChanged(RefreshState.PullToUpLoad)
        } else {
            setViceState(RefreshState.PullToUpLoad)
        }
    }

    protected fun setStateReleaseToLoad() {
        if (isEnableLoadMore() && !mLoadMoreFinished && !mState.opening) {
            notifyStateChanged(RefreshState.ReleaseToLoad)
        } else {
            setViceState(RefreshState.ReleaseToLoad)
        }
    }

    protected fun setStatePullUpCanceled() {
        if (isEnableLoadMore() && !mLoadMoreFinished && !mState.opening) {
            notifyStateChanged(RefreshState.PullUpCanceled)
            resetStatus()
        } else {
            setViceState(RefreshState.PullUpCanceled)
        }
    }

    protected fun setStatePullDownCanceled() {
        if (!mState.opening && isEnableRefresh()) {
            notifyStateChanged(RefreshState.PullDownCanceled)
            resetStatus()
        } else {
            setViceState(RefreshState.PullDownCanceled)
        }
    }

    protected fun setStateReleaseToRefresh() {
        if (!mState.opening && isEnableRefresh()) {
            notifyStateChanged(RefreshState.ReleaseToRefresh)
        } else {
            setViceState(RefreshState.ReleaseToRefresh)
        }
    }

    protected fun setStatePullDownToRefresh() {
        if (!mState.opening && isEnableRefresh()) {
            notifyStateChanged(RefreshState.PullDownToRefresh)
        } else {
            setViceState(RefreshState.PullDownToRefresh)
        }
    }

    protected fun setStateDirectLoding() {
        if (mState !== RefreshState.Loading) {
            mLastLoadingTime = System.currentTimeMillis()
            if (mState !== RefreshState.LoadReleased) {
                if (mState !== RefreshState.ReleaseToLoad) {
                    if (mState !== RefreshState.PullToUpLoad) {
                        setStatePullUpToLoad()
                    }
                    setStateReleaseToLoad()
                }
                notifyStateChanged(RefreshState.LoadReleased)
                if (mRefreshFooter != null) {
                    mRefreshFooter!!.onLoadMoreReleased(this, mFooterHeight, mFooterExtendHeight)
                }
            }
            notifyStateChanged(RefreshState.Loading)
            if (mRefreshFooter != null) {
                mRefreshFooter!!.onStartAnimator(this@RefreshLayout, mFooterHeight, mFooterExtendHeight)
            }
            if (mLoadMoreListener != null) {
                mLoadMoreListener!!.onLoadMore(this@RefreshLayout)
            }
            if (mOnMultiPurposeListener != null) {
                mOnMultiPurposeListener!!.onLoadMore(this@RefreshLayout)
                mOnMultiPurposeListener!!.onFooterStartAnimator(mRefreshFooter!!, mFooterHeight, mFooterExtendHeight)
            }
        }
    }

    protected fun setStateLoding() {
        val listener = object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                setStateDirectLoding()
            }
        }
        notifyStateChanged(RefreshState.LoadReleased)
        val animator = animSpinner(-mFooterHeight)
        if (animator != null) {
            animator.addListener(listener)
        }
        if (mRefreshFooter != null) {
            //onLoadmoreReleased 的执行顺序定在 animSpinner 之后 onAnimationEnd 之前
            // 这样 onLoadmoreReleased 内部 可以做出 对 前面 animSpinner 的覆盖 操作
            mRefreshFooter!!.onLoadMoreReleased(this, mFooterHeight, mFooterExtendHeight)
        }
        if (mOnMultiPurposeListener != null) {
            //同 mRefreshFooter.onLoadmoreReleased 一致
            mOnMultiPurposeListener!!.onFooterReleased(mRefreshFooter!!, mFooterHeight, mFooterExtendHeight)
        }
        if (animator == null) {
            //onAnimationEnd 会改变状态为 loading 必须在 onLoadmoreReleased 之后调用
            listener.onAnimationEnd(null!!)
        }
    }

    protected fun setStateRefreshing() {
        val listener = object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                mLastRefreshingTime = System.currentTimeMillis()
                notifyStateChanged(RefreshState.Refreshing)
                if (mRefreshListener != null) {
                    mRefreshListener!!.onRefresh(this@RefreshLayout)
                }
                if (mRefreshHeader != null) {
                    mRefreshHeader!!.onStartAnimator(this@RefreshLayout, mHeaderHeight, mHeaderExtendHeight)
                }
                if (mOnMultiPurposeListener != null) {
                    mOnMultiPurposeListener!!.onRefresh(this@RefreshLayout)
                    mOnMultiPurposeListener!!.onHeaderStartAnimator(mRefreshHeader!!, mHeaderHeight, mHeaderExtendHeight)
                }
            }
        }
        notifyStateChanged(RefreshState.RefreshReleased)
        val animator = animSpinner(mHeaderHeight)
        if (animator != null) {
            animator!!.addListener(listener)
        }
        if (mRefreshHeader != null) {
            //onRefreshReleased 的执行顺序定在 animSpinner 之后 onAnimationEnd 之前
            // 这样 onRefreshReleased内部 可以做出 对 前面 animSpinner 的覆盖 操作
            mRefreshHeader!!.onRefreshReleased(this, mHeaderHeight, mHeaderExtendHeight)
        }
        if (mOnMultiPurposeListener != null) {
            //同 mRefreshHeader.onRefreshReleased 一致
            mOnMultiPurposeListener!!.onHeaderReleased(mRefreshHeader!!, mHeaderHeight, mHeaderExtendHeight)
        }
        if (animator == null) {
            //onAnimationEnd 会改变状态为 Refreshing 必须在 onRefreshReleased 之后调用
            listener.onAnimationEnd(null!!)
        }
    }

    /**
     * 重置状态
     */
    protected fun resetStatus() {
        if (mState !== RefreshState.None) {
            if (mSpinner == 0) {
                notifyStateChanged(RefreshState.None)
            }
        }
        if (mSpinner != 0) {
            animSpinner(0)
        }
    }


    protected fun getViceState(): RefreshState {
        return mViceState
    }

    protected fun setViceState(state: RefreshState) {
        if (mState.draging && mState.isHeader() !== state.isHeader()) {
            notifyStateChanged(RefreshState.None)
        }
        if (mViceState !== state) {
            mViceState = state
        }
    }

    //</editor-fold>

    //<editor-fold desc="视图位移 displacement">

    //<editor-fold desc="动画监听 Animator Listener">
    protected var reboundAnimator: ValueAnimator? = null
    protected var reboundAnimatorEndListener: Animator.AnimatorListener = object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
            reboundAnimator = null
            if (mSpinner == 0) {
                if (mState !== RefreshState.None && !mState.opening) {
                    notifyStateChanged(RefreshState.None)
                }
            } else if (mState !== mViceState) {
                setViceState(mState)
            }
        }
    }

    protected var reboundUpdateListener: ValueAnimator.AnimatorUpdateListener =
            ValueAnimator.AnimatorUpdateListener { animation -> moveSpinner(animation.animatedValue as Int, true) }
    //</editor-fold>

    protected fun animSpinner(endSpinner: Int): ValueAnimator? {
        return animSpinner(endSpinner, 0)
    }

    protected fun animSpinner(endSpinner: Int, startDelay: Int): ValueAnimator? {
        return animSpinner(endSpinner, startDelay, mReboundInterpolator!!)
    }

    /**
     * 执行回弹动画
     */
    protected fun animSpinner(endSpinner: Int, startDelay: Int, interpolator: Interpolator): ValueAnimator? {
        if (mSpinner != endSpinner) {
            if (reboundAnimator != null) {
                reboundAnimator!!.cancel()
            }
            reboundAnimator = ValueAnimator.ofInt(mSpinner, endSpinner)
            reboundAnimator!!.duration = mReboundDuration.toLong()
            reboundAnimator!!.interpolator = interpolator
            reboundAnimator!!.addUpdateListener(reboundUpdateListener)
            reboundAnimator!!.addListener(reboundAnimatorEndListener)
            reboundAnimator!!.startDelay = startDelay.toLong()
            reboundAnimator!!.start()
            return reboundAnimator
        }
        return null
    }

    /**
     * 越界回弹动画
     */
    protected fun animSpinnerBounce(bounceSpinner: Int): ValueAnimator {
        if (reboundAnimator == null) {
            var duration = mReboundDuration * 2 / 3
            mLastTouchX = (measuredWidth / 2).toFloat()
            if ((mState === RefreshState.Refreshing || mState === RefreshState.TwoLevel) && bounceSpinner > 0) {
                reboundAnimator = ValueAnimator.ofInt(mSpinner, Math.min(2 * bounceSpinner, mHeaderHeight))
                reboundAnimator!!.addListener(reboundAnimatorEndListener)
            } else if (bounceSpinner < 0 && (mState === RefreshState.Loading
                    || mEnableFooterFollowWhenLoadFinished && mLoadMoreFinished
                    || mEnableAutoLoadmore && isEnableLoadMore() && !mLoadMoreFinished
                    && mState !== RefreshState.Refreshing)) {
                reboundAnimator = ValueAnimator.ofInt(mSpinner, Math.max(7 * bounceSpinner / 2, -mFooterHeight))
                reboundAnimator!!.addListener(reboundAnimatorEndListener)
            } else if (mSpinner == 0 && mEnableOverScrollBounce) {
                if (bounceSpinner > 0) {
                    if (mState !== RefreshState.Loading) {
                        setStatePullDownToRefresh()
                    }
                    duration = Math.max(150, bounceSpinner * 250 / mHeaderHeight)
                    reboundAnimator = ValueAnimator.ofInt(0, Math.min(bounceSpinner, mHeaderHeight))
                } else {
                    if (mState !== RefreshState.Refreshing) {
                        setStatePullUpToLoad()
                    }
                    duration = Math.max(150, -bounceSpinner * 250 / mFooterHeight)
                    reboundAnimator = ValueAnimator.ofInt(0, Math.max(bounceSpinner, -mFooterHeight))
                }
                val finalDuration = duration
                reboundAnimator!!.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationStart(animation: Animator) {}

                    override fun onAnimationEnd(animation: Animator) {
                        reboundAnimator = ValueAnimator.ofInt(mSpinner, 0)
                        reboundAnimator!!.duration = finalDuration.toLong()
                        reboundAnimator!!.interpolator = DecelerateInterpolator()
                        reboundAnimator!!.addUpdateListener(reboundUpdateListener)
                        reboundAnimator!!.addListener(reboundAnimatorEndListener)
                        reboundAnimator!!.start()
                    }
                })
            }
            if (reboundAnimator != null) {
                reboundAnimator!!.duration = duration.toLong()
                reboundAnimator!!.interpolator = DecelerateInterpolator()
                reboundAnimator!!.addUpdateListener(reboundUpdateListener)
                reboundAnimator!!.start()
            }
        }
        return reboundAnimator!!
    }

    /**
     * 手势拖动结束
     * 开始执行回弹动画
     */
    protected fun overSpinner(): Boolean {
        if (mState === RefreshState.TwoLevel) {
            if (mVelocityTracker!!.getYVelocity() > -1000 && mSpinner > measuredHeight / 2) {
                val animator = animSpinner(measuredHeight)
                if (animator != null) {
                    animator.duration = mFloorDuration.toLong()
                }
            } else if (mIsBeingDragged) {
                mKernel!!.finishTwoLevel()
            }
            return mIsBeingDragged
        } else if (mState === RefreshState.Loading
                || mEnableAutoLoadmore && isEnableLoadMore() && !mLoadMoreFinished
                && mSpinner < 0 && mState !== RefreshState.Refreshing
                || mEnableFooterFollowWhenLoadFinished && mLoadMoreFinished && mSpinner < 0) {
            if (mSpinner < -mFooterHeight) {
                mTotalUnconsumed = -mFooterHeight
                animSpinner(-mFooterHeight)
            } else if (mSpinner > 0) {
                mTotalUnconsumed = 0
                animSpinner(0)
            } else {
                return false
            }
        } else if (mState === RefreshState.Refreshing) {
            if (mSpinner > mHeaderHeight) {
                mTotalUnconsumed = mHeaderHeight
                animSpinner(mHeaderHeight)
            } else if (mSpinner < 0) {
                mTotalUnconsumed = 0
                animSpinner(0)
            } else {
                return false
            }
        } else if (mState === RefreshState.PullDownToRefresh) {
            setStatePullDownCanceled()
        } else if (mState === RefreshState.PullToUpLoad) {
            setStatePullUpCanceled()
        } else if (mState === RefreshState.ReleaseToRefresh) {
            setStateRefreshing()
        } else if (mState === RefreshState.ReleaseToLoad) {
            setStateLoding()
        } else if (mState === RefreshState.ReleaseToTwoLevel) {
            notifyStateChanged(RefreshState.TwoLevelReleased)
        } else if (mSpinner != 0) {
            animSpinner(0)
        } else {
            return false
        }
        return true
    }

    protected fun moveSpinnerInfinitely(spinner: Float) {
        if (mState === RefreshState.TwoLevel && spinner > 0) {
            moveSpinner(Math.min(spinner.toInt(), measuredHeight), false)
        } else if (mState === RefreshState.Refreshing && spinner >= 0) {
            if (spinner < mHeaderHeight) {
                moveSpinner(spinner.toInt(), false)
            } else {
                val M = mHeaderExtendHeight.toDouble()
                val H = (Math.max(mScreenHeightPixels * 4 / 3, height) - mHeaderHeight).toDouble()
                val x = Math.max(0f, (spinner - mHeaderHeight) * mDragRate).toDouble()
                val y = Math.min(M * (1 - Math.pow(100.0, -x / H)), x)
                // 公式 y = M(1-100^(-x/H))
                moveSpinner(y.toInt() + mHeaderHeight, false)
            }
        } else if (spinner < 0 && (mState === RefreshState.Loading
                || mEnableFooterFollowWhenLoadFinished && mLoadMoreFinished
                || mEnableAutoLoadmore && isEnableLoadMore() && !mLoadMoreFinished)) {
            if (spinner > -mFooterHeight) {
                moveSpinner(spinner.toInt(), false)
            } else {
                val M = mFooterExtendHeight.toDouble()
                val H = (Math.max(mScreenHeightPixels * 4 / 3, height) - mFooterHeight).toDouble()
                val x = (-Math.min(0f, (spinner + mFooterHeight) * mDragRate)).toDouble()
                val y = -Math.min(M * (1 - Math.pow(100.0, -x / H)), x)
                // 公式 y = M(1-100^(-x/H))
                moveSpinner(y.toInt() - mFooterHeight, false)
            }
        } else if (spinner >= 0) {
            val M = (mHeaderExtendHeight + mHeaderHeight).toDouble()
            val H = Math.max(mScreenHeightPixels / 2, height).toDouble()
            val x = Math.max(0f, spinner * mDragRate).toDouble()
            val y = Math.min(M * (1 - Math.pow(100.0, -x / H)), x)
            // 公式 y = M(1-100^(-x/H))
            moveSpinner(y.toInt(), false)
        } else {
            val M = (mFooterExtendHeight + mFooterHeight).toDouble()
            val H = Math.max(mScreenHeightPixels / 2, height).toDouble()
            val x = (-Math.min(0f, spinner * mDragRate)).toDouble()
            val y = -Math.min(M * (1 - Math.pow(100.0, -x / H)), x)
            // 公式 y = M(1-100^(-x/H))
            moveSpinner(y.toInt(), false)
        }
        if (mEnableAutoLoadmore && isEnableLoadMore()
                && spinner < 0
                && mState !== RefreshState.Refreshing
                && mState !== RefreshState.Loading
                && mState !== RefreshState.LoadFinish
                && !mLoadMoreFinished) {
            setStateDirectLoding()
        }
    }

    /**
     * 移动滚动 Scroll
     * moveSpinner 的取名来自 谷歌官方的 @[android.support.v4.widget.SwipeRefreshLayout.moveSpinner]
     */
    protected fun moveSpinner(spinner: Int, isAnimator: Boolean) {
        if (mSpinner == spinner
                && (mRefreshHeader == null || !mRefreshHeader!!.isSupportHorizontalDrag())
                && (mRefreshFooter == null || !mRefreshFooter!!.isSupportHorizontalDrag())) {
            return
        }
        val oldSpinner = mSpinner
        this.mSpinner = spinner
        if (!isAnimator && getViceState().draging) {
            if (mSpinner > mHeaderHeight * mHeaderTriggerRate) {
                if (mState !== RefreshState.ReleaseToTwoLevel) {
                    setStateReleaseToRefresh()
                }
            } else if (-mSpinner > mFooterHeight * mFooterTriggerRate && !mLoadMoreFinished) {
                setStateReleaseToLoad()
            } else if (mSpinner < 0 && !mLoadMoreFinished) {
                setStatePullUpToLoad()
            } else if (mSpinner > 0) {
                setStatePullDownToRefresh()
            }
        }
        if (mRefreshContent != null) {
            var tspinner: Int? = null
            if (spinner >= 0) {
                if (mEnableHeaderTranslationContent || mRefreshHeader == null ||
                        mRefreshHeader!!.getSpinnerStyle() === SpinnerStyle.FixedBehind) {
                    tspinner = spinner
                } else if (oldSpinner < 0) {
                    tspinner = 0
                }
            }
            if (spinner <= 0) {
                if (mEnableFooterTranslationContent || mRefreshFooter == null ||
                        mRefreshFooter!!.getSpinnerStyle() === SpinnerStyle.FixedBehind) {
                    tspinner = spinner
                } else if (oldSpinner > 0) {
                    tspinner = 0
                }
            }
            if (tspinner != null) {
                mRefreshContent!!.moveSpinner(tspinner)
                if (mHeaderBackgroundColor != 0 && (tspinner >= 0 || oldSpinner > 0) ||
                        mFooterBackgroundColor != 0 && (tspinner <= 0 || oldSpinner < 0)) {
                    invalidate()
                }
            }
        }
        if ((spinner >= 0 || oldSpinner > 0) && mRefreshHeader != null) {

            val offset = Math.max(spinner, 0)
            val headerHeight = mHeaderHeight
            val extendHeight = mHeaderExtendHeight
            val percent = 1f * offset / mHeaderHeight

            if (isEnableRefresh() || mState === RefreshState.RefreshFinish && isAnimator) {
                if (oldSpinner != mSpinner) {
                    when (mRefreshHeader!!.getSpinnerStyle()) {
                        SpinnerStyle.Translate -> mRefreshHeader!!.getView().translationY = mSpinner.toFloat()
                        SpinnerStyle.Scale -> mRefreshHeader!!.getView().requestLayout()
                        else -> {
                        }
                    }
                    if (isAnimator) {
                        mRefreshHeader!!.onReleasing(percent, offset, headerHeight, extendHeight)
                    }
                }
                if (!isAnimator) {
                    if (mRefreshHeader!!.isSupportHorizontalDrag()) {
                        val offsetX = mLastTouchX.toInt()
                        val offsetMax = width
                        val percentX = mLastTouchX / offsetMax
                        mRefreshHeader!!.onHorizontalDrag(percentX, offsetX, offsetMax)
                        mRefreshHeader!!.onPullingDown(percent, offset, headerHeight, extendHeight)
                    } else if (oldSpinner != mSpinner) {
                        mRefreshHeader!!.onPullingDown(percent, offset, headerHeight, extendHeight)
                    }
                }
            }

            if (oldSpinner != mSpinner && mOnMultiPurposeListener != null) {
                if (isAnimator) {
                    mOnMultiPurposeListener!!.onHeaderReleasing(mRefreshHeader!!, percent, offset,
                            headerHeight, extendHeight)
                } else {
                    mOnMultiPurposeListener!!.onHeaderPulling(mRefreshHeader!!, percent, offset,
                            headerHeight, extendHeight)
                }
            }

        }
        if ((spinner <= 0 || oldSpinner < 0) && mRefreshFooter != null) {

            val offset = -Math.min(spinner, 0)
            val footerHeight = mFooterHeight
            val extendHeight = mFooterExtendHeight
            val percent = offset * 1f / mFooterHeight

            if (isEnableLoadMore() || mState === RefreshState.LoadFinish && isAnimator) {
                if (oldSpinner != mSpinner) {
                    when (mRefreshFooter!!.getSpinnerStyle()) {
                        SpinnerStyle.Translate -> mRefreshFooter!!.getView().translationY = mSpinner.toFloat()
                        SpinnerStyle.Scale -> mRefreshFooter!!.getView().requestLayout()
                        else -> {
                        }
                    }
                    if (isAnimator) {
                        mRefreshFooter!!.onPullReleasing(percent, offset, footerHeight, extendHeight)
                    }
                }

                if (!isAnimator) {
                    if (mRefreshFooter!!.isSupportHorizontalDrag()) {
                        val offsetX = mLastTouchX.toInt()
                        val offsetMax = width
                        val percentX = mLastTouchX / offsetMax
                        mRefreshFooter!!.onHorizontalDrag(percentX, offsetX, offsetMax)
                        mRefreshFooter!!.onPullingUp(percent, offset, footerHeight, extendHeight)
                    } else if (oldSpinner != mSpinner) {
                        mRefreshFooter!!.onPullingUp(percent, offset, footerHeight, extendHeight)
                    }
                }
            }

            if (oldSpinner != mSpinner && mOnMultiPurposeListener != null) {
                if (isAnimator) {
                    mOnMultiPurposeListener!!.onFooterReleasing(mRefreshFooter!!, percent, offset,
                            footerHeight, extendHeight)
                } else {
                    mOnMultiPurposeListener!!.onFooterPulling(mRefreshFooter!!, percent, offset,
                            footerHeight, extendHeight)
                }
            }
        }
    }

    //</editor-fold>

    //<editor-fold desc="布局参数 LayoutParams">
    override fun checkLayoutParams(p: ViewGroup.LayoutParams): Boolean {
        return p is LayoutParams
    }

    override fun generateDefaultLayoutParams(): LayoutParams {
        return LayoutParams(MATCH_PARENT, MATCH_PARENT)
    }

    override fun generateLayoutParams(p: ViewGroup.LayoutParams): LayoutParams {
        return LayoutParams(p)
    }

    override fun generateLayoutParams(attrs: AttributeSet): LayoutParams {
        return LayoutParams(context, attrs)
    }

    class LayoutParams : ViewGroup.MarginLayoutParams {

        var backgroundColor = 0
        var spinnerStyle: SpinnerStyle? = null

        constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
            val ta = context.obtainStyledAttributes(attrs, R.styleable.RefreshLayout_Layout)
            backgroundColor = ta.getColor(R.styleable.RefreshLayout_Layout_layout_srlBackgroundColor, backgroundColor)
            if (ta.hasValue(R.styleable.RefreshLayout_Layout_layout_srlSpinnerStyle)) {
                spinnerStyle = SpinnerStyle.values()[ta.getInt(R.styleable.RefreshLayout_Layout_layout_srlSpinnerStyle, SpinnerStyle.Translate.ordinal)]
            }
            ta.recycle()
        }

        constructor(width: Int, height: Int) : super(width, height) {}

        constructor(source: ViewGroup.MarginLayoutParams) : super(source) {}

        constructor(source: ViewGroup.LayoutParams) : super(source) {}
    }
    //</editor-fold>

    //<editor-fold desc="嵌套滚动 NestedScrolling">

    //<editor-fold desc="NestedScrollingParent">
    public override fun onStartNestedScroll(child: View, target: View, nestedScrollAxes: Int): Boolean {
        var accepted = isEnabled && isNestedScrollingEnabled &&
                nestedScrollAxes and ViewCompat.SCROLL_AXIS_VERTICAL != 0
        accepted = accepted && (isEnableRefresh() || isEnableLoadMore())
        return accepted
    }

    override fun onNestedScrollAccepted(child: View, target: View, axes: Int) {
        // Reset the counter of how much leftover scroll needs to be consumed.
        mNestedScrollingParentHelper!!.onNestedScrollAccepted(child, target, axes)
        // Dispatch up to the nested parent
        startNestedScroll(axes and ViewCompat.SCROLL_AXIS_VERTICAL)
        mTotalUnconsumed = 0
        mTouchSpinner = mSpinner
        mNestedScrollInProgress = true
    }

    override fun onNestedPreScroll(target: View, dx: Int,dy: Int, consumed: IntArray) {
        var dy = dy
        // If we are in the middle of consuming, a scroll, then we want to move the spinner back up
        // before allowing the list to scroll
        if (mState.opening) {
            val parentConsumed = mParentScrollConsumed
            if (dispatchNestedPreScroll(dx, dy, parentConsumed, null)) {
                dy -= parentConsumed[1]
            }

            //判断 mTotalUnconsumed和dy 同为负数或者正数
            if ((mState === RefreshState.Refreshing || mState === RefreshState.TwoLevel) &&
                    (dy * mTotalUnconsumed > 0 || mTouchSpinner > 0)) {
                consumed[1] = 0
                if (Math.abs(dy) > Math.abs(mTotalUnconsumed)) {
                    consumed[1] += mTotalUnconsumed
                    mTotalUnconsumed = 0
                    dy -= mTotalUnconsumed
                    if (mTouchSpinner <= 0) {
                        moveSpinnerInfinitely(0f)
                    }
                } else {
                    mTotalUnconsumed -= dy
                    consumed[1] += dy
                    dy = 0
                    moveSpinnerInfinitely((mTotalUnconsumed + mTouchSpinner).toFloat())
                }

                if (dy > 0 && mTouchSpinner > 0) {
                    if (dy > mTouchSpinner) {
                        consumed[1] += mTouchSpinner
                        mTouchSpinner = 0
                    } else {
                        mTouchSpinner -= dy
                        consumed[1] += dy
                    }
                    moveSpinnerInfinitely(mTouchSpinner.toFloat())
                }
            } else {
                if (mState === RefreshState.Loading && (dy * mTotalUnconsumed > 0 || mTouchSpinner < 0)) {
                    consumed[1] = 0
                    if (Math.abs(dy) > Math.abs(mTotalUnconsumed)) {
                        consumed[1] += mTotalUnconsumed
                        mTotalUnconsumed = 0
                        dy -= mTotalUnconsumed
                        if (mTouchSpinner >= 0) {
                            moveSpinnerInfinitely(0f)
                        }
                    } else {
                        mTotalUnconsumed -= dy
                        consumed[1] += dy
                        dy = 0
                        moveSpinnerInfinitely((mTotalUnconsumed + mTouchSpinner).toFloat())
                    }

                    if (dy < 0 && mTouchSpinner < 0) {
                        if (dy < mTouchSpinner) {
                            consumed[1] += mTouchSpinner
                            mTouchSpinner = 0
                        } else {
                            mTouchSpinner -= dy
                            consumed[1] += dy
                        }
                        moveSpinnerInfinitely(mTouchSpinner.toFloat())
                    }
                }
            }
        } else {
            if (isEnableRefresh() && dy > 0 && mTotalUnconsumed > 0) {
                if (dy > mTotalUnconsumed) {
                    consumed[1] = dy - mTotalUnconsumed
                    mTotalUnconsumed = 0
                } else {
                    mTotalUnconsumed -= dy
                    consumed[1] = dy
                }
                moveSpinnerInfinitely(mTotalUnconsumed.toFloat())
            } else if (isEnableLoadMore() && dy < 0 && mTotalUnconsumed < 0) {
                if (dy < mTotalUnconsumed) {
                    consumed[1] = dy - mTotalUnconsumed
                    mTotalUnconsumed = 0
                } else {
                    mTotalUnconsumed -= dy
                    consumed[1] = dy
                }
                moveSpinnerInfinitely(mTotalUnconsumed.toFloat())
            }

            // If a client layout is using a custom start position for the circle
            // view, they mean to hide it again before scrolling the child view
            // If we get back to mTotalUnconsumed == 0 and there is more to go, hide
            // the circle so it isn't exposed if its blocking content is moved
            //        if (mUsingCustomStart && dy > 0 && mTotalUnconsumed == 0
            //                && Math.abs(dy - consumed[1]) > 0) {
            //            mCircleView.setVisibility(View.GONE);
            //        }

            // Now let our nested parent consume the leftovers
            val parentConsumed = mParentScrollConsumed
            if (dispatchNestedPreScroll(dx - consumed[0], dy - consumed[1],
                    parentConsumed, null)) {
                consumed[0] += parentConsumed[0]
                consumed[1] += parentConsumed[1]
            }
        }

    }

    override fun getNestedScrollAxes(): Int {
        return mNestedScrollingParentHelper!!.getNestedScrollAxes()
    }

    override fun onStopNestedScroll(target: View) {
        mNestedScrollingParentHelper!!.onStopNestedScroll(target)
        mNestedScrollInProgress = false
        // Finish the spinner for nested scrolling if we ever consumed any
        // unconsumed nested scroll
        //        if (!mState.opening) {
        //        }
        mTotalUnconsumed = 0
        overSpinner()
        // Dispatch up our nested parent
        stopNestedScroll()
    }

    override fun onNestedScroll(target: View, dxConsumed: Int, dyConsumed: Int,
                                dxUnconsumed: Int, dyUnconsumed: Int) {
        // Dispatch up to the nested parent first
        dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed,
                mParentOffsetInWindow)

        // This is a bit of a hack. Nested scrolling works from the bottom up, and as we are
        // sometimes between two nested scrolling views, we need a way to be able to know when any
        // nested scrolling parent has stopped handling events. We do that by using the
        // 'offset in window 'functionality to see if we have been moved from the event.
        // This is a decent indication of whether we should take over the event stream or not.

        val dy = dyUnconsumed + mParentOffsetInWindow[1]
        if (mState.opening) {
            if (isEnableRefresh() && dy < 0 && (mRefreshContent == null || mRefreshContent!!.canRefresh())) {
                mTotalUnconsumed += Math.abs(dy)
                moveSpinnerInfinitely((mTotalUnconsumed + mTouchSpinner).toFloat())
            } else if (isEnableLoadMore() && dy > 0 && (mRefreshContent == null || mRefreshContent!!.canLoadMore())) {
                mTotalUnconsumed -= Math.abs(dy)
                moveSpinnerInfinitely((mTotalUnconsumed + mTouchSpinner).toFloat())
            }
        } else {
            if (isEnableRefresh() && dy < 0 && (mRefreshContent == null || mRefreshContent!!.canRefresh())) {
                if (mState === RefreshState.None) {
                    setStatePullDownToRefresh()
                }
                mTotalUnconsumed += Math.abs(dy)
                moveSpinnerInfinitely(mTotalUnconsumed.toFloat())
            } else if (isEnableLoadMore() && dy > 0
                    && (mRefreshContent == null || mRefreshContent!!.canLoadMore())) {
                if (mState === RefreshState.None && !mLoadMoreFinished) {
                    setStatePullUpToLoad()
                }
                mTotalUnconsumed -= Math.abs(dy)
                moveSpinnerInfinitely(mTotalUnconsumed.toFloat())
            }
        }
    }

    override fun onNestedPreFling(target: View, velocityX: Float, velocityY: Float): Boolean {
        if (mSpinner != 0 && mState.opening) {
            animSpinner(0)
        }
        return reboundAnimator != null || mState === RefreshState.ReleaseToRefresh
                || mState === RefreshState.ReleaseToLoad || mState === RefreshState.PullDownToRefresh
                && mSpinner > 0 || mState === RefreshState.PullToUpLoad && mSpinner > 0
                || dispatchNestedPreFling(velocityX, velocityY)
    }

    override fun onNestedFling(target: View, velocityX: Float, velocityY: Float, consumed: Boolean): Boolean {
        return dispatchNestedFling(velocityX, velocityY, consumed)
    }
    //</editor-fold>

    //<editor-fold desc="NestedScrollingChild">
    override fun setNestedScrollingEnabled(enabled: Boolean) {
        mManualNestedScrolling = true
        mNestedScrollingChildHelper!!.setNestedScrollingEnabled(enabled)
    }

    override fun isNestedScrollingEnabled(): Boolean {
        return mNestedScrollingChildHelper!!.isNestedScrollingEnabled()
    }

    override fun startNestedScroll(axes: Int): Boolean {
        return mNestedScrollingChildHelper!!.startNestedScroll(axes)
    }

    override fun stopNestedScroll() {
        mNestedScrollingChildHelper!!.stopNestedScroll()
    }

    override fun hasNestedScrollingParent(): Boolean {
        return mNestedScrollingChildHelper!!.hasNestedScrollingParent()
    }

    override fun dispatchNestedScroll(dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int,
                                      dyUnconsumed: Int, offsetInWindow: IntArray?): Boolean {
        return mNestedScrollingChildHelper!!.dispatchNestedScroll(dxConsumed, dyConsumed,
                dxUnconsumed, dyUnconsumed, offsetInWindow)
    }

    override fun dispatchNestedPreScroll(dx: Int, dy: Int, consumed: IntArray?, offsetInWindow: IntArray?): Boolean {
        return mNestedScrollingChildHelper!!.dispatchNestedPreScroll(
                dx, dy, consumed, offsetInWindow)
    }

    override fun dispatchNestedFling(velocityX: Float, velocityY: Float, consumed: Boolean): Boolean {
        return mNestedScrollingChildHelper!!.dispatchNestedFling(velocityX, velocityY, consumed)
    }

    override fun dispatchNestedPreFling(velocityX: Float, velocityY: Float): Boolean {
        return mNestedScrollingChildHelper!!.dispatchNestedPreFling(velocityX, velocityY)
    }
    //</editor-fold>

    //</editor-fold>

    //<editor-fold desc="开放接口 open interface">
    override fun setFooterHeight(heightDp: Float): IRefreshLayout {
        return setFooterHeightPx(DeviceUtil.dip2Px(context,heightDp))
    }

    override fun setFooterHeightPx(heightPx: Int): IRefreshLayout {
        if (mFooterHeightStatus.canReplaceWith(DimensionStatus.CodeExact)) {
            mFooterHeight = heightPx
            mFooterExtendHeight = Math.max(heightPx * (mFooterMaxDragRate - 1), 0f).toInt()
            mFooterHeightStatus = DimensionStatus.CodeExactUnNotify
            if (mRefreshFooter != null) {
                mRefreshFooter!!.getView().requestLayout()
            }
        }
        return this
    }

    override fun setHeaderHeight(heightDp: Float): IRefreshLayout {
        return setHeaderHeightPx(DeviceUtil.dip2Px(context,heightDp))
    }

    override fun setHeaderHeightPx(heightPx: Int): IRefreshLayout {
        if (mHeaderHeightStatus.canReplaceWith(DimensionStatus.CodeExact)) {
            mHeaderHeight = heightPx
            mHeaderExtendHeight = Math.max(heightPx * (mHeaderMaxDragRate - 1), 0f).toInt()
            mHeaderHeightStatus = DimensionStatus.CodeExactUnNotify
            if (mRefreshHeader != null) {
                mRefreshHeader!!.getView().requestLayout()
            }
        }
        return this
    }

    override fun setDragRate(rate: Float): IRefreshLayout {
        this.mDragRate = rate
        return this
    }

    /**
     * 设置下拉最大高度和Header高度的比率（将会影响可以下拉的最大高度）
     */
    override fun setHeaderMaxDragRate(rate: Float): IRefreshLayout {
        this.mHeaderMaxDragRate = rate
        this.mHeaderExtendHeight = Math.max(mHeaderHeight * (mHeaderMaxDragRate - 1), 0f).toInt()
        if (mRefreshHeader != null && mHandler != null) {
            mRefreshHeader!!.onInitialized(mKernel!!, mHeaderHeight, mHeaderExtendHeight)
        } else {
            mHeaderHeightStatus = mHeaderHeightStatus.unNotify()
        }
        return this
    }

    /**
     * 设置上啦最大高度和Footer高度的比率（将会影响可以上啦的最大高度）
     */
    override fun setFooterMaxDragRate(rate: Float): IRefreshLayout {
        this.mFooterMaxDragRate = rate
        this.mFooterExtendHeight = Math.max(mFooterHeight * (mFooterMaxDragRate - 1), 0f).toInt()
        if (mRefreshFooter != null && mHandler != null) {
            mRefreshFooter!!.onInitialized(mKernel!!, mFooterHeight, mFooterExtendHeight)
        } else {
            mFooterHeightStatus = mFooterHeightStatus.unNotify()
        }
        return this
    }

    /**
     * 设置 触发刷新距离 与 HeaderHieght 的比率
     */
    override fun setHeaderTriggerRate(rate: Float): IRefreshLayout {
        this.mHeaderTriggerRate = rate
        return this
    }

    /**
     * 设置 触发加载距离 与 FooterHieght 的比率
     */
    override fun setFooterTriggerRate(rate: Float): IRefreshLayout {
        this.mFooterTriggerRate = rate
        return this
    }

    /**
     * 设置回弹显示插值器
     */
    override fun setReboundInterpolator(interpolator: Interpolator): IRefreshLayout {
        this.mReboundInterpolator = interpolator
        return this
    }

    /**
     * 设置回弹动画时长
     */
    override fun setReboundDuration(duration: Int): IRefreshLayout {
        this.mReboundDuration = duration
        return this
    }

    /**
     * 设置是否启用上啦加载更多（默认启用）
     */
    override fun setEnableLoadMore(enable: Boolean): IRefreshLayout {
        this.mManualLoadMore = true
        this.mEnableLoadmore = enable
        return this
    }

    /**
     * 是否启用下拉刷新（默认启用）
     */
    override fun setEnableRefresh(enable: Boolean): IRefreshLayout {
        this.mEnableRefresh = enable
        return this
    }

    /**
     * 设置是否启用内容视图拖动效果
     */
    override fun setEnableHeaderTranslationContent(enable: Boolean): IRefreshLayout {
        this.mEnableHeaderTranslationContent = enable
        this.mManualHeaderTranslationContent = true
        return this
    }

    /**
     * 设置是否启用内容视图拖动效果
     */
    override fun setEnableFooterTranslationContent(enable: Boolean): IRefreshLayout {
        this.mEnableFooterTranslationContent = enable
        return this
    }

    /**
     * 设置是否开启在刷新时候禁止操作内容视图
     */
    override fun setDisableContentWhenRefresh(disable: Boolean): IRefreshLayout {
        this.mDisableContentWhenRefresh = disable
        return this
    }

    /**
     * 设置是否开启在加载时候禁止操作内容视图
     */
    override fun setDisableContentWhenLoading(disable: Boolean): IRefreshLayout {
        this.mDisableContentWhenLoading = disable
        return this
    }

    /**
     * 设置是否监听列表在滚动到底部时触发加载事件
     */
    override fun setEnableAutoLoadMore(enable: Boolean): IRefreshLayout {
        this.mEnableAutoLoadmore = enable
        return this
    }

    /**
     * 设置是否启用越界回弹
     */
    override fun setEnableOverScrollBounce(enable: Boolean): IRefreshLayout {
        this.mEnableOverScrollBounce = enable
        return this
    }

    /**
     * 设置是否开启纯滚动模式
     */
    override fun setEnablePureScrollMode(enable: Boolean): IRefreshLayout {
        this.mEnablePureScrollMode = enable
        return this
    }

    /**
     * 设置是否在加载更多完成之后滚动内容显示新数据
     */
    override fun setEnableScrollContentWhenLoaded(enable: Boolean): IRefreshLayout {
        this.mEnableScrollContentWhenLoaded = enable
        return this
    }

    /**
     * 是否在刷新完成之后滚动内容显示新数据
     */
    override fun setEnableScrollContentWhenRefreshed(enable: Boolean): IRefreshLayout {
        this.mEnableScrollContentWhenRefreshed = enable
        return this
    }

    /**
     * 设置在内容不满一页的时候，是否可以上拉加载更多
     */
    override fun setEnableLoadMoreWhenContentNotFull(enable: Boolean): IRefreshLayout {
        this.mEnableLoadMoreWhenContentNotFull = enable
        if (mRefreshContent != null) {
            mRefreshContent!!.setEnableLoadMoreWhenContentNotFull(enable)
        }
        return this
    }

    /**
     * 设置是否启用越界拖动（仿苹果效果）
     */
    override fun setEnableOverScrollDrag(enable: Boolean): IRefreshLayout {
        this.mEnableOverScrollDrag = enable
        return this
    }

    /**
     * 设置是否在全部加载结束之后Footer跟随内容
     */
    override fun setEnableFooterFollowWhenLoadFinished(enable: Boolean): IRefreshLayout {
        this.mEnableFooterFollowWhenLoadFinished = enable
        return this
    }

    /**
     * 设置是会否启用嵌套滚动功能（默认关闭+智能开启）
     */
    override fun setEnableNestedScroll(enabled: Boolean): IRefreshLayout {
        isNestedScrollingEnabled = enabled
        return this
    }

    /**
     * 设置指定的Header
     */
    override fun setRefreshHeader(header: IRefreshHeader): IRefreshLayout {
        return setRefreshHeader(header, MATCH_PARENT, WRAP_CONTENT)
    }

    /**
     * 设置指定的Header
     */
    override fun setRefreshHeader(header: IRefreshHeader, width: Int, height: Int): IRefreshLayout {
        if (header != null) {
            if (mRefreshHeader != null) {
                removeView(mRefreshHeader!!.getView())
            }
            this.mRefreshHeader = header
            this.mHeaderHeightStatus = mHeaderHeightStatus.unNotify()
            if (header.getSpinnerStyle() === SpinnerStyle.FixedBehind) {
                this.addView(mRefreshHeader!!.getView(), 0, LayoutParams(width, height))
            } else {
                this.addView(mRefreshHeader!!.getView(), width, height)
            }
        }
        return this
    }

    /**
     * 设置指定的Footer
     */
    override fun setRefreshFooter(footer: IRefreshFooter): IRefreshLayout {
        return setRefreshFooter(footer, MATCH_PARENT, WRAP_CONTENT)
    }

    /**
     * 设置指定的Footer
     */
    override fun setRefreshFooter(footer: IRefreshFooter, width: Int, height: Int): IRefreshLayout {
        if (footer != null) {
            if (mRefreshFooter != null) {
                removeView(mRefreshFooter!!.getView())
            }
            this.mRefreshFooter = footer
            this.mFooterHeightStatus = mFooterHeightStatus.unNotify()
            this.mEnableLoadmore = !mManualLoadMore || mEnableLoadmore
            if (mRefreshFooter!!.getSpinnerStyle() === SpinnerStyle.FixedBehind) {
                this.addView(mRefreshFooter!!.getView(), 0, LayoutParams(width, height))
            } else {
                this.addView(mRefreshFooter!!.getView(), width, height)
            }
        }
        return this
    }

    /**
     * 设置指定的Content
     */
    override fun setRefreshContent(content: View): IRefreshLayout {
        return setRefreshContent(content, MATCH_PARENT, MATCH_PARENT)
    }

    /**
     * 设置指定的Content
     */
    override fun setRefreshContent(content: View, width: Int, height: Int): IRefreshLayout {
        if (content != null) {
            if (mRefreshContent != null) {
                removeView(mRefreshContent!!.getView())
            }
            addView(content, 0, LayoutParams(width, height))
            if (mRefreshHeader != null && mRefreshHeader!!.getSpinnerStyle() === SpinnerStyle.FixedBehind) {
                bringChildToFront(content)
                if (mRefreshFooter != null && mRefreshFooter!!.getSpinnerStyle() !== SpinnerStyle.FixedBehind) {
                    bringChildToFront(mRefreshFooter!!.getView())
                }
            } else if (mRefreshFooter != null && mRefreshFooter!!.getSpinnerStyle() === SpinnerStyle.FixedBehind) {
                bringChildToFront(content)
                if (mRefreshHeader != null && mRefreshHeader!!.getSpinnerStyle() === SpinnerStyle.FixedBehind) {
                    bringChildToFront(mRefreshHeader!!.getView())
                }
            }
            mRefreshContent = RefreshContentWrapper(content)
            if (mHandler != null) {
                val fixedHeaderView = if (mFixedHeaderViewId > 0) findViewById<View>(mFixedHeaderViewId) else null
                val fixedFooterView = if (mFixedFooterViewId > 0) findViewById<View>(mFixedFooterViewId) else null

                mRefreshContent!!.setScrollBoundaryDecider(mScrollBoundaryDecider!!)
                mRefreshContent!!.setEnableLoadMoreWhenContentNotFull(mEnableLoadMoreWhenContentNotFull)
                mRefreshContent!!.setUpComponent(mKernel!!, fixedHeaderView!!, fixedFooterView!!)
            }
        }
        return this
    }

    /**
     * 获取底部上啦组件的实现
     */
    override fun getRefreshFooter(): IRefreshFooter? {
        return mRefreshFooter
    }

    /**
     * 获取顶部下拉组件的实现
     */
    override fun getRefreshHeader(): IRefreshHeader? {
        return mRefreshHeader
    }

    /**
     * 获取状态
     */
    override fun getState(): RefreshState {
        return mState
    }

    /**
     * 获取实体布局视图
     */
    override fun getLayout(): RefreshLayout {
        return this
    }

    /**
     * 单独设置刷新监听器
     */
    override fun setOnRefreshListener(listener: OnRefreshListener): IRefreshLayout {
        this.mRefreshListener = listener
        return this
    }

    /**
     * 单独设置加载监听器
     */
    override fun setOnLoadMoreListener(listener: OnLoadMoreListener): IRefreshLayout {
        this.mLoadMoreListener = listener
        this.mEnableLoadmore = mEnableLoadmore || !mManualLoadMore && listener != null
        return this
    }

    /**
     * 同时设置刷新和加载监听器
     */
    override fun setOnRefreshLoadMoreListener(listener: OnRefreshLoadMoreListener): IRefreshLayout {
        this.mRefreshListener = listener
        this.mLoadMoreListener = listener
        this.mEnableLoadmore = mEnableLoadmore || !mManualLoadMore && listener != null
        return this
    }

    /**
     * 设置多功能监听器
     */
    override fun setOnMultiPurposeListener(listener: OnMultiPurposeListener): IRefreshLayout {
        this.mOnMultiPurposeListener = listener
        return this
    }

    /**
     * 设置主题颜色
     */
    override fun setPrimaryColorsId(@ColorRes vararg primaryColorId: IntArray): IRefreshLayout {
        val colors = IntArray(primaryColorId.size)
        for (i in primaryColorId.indices) {
            colors[i] = ContextCompat.getColor(context, primaryColorId[i])
        }
        setPrimaryColors(*colors)
        return this
    }

    /**
     * 设置主题颜色
     */
    override fun setPrimaryColors(vararg colors: IntArray): IRefreshLayout {
        if (mRefreshHeader != null) {
            mRefreshHeader!!.setPrimaryColors(colors)
        }
        if (mRefreshFooter != null) {
            mRefreshFooter!!.setPrimaryColors(colors)
        }
        mPrimaryColors = colors
        return this
    }

    /**
     * 设置滚动边界
     */
    override fun setScrollBoundaryDecider(boundary: ScrollBoundaryDecider): IRefreshLayout {
        mScrollBoundaryDecider = boundary
        if (mRefreshContent != null) {
            mRefreshContent!!.setScrollBoundaryDecider(boundary)
        }
        return this
    }

    /**
     * 设置数据全部加载完成，将不能再次触发加载功能
     */
    override fun setLoadMoreFinished(finished: Boolean): IRefreshLayout {
        mLoadMoreFinished = finished
        if (mRefreshFooter != null) {
            mRefreshFooter!!.setLoadMoreFinished(finished)
        }
        return this
    }

    /**
     * 完成刷新
     */
    override fun finishRefresh(): IRefreshLayout {
        val passTime = System.currentTimeMillis() - mLastRefreshingTime
        //保证刷新动画有1000毫秒的时间
        return finishRefresh(Math.max(0, 1000 - passTime.toInt()))
    }

    /**
     * 完成加载
     */
    override fun finishLoadMore(): IRefreshLayout {
        val passTime = System.currentTimeMillis() - mLastLoadingTime
        //保证加载动画有1000毫秒的时间
        return finishLoadMore(Math.max(0, 1000 - passTime.toInt()))
    }

    /**
     * 完成刷新
     */
    override fun finishRefresh(delayed: Int): IRefreshLayout {
        return finishRefresh(delayed, true)
    }

    /**
     * 完成刷新
     */
    override fun finishRefresh(success: Boolean): IRefreshLayout {
        val passTime = System.currentTimeMillis() - mLastRefreshingTime
        //保证加载动画有1000毫秒的时间
        return finishRefresh(if (success) Math.max(0, 1000 - passTime.toInt()) else 0, success)
    }

    /**
     * 完成刷新
     */
    override fun finishRefresh(delayed: Int, success: Boolean): IRefreshLayout {
        postDelayed({
            if (mState === RefreshState.Refreshing) {
                if (mRefreshHeader != null && mRefreshContent != null) {
                    val startDelay = mRefreshHeader!!.onFinish(this@RefreshLayout, success)
                    if (startDelay < Integer.MAX_VALUE) {
                        if (mIsBeingDragged) {
                            mTouchSpinner = 0
                            mTouchY = mLastTouchY
                            mIsBeingDragged = false
                            val time = System.currentTimeMillis()
                            superDispatchTouchEvent(obtain(time, time, MotionEvent.ACTION_DOWN, mLastTouchX, mTouchY + mSpinner, 0))
                        }
                        notifyStateChanged(RefreshState.RefreshFinish)
                    }
                    if (mOnMultiPurposeListener != null) {
                        mOnMultiPurposeListener!!.onHeaderFinish(mRefreshHeader!!, success)
                    }
                    if (startDelay < Integer.MAX_VALUE) {
                        if (mSpinner > 0) {
                            var updateListener: ValueAnimator.AnimatorUpdateListener? = null
                            val valueAnimator = animSpinner(0, startDelay)
                            if (mEnableScrollContentWhenRefreshed) {
                                updateListener = mRefreshContent!!.scrollContentWhenFinished(mSpinner)
                            }
                            if (valueAnimator != null && updateListener != null) {
                                valueAnimator.addUpdateListener(updateListener)
                            }
                        } else {
                            moveSpinner(0, true)
                            resetStatus()
                        }
                    }
                } else {
                    resetStatus()
                }
            }
        }, (if (delayed <= 0) 1 else delayed).toLong())
        return this
    }

    /**
     * 完成加载
     */
    override fun finishLoadMore(delayed: Int): IRefreshLayout {
        return finishLoadMore(delayed, true)
    }

    /**
     * 完成加载
     */
    override fun finishLoadMore(success: Boolean): IRefreshLayout {
        val passTime = System.currentTimeMillis() - mLastLoadingTime
        return finishLoadMore(if (success) Math.max(0, 1000 - passTime.toInt()) else 0, success)
    }

    /**
     * 完成加载
     */
    override fun finishLoadMore(delayed: Int, success: Boolean): IRefreshLayout {
        return finishLoadMore(delayed, success, false)
    }

    /**
     * 完成加载
     */
    override fun finishLoadMore(delayed: Int, success: Boolean, noMoreData: Boolean): IRefreshLayout {
        postDelayed({
            if (mState === RefreshState.Loading) {
                if (mRefreshFooter != null && mRefreshContent != null) {
                    val startDelay = mRefreshFooter!!.onFinish(this@RefreshLayout, success)
                    if (startDelay < Integer.MAX_VALUE) {
                        if (mIsBeingDragged) {
                            mTouchSpinner = 0
                            mTouchY = mLastTouchY
                            mIsBeingDragged = false
                            val time = System.currentTimeMillis()
                            superDispatchTouchEvent(obtain(time, time, MotionEvent.ACTION_DOWN, mLastTouchX, mTouchY + mSpinner, 0))
                        }
                        notifyStateChanged(RefreshState.LoadFinish)
                    }
                    if (mOnMultiPurposeListener != null) {
                        mOnMultiPurposeListener!!.onFooterFinish(mRefreshFooter!!, success)
                    }
                    if (startDelay < Integer.MAX_VALUE) {
                        postDelayed({
                            var updateListener: ValueAnimator.AnimatorUpdateListener? = null
                            if (mEnableScrollContentWhenLoaded && mSpinner < 0) {
                                updateListener = mRefreshContent!!.scrollContentWhenFinished(mSpinner)
                            }
                            if (updateListener != null) {
                                updateListener.onAnimationUpdate(ValueAnimator.ofInt(0, 0))
                            }
                            if (updateListener != null || mSpinner >= 0) {
                                moveSpinner(0, true)
                                resetStatus()
                                if (noMoreData) {
                                    setLoadMoreFinished(true)
                                }
                            } else {
                                val valueAnimator = animSpinner(0, startDelay)
                                if (valueAnimator != null && noMoreData) {
                                    valueAnimator.addListener(object : AnimatorListenerAdapter() {
                                        override fun onAnimationEnd(animation: Animator) {
                                            setLoadMoreFinished(true)
                                        }
                                    })
                                }
                            }
                        }, (if (mSpinner < 0) startDelay else 0).toLong())
                    }
                } else {
                    resetStatus()
                }
            } else if (noMoreData) {
                setLoadMoreFinished(true)
            }
        }, (if (delayed <= 0) 1 else delayed).toLong())
        return this
    }

    /**
     * 完成加载并标记没有更多数据
     */
    override fun finishLoadMoreWithNoMoreData(): IRefreshLayout {
        val passTime = System.currentTimeMillis() - mLastLoadingTime
        return finishLoadMore(Math.max(0, 1000 - passTime.toInt()), true, true)
    }

    /**
     * 恢复没有更多数据的原始状态
     */
    override fun resetNoMoreData(): IRefreshLayout {
        setLoadMoreFinished(false)
        return this
    }

    /**
     * 是否正在刷新
     */
    override fun isRefreshing(): Boolean {
        return mState === RefreshState.Refreshing
    }

    /**
     * 是否正在加载
     */
    override fun isLoading(): Boolean {
        return mState === RefreshState.Loading
    }

    /**
     * 自动刷新
     */
    override fun autoRefresh(): Boolean {
        return autoRefresh(if (mHandler == null) 400 else 0)
    }

    /**
     * 自动刷新
     */
    override fun autoRefresh(delayed: Int): Boolean {
        return autoRefresh(delayed, mReboundDuration, 1f *
                (mHeaderHeight + mHeaderExtendHeight / 2) / mHeaderHeight)
    }

    /**
     * 自动刷新
     */
    override fun autoRefresh(delayed: Int, duration: Int, dragrate: Float): Boolean {
        if (mState === RefreshState.None && isEnableRefresh()) {
            if (reboundAnimator != null) {
                reboundAnimator!!.cancel()
            }
            val runnable = Runnable {
                reboundAnimator = ValueAnimator.ofInt(mSpinner, (mHeaderHeight * dragrate).toInt())
                reboundAnimator!!.duration = duration.toLong()
                reboundAnimator!!.interpolator = DecelerateInterpolator()
                reboundAnimator!!.addUpdateListener(ValueAnimator.AnimatorUpdateListener { animation -> moveSpinner(animation.animatedValue as Int, false) })
                reboundAnimator!!.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationStart(animation: Animator) {
                        mLastTouchX = (measuredWidth / 2).toFloat()
                        setStatePullDownToRefresh()
                    }

                    override fun onAnimationEnd(animation: Animator) {
                        reboundAnimator = null
                        if (mState !== RefreshState.ReleaseToRefresh) {
                            setStateReleaseToRefresh()
                        }
                        overSpinner()
                    }
                })
                reboundAnimator!!.start()
            }
            if (delayed > 0) {
                reboundAnimator = ValueAnimator()
                postDelayed(runnable, delayed.toLong())
            } else {
                runnable.run()
            }
            return true
        } else {
            return false
        }
    }

    /**
     * 自动加载
     */
    override fun autoLoadMore(): Boolean {
        return autoLoadMore(0)
    }

    /**
     * 自动加载
     */
    override fun autoLoadMore(delayed: Int): Boolean {
        return autoLoadMore(delayed, mReboundDuration, 1f * (mFooterHeight + mFooterExtendHeight / 2) / mFooterHeight)
    }

    /**
     * 自动加载
     */
    override fun autoLoadMore(delayed: Int, duration: Int, dragrate: Float): Boolean {
        if (mState === RefreshState.None && isEnableLoadMore() && !mLoadMoreFinished) {
            if (reboundAnimator != null) {
                reboundAnimator!!.cancel()
            }
            val runnable = Runnable {
                reboundAnimator = ValueAnimator.ofInt(mSpinner, -(mFooterHeight * dragrate).toInt())
                reboundAnimator!!.duration = duration.toLong()
                reboundAnimator!!.interpolator = DecelerateInterpolator()
                reboundAnimator!!.addUpdateListener(ValueAnimator.AnimatorUpdateListener { animation -> moveSpinner(animation.animatedValue as Int, false) })
                reboundAnimator!!.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationStart(animation: Animator) {
                        mLastTouchX = (measuredWidth / 2).toFloat()
                        setStatePullUpToLoad()
                    }

                    override fun onAnimationEnd(animation: Animator) {
                        reboundAnimator = null
                        if (mState !== RefreshState.ReleaseToLoad) {
                            setStateReleaseToLoad()
                        }
                        if (mEnableAutoLoadmore) {
                            mEnableAutoLoadmore = false
                            overSpinner()
                            mEnableAutoLoadmore = true
                        } else {
                            overSpinner()
                        }
                    }
                })
                reboundAnimator!!.start()
            }
            if (delayed > 0) {
                reboundAnimator = ValueAnimator()
                postDelayed(runnable, delayed.toLong())
            } else {
                runnable.run()
            }
            return true
        } else {
            return false
        }
    }

    override fun isEnableLoadMore(): Boolean {
        return mEnableLoadmore && !mEnablePureScrollMode
    }

    override fun isLoadMoreFinished(): Boolean {
        return mLoadMoreFinished
    }

    override fun isEnableAutoLoadMore(): Boolean {
        return mEnableAutoLoadmore
    }

    override fun isEnableRefresh(): Boolean {
        return mEnableRefresh && !mEnablePureScrollMode
    }

    override fun isEnableOverScrollBounce(): Boolean {
        return mEnableOverScrollBounce
    }

    override fun isEnablePureScrollMode(): Boolean {
        return mEnablePureScrollMode
    }

    override fun isEnableScrollContentWhenLoaded(): Boolean {
        return mEnableScrollContentWhenLoaded
    }

    /**
     * 设置默认Header构建器
     */
    fun setDefaultRefreshHeaderCreater(creater: DefaultRefreshHeaderCreater) {
        sHeaderCreater = creater
    }

    /**
     * 设置默认Footer构建器
     */
    fun setDefaultRefreshFooterCreater(creater: DefaultRefreshFooterCreater) {
        sFooterCreater = creater
        sManualFooterCreater = true
    }

    //</editor-fold>
    //<editor-fold desc="核心接口 RefreshKernel">
    class RefreshKernelImpl(val layout: RefreshLayout) : IRefreshKernel {

        override fun getRefreshLayout(): RefreshLayout {
            return layout
        }

        override fun getRefreshContent(): IRefreshContent {
            return layout.mRefreshContent!!
        }

        override fun setState(state: RefreshState): IRefreshKernel {
            when (state) {
                RefreshState.None -> layout.resetStatus()
                RefreshState.PullDownToRefresh -> layout.setStatePullDownToRefresh()
                RefreshState.PullToUpLoad -> layout.setStatePullUpToLoad()
                RefreshState.PullDownCanceled -> layout.setStatePullDownCanceled()
                RefreshState.PullUpCanceled -> layout.setStatePullUpCanceled()
                RefreshState.ReleaseToRefresh -> layout.setStateReleaseToRefresh()
                RefreshState.ReleaseToLoad -> layout.setStateReleaseToLoad()
                RefreshState.ReleaseToTwoLevel -> {
                    if (!layout.mState.opening && layout.isEnableRefresh()) {
                        layout.notifyStateChanged(RefreshState.ReleaseToTwoLevel)
                    } else {
                        layout.setViceState(RefreshState.ReleaseToTwoLevel)
                    }
                }
                RefreshState.RefreshReleased -> {
                    if (!layout.mState.opening && layout.isEnableRefresh()) {
                        layout.notifyStateChanged(RefreshState.RefreshReleased)
                    } else {
                        layout.setViceState(RefreshState.RefreshReleased)
                    }
                }
                RefreshState.LoadReleased -> {
                    if (!layout.mState.opening && layout.isEnableLoadMore()) {
                        layout.notifyStateChanged(RefreshState.LoadReleased)
                    } else {
                        layout.setViceState(RefreshState.LoadReleased)
                    }
                }
                RefreshState.Refreshing -> layout.setStateRefreshing()
                RefreshState.Loading -> layout.setStateLoding()
                RefreshState.RefreshFinish -> {
                    if (layout.mState === RefreshState.Refreshing) {
                        layout.notifyStateChanged(RefreshState.RefreshFinish)
                    }
                }
                RefreshState.LoadFinish -> {
                    if (layout.mState === RefreshState.Loading) {
                        layout.notifyStateChanged(RefreshState.LoadFinish)
                    }
                }
                else -> {
                }
            }
            return null!!
        }

        override fun startTwoLevel(open: Boolean) {
            if (open) {
                val listener = object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        layout.notifyStateChanged(RefreshState.TwoLevel)
                    }
                }
                val animator = layout.animSpinner(layout.measuredHeight)
                if (animator != null && animator == layout.reboundAnimator) {
                    animator.duration = layout.mFloorDuration.toLong()
                    animator.addListener(listener)
                } else {
                    listener.onAnimationEnd(null!!)
                }
            } else {
                if (animSpinner(0) == null) {
                    layout.notifyStateChanged(RefreshState.None)
                }
            }
        }

        override fun finishTwoLevel() {
            if (layout.mState == RefreshState.TwoLevel) {
                layout.notifyStateChanged(RefreshState.TwoLevelFinish)
                if (layout.mSpinner == 0) {
                    moveSpinner(0, true)
                    layout.notifyStateChanged(RefreshState.None)
                } else {
                    layout.animSpinner(0)!!.duration = layout.mFloorDuration.toLong()
                }
            }
        }

        //<editor-fold desc="状态更改 state changes">

        //</editor-fold>

        //<editor-fold desc="视图位移 Spinner">

        override fun moveSpinner(spinner: Int, isAnimator: Boolean): IRefreshKernel {
            layout.moveSpinner(spinner, isAnimator)
            return this
        }

        override fun animSpinner(endSpinner: Int): IRefreshKernel {
            layout.animSpinner(endSpinner)
            return this
        }

        //</editor-fold>

        //<editor-fold desc="请求事件">
        override fun requestDrawBackgoundForHeader(backgroundColor: Int): IRefreshKernel {
            if (layout.mPaint == null && backgroundColor != 0) {
                layout.mPaint = Paint()
            }
            layout.mHeaderBackgroundColor = backgroundColor
            return this
        }

        override fun requestDrawBackgoundForFooter(backgroundColor: Int): IRefreshKernel {
            if (layout.mPaint == null && backgroundColor != 0) {
                layout.mPaint = Paint()
            }
            layout.mFooterBackgroundColor = backgroundColor
            return this
        }

        override fun requestHeaderNeedTouchEventWhenRefreshing(request: Boolean): IRefreshKernel {
            layout.mHeaderNeedTouchEventWhenRefreshing = request
            return this
        }

        override fun requestFooterNeedTouchEventWhenLoading(request: Boolean): IRefreshKernel {
            layout.mFooterNeedTouchEventWhenLoading = request
            return this
        }

        override fun requestDefaultHeaderTranslationContent(translation: Boolean): IRefreshKernel {
            if (!layout.mManualHeaderTranslationContent) {
                layout.mManualHeaderTranslationContent = true
                layout.mEnableHeaderTranslationContent = translation
            }
            return this
        }

        override fun requestRemeasureHeightForHeader(): IRefreshKernel {
            if (layout.mHeaderHeightStatus.notifyed) {
                layout.mHeaderHeightStatus = layout.mHeaderHeightStatus.unNotify()
            }
            return this
        }

        override fun requestRemeasureHeightForFooter(): IRefreshKernel {
            if (layout.mFooterHeightStatus.notifyed) {
                layout.mFooterHeightStatus = layout.mFooterHeightStatus.unNotify()
            }
            return this
        }

        override fun requestFloorDuration(duration: Int): IRefreshKernel {
            layout.mFloorDuration = duration
            return this
        }
        //</editor-fold>
    }
    //</editor-fold>


    //<editor-fold desc="内存泄漏 postDelayed优化">

    override fun post(action: Runnable): Boolean {
        if (mHandler == null) {
            mDelayedRunables = if (mDelayedRunables == null) ArrayList() else mDelayedRunables
            mDelayedRunables!!.add(DelayedRunable(action))
            return false
        }
        return mHandler!!.post(DelayedRunable(action))
    }

    override fun postDelayed(action: Runnable, delayMillis: Long): Boolean {
        if (delayMillis == 0L) {
            DelayedRunable(action).run()
            return true
        }
        if (mHandler == null) {
            mDelayedRunables = if (mDelayedRunables == null) ArrayList() else mDelayedRunables
            mDelayedRunables!!.add(DelayedRunable(action, delayMillis))
            return false
        }
        return mHandler!!.postDelayed(DelayedRunable(action), delayMillis)
    }

    //</editor-fold>
}