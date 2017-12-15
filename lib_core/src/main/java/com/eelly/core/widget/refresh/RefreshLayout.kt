package com.eelly.core.widget.refresh

import android.content.Context
import android.graphics.Paint
import android.os.Handler
import android.support.v4.view.*
import android.util.AttributeSet
import android.view.*
import android.view.View.MeasureSpec.AT_MOST
import android.view.View.MeasureSpec.EXACTLY
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
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
import com.eelly.core.widget.refresh.tools.ViscousFluidInterpolator

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
            if (accentColor != 0) {
                mPrimaryColors = intArrayOf(primaryColor, accentColor)
            } else {
                mPrimaryColors = intArrayOf(primaryColor)
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
            mDelayedRunables.clear()
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
            if (mRefreshContent != null && mRefreshContent.getView() === child) {
                val lp = mRefreshContent!!.getLayoutParams() as LayoutParams
                val widthSpec = ViewGroup.getChildMeasureSpec(widthMeasureSpec,
                        paddingLeft + paddingRight +
                        lp.leftMargin + lp.rightMargin, lp.width)
                val heightSpec = ViewGroup.getChildMeasureSpec(heightMeasureSpec,
                        paddingTop + paddingBottom +
                        lp.topMargin + lp.bottomMargin +
                        (if (isInEditMode && isEnableRefresh() && (mEnableHeaderTranslationContent || mRefreshHeader.getSpinnerStyle() === SpinnerStyle.FixedBehind)) mHeaderHeight else 0) +
                                if (isInEditMode && isEnableLoadMore() && (mEnableFooterTranslationContent || mRefreshFooter.getSpinnerStyle() === SpinnerStyle.FixedBehind)) mFooterHeight else 0, lp.height)
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
        val paddingBottom = paddingBottom


        var i = 0
        val len = childCount
        while (i < len) {
            val child = getChildAt(i)

            if (mRefreshContent != null && mRefreshContent!!.getView() === child) {
                val isInEditMode = isInEditMode && mEnablePreviewInEditMode
                val lp = mRefreshContent!!.getLayoutParams()
                val left = paddingLeft + lp.leftMargin
                var top = paddingTop + lp.topMargin
                val right = left + mRefreshContent!!.getMeasuredWidth()
                var bottom = top + mRefreshContent!!.getMeasuredHeight()
                if (isInEditMode && isEnableRefresh() && (mEnableHeaderTranslationContent ||
                        mRefreshHeader.getSpinnerStyle() === SpinnerStyle.FixedBehind)) {
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

}