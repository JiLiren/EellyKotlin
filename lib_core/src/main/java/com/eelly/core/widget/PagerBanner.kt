package com.eelly.core.widget

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.ViewConfiguration
import android.widget.LinearLayout
import android.widget.Scroller
import com.eelly.core.R
import com.eelly.core.tools.HeaderScrollHelper

/**
 * @author Vurtne on 6-Dec-17.
 */
class PagerBanner : LinearLayout{

    private val DIRECTION_UP = 1
    private val DIRECTION_DOWN = 2

    /**
     * 滚动的最大偏移量
     * */
    private var mTopOffset = 0

    private val mScroller: Scroller?

    /**
     * 表示滑动的时候，手的移动要大于这个距离才开始移动控件。
     * */
    private val mTouchSlop: Int
    /**
     * 允许执行一个fling手势动作的最小速度值
     * */
    private val mMinimumVelocity: Int
    /**
     * 允许执行一个fling手势动作的最大速度值
     * */
    private val mMaximumVelocity: Int
    /**
     * 当前sdk版本，用于判断api版本
     * */
    private val mSysVersion: Int
    /**
     * 滚动的监听
     * */
    private var onScrollListener: OnScrollListener? = null
    /**
     * 需要被滑出的头部
     * */
    private var mHeadView: View? = null
    /**
     * 滑出头部的高度
     * */
    private var mHeadHeight: Int = 0
    /**
     * 最大滑出的距离，等于 mHeadHeight
     * */
    private var maxY = 0
    /**
     * 最小的距离， 头部在最顶部
     * */
    private val minY = 0
    /**
     * 当前已经滚动的距离
     * */
    private var mCurY: Int = 0

    private var mDirection: Int = 0
    private var mLastScrollerY: Int = 0
    /**
     * 是否允许拦截事件
     * */
    private var mDisallowIntercept: Boolean = false
    /**
     * 当前点击区域是否在头部
     * */
    private var isClickHead: Boolean = false
    /**
     * 第一次按下的x坐标
     * */
    private var mDownX: Float = 0.toFloat()
    /**
     * 第一次按下的y坐标
     * */
    private var mDownY: Float = 0.toFloat()
    /**
     * 最后一次移动的Y坐标
     * */
    private var mLastY: Float = 0.toFloat()
    /**
     * 是否允许垂直滚动
     * */
    private var verticalScrollFlag = false
    /**
     * 是否需要滑动一下
     * */
    private var mIsScroll = true

    private var isHaveData = false

    private var mVelocityTracker: VelocityTracker? = null


    private val mScrollable: HeaderScrollHelper

    constructor(context: Context):this(context,null)

    constructor(context: Context, attrs: AttributeSet?):this(context,attrs,0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int):super(context,attrs,defStyleAttr){
        val array = context.obtainStyledAttributes(attrs, R.styleable.pagerBanner)
        mTopOffset = array.getDimensionPixelSize(array.getIndex(R.styleable.pagerBanner_TopOffset), mTopOffset)
        array.recycle()

        mScroller = Scroller(context)
        mScrollable = HeaderScrollHelper()
        val configuration = ViewConfiguration.get(context)
        mTouchSlop = configuration.scaledTouchSlop
        mMinimumVelocity = configuration.scaledMinimumFlingVelocity
        mMaximumVelocity = configuration.scaledMaximumFlingVelocity
        mSysVersion = Build.VERSION.SDK_INT
    }

    fun setOnScrollListener(onScrollListener: OnScrollListener) {
        this.onScrollListener = onScrollListener
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        if (mHeadView != null && !mHeadView!!.isClickable) {
            mHeadView!!.isClickable = true
        }
    }

    /**
     * 设置viewpager没有设置adapter是否可以滑动外布局
     * @param isHaveData isHaveData
     */
    fun setIsHaveData(isHaveData: Boolean) {
        this.isHaveData = isHaveData
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        mHeadView = getChildAt(0)
        measureChildWithMargins(mHeadView, widthMeasureSpec, 0, View.MeasureSpec.UNSPECIFIED, 0)
        mHeadHeight = (mHeadView as View?)!!.measuredHeight
        maxY = mHeadHeight - mTopOffset
        //让测量高度加上头部的高度
        super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(
                View.MeasureSpec.getSize(heightMeasureSpec) + maxY, View.MeasureSpec.EXACTLY))
    }

    /**
     * @param disallowIntercept 作用同 requestDisallowInterceptTouchEvent
     */
    fun requestHeaderViewPagerDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
        super.requestDisallowInterceptTouchEvent(disallowIntercept)
        mDisallowIntercept = disallowIntercept
    }

    /**
     * 说明：一旦dispatTouchEvent返回true，即表示当前View就是事件传递需要的 targetView，事件不会再传递给
     * 其他View，如果需要将事件继续传递给子View，可以手动传递
     * 由于dispatchTouchEvent处理事件的优先级高于子View，也高于onTouchEvent,所以在这里进行处理
     * 好处一：当有子View，并且子View可以获取焦点的时候，子View的onTouchEvent会优先处理，如果当前逻辑
     * 在onTouchEnent中，则事件无法到达，逻辑失效
     * 好处二：当子View是拥有滑动事件时，例如ListView，ScrollView等，不需要对子View的事件进行拦截，可以
     * 全部让该父控件处理，在需要的地方手动将事件传递给子View，保证滑动的流畅性，结尾两行代码就是证明：
     * super.dispatchTouchEvent(ev);
     * return true;
     */
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val currentX = ev.x
        //当前手指相对于当前view的X坐标
        val currentY = ev.y
        //当前手指相对于当前view的Y坐标
        val shiftX = Math.abs(currentX - mDownX)
        //当前触摸位置与第一次按下位置的X偏移量
        val shiftY = Math.abs(currentY - mDownY)
        //当前触摸位置与第一次按下位置的Y偏移量
        val deltaY: Float
        //滑动的偏移量，即连续两次进入Move的偏移量
        obtainVelocityTracker(ev)
        //初始化速度追踪器
        when (ev.action) {
        //Down事件主要初始化变量
            MotionEvent.ACTION_DOWN -> {
                mDisallowIntercept = false
                verticalScrollFlag = false
                mDownX = currentX
                mDownY = currentY
                mLastY = currentY
                checkIsClickHead(currentY.toInt(), mHeadHeight, scrollY)
                mScroller!!.abortAnimation()
            }
            MotionEvent.ACTION_MOVE -> {
                if (!mDisallowIntercept) {
                    deltaY = mLastY - currentY
                    //连续两次进入move的偏移量
                    mLastY = currentY
                    if (shiftX > mTouchSlop && shiftX > shiftY) {
                        //水平滑动
                        verticalScrollFlag = false
                    } else if (shiftY > mTouchSlop && shiftY > shiftX) {
                        //垂直滑动
                        verticalScrollFlag = true
                    }
                    /**
                     * 这里要注意，对于垂直滑动来说，给出以下三个条件
                     * 头部没有固定，允许滑动的View处于第一条可见，当前按下的点在头部区域
                     * 三个条件满足一个即表示需要滚动当前布局，否者不处理，将事件交给子View去处理
                     */
                    if (verticalScrollFlag && (!isStickied() || mScrollable != null && mScrollable.isTop() || isClickHead)) {
                        //如果是向下滑，则deltaY小于0，对于scrollBy来说
                        //正值为向上和向左滑，负值为向下和向右滑，这里要注意
                        scrollBy(0, (deltaY + 0.5).toInt())
                        invalidate()
                    }
                }

            }
            MotionEvent.ACTION_UP -> {
                if (verticalScrollFlag) {
                    mVelocityTracker!!.computeCurrentVelocity(1000, mMaximumVelocity.toFloat())
                    //1000表示单位，每1000毫秒允许滑过的最大距离是mMaximumVelocity
                    val yVelocity = mVelocityTracker!!.yVelocity
                    //获取当前的滑动速度
                    mDirection = if (yVelocity > 0) DIRECTION_DOWN else DIRECTION_UP
                    //下滑速度大于0，上滑速度小于0
                    //根据当前的速度和初始化参数，将滑动的惯性初始化到当前View，至于是否滑动当前View，取决于computeScroll中计算的值
                    //这里不判断最小速度，确保computeScroll一定至少执行一次
                    mScroller!!.fling(0, scrollY, 0, -yVelocity.toInt(), 0, 0, -Integer.MAX_VALUE, Integer.MAX_VALUE)
                    mLastScrollerY = scrollY
                    invalidate()
                    //更新界面，该行代码会导致computeScroll中的代码执行
                    //阻止快读滑动的时候点击事件的发生，滑动的时候，将Up事件改为Cancel就不会发生点击了
                    if (shiftX > mTouchSlop || shiftY > mTouchSlop) {
                        if (isClickHead || !isStickied()) {
                            val action = ev.action
                            ev.action = MotionEvent.ACTION_CANCEL
                            val dd = super.dispatchTouchEvent(ev)
                            ev.action = action
                            return dd
                        }
                    }
                }
                recycleVelocityTracker()
            }
            MotionEvent.ACTION_CANCEL -> recycleVelocityTracker()
            else -> {
            }
        }
        //手动将事件传递给子View，让子View自己去处理事件
        try {
            super.dispatchTouchEvent(ev)
        } catch (e: Exception) {

        }
        //消费事件，返回True表示当前View需要消费事件，就是事件的TargetView
        return true
    }

    private fun checkIsClickHead(downY: Int, headHeight: Int, scrollY: Int) {
        isClickHead = downY + scrollY <= headHeight
    }

    private fun obtainVelocityTracker(event: MotionEvent) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain()
        }
        mVelocityTracker!!.addMovement(event)
    }

    private fun recycleVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker!!.recycle()
            mVelocityTracker = null
        }
    }

    override fun computeScroll() {
        if (mScroller!!.computeScrollOffset()) {
            val currY = mScroller.currY
            if (mDirection == DIRECTION_UP) {
                // 手势向上划
                if (isStickied()) {
                    //这里主要是将快速滚动时的速度对接起来，让布局看起来滚动连贯
                    val distance = mScroller.getFinalY() - currY    //除去布局滚动消耗的时间后，剩余的时间
                    val duration = calcDuration(mScroller.getDuration(), mScroller.timePassed()) //除去布局滚动的距离后，剩余的距离
                    mScrollable.smoothScrollBy(getScrollerVelocity(distance, duration), distance, duration)
                    //外层布局已经滚动到指定位置，不需要继续滚动了
                    mScroller.abortAnimation()
                    return
                } else {
                    scrollTo(0, currY)  //将外层布局滚动到指定位置
                    invalidate()        //移动完后刷新界面
                }
            } else {
                // 手势向下划，内部View已经滚动到顶了，需要滚动外层的View
                try {
                    if (isHaveData || mScrollable.isTop() || isClickHead || mScrollable.isNoData()) {
                        val deltaY = currY - mLastScrollerY
                        val toY = scrollY + deltaY
                        scrollTo(0, toY)
                        if (mCurY <= minY) {
                            mScroller.abortAnimation()
                            return
                        }
                    }
                } catch (exception: Exception) {

                }

                //向下滑动时，初始状态可能不在顶部，所以要一直重绘，让computeScroll一直调用
                //确保代码能进入上面的if判断
                invalidate()
            }
            mLastScrollerY = currY
        }
    }

    @SuppressLint("NewApi")
    private fun getScrollerVelocity(distance: Int, duration: Int): Int {
        if (mScroller == null) {
            return 0
        } else if (mSysVersion >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            return mScroller.currVelocity.toInt()
        } else {
            return distance / duration
        }
    }

    /**
     * 对滑动范围做限制
     */
    override fun scrollBy(x: Int, y: Int) {
        var z = y
        val scrollY = scrollY
        var toY = scrollY + z
        if (toY >= maxY) {
            toY = maxY
        } else if (toY <= minY) {
            toY = minY
        }
        z = toY - scrollY
        super.scrollBy(x, z)
    }

    /**
     * 对滑动范围做限制
     */
    override fun scrollTo(x: Int, y: Int) {
        var z = y
        if (z >= maxY) {
            z = maxY
        } else if (y <= minY) {
            z = minY
        }
        mCurY = z
        if (onScrollListener != null) {
            onScrollListener!!.onScroll(z, maxY)
        }
        super.scrollTo(x, z)
    }

    /**
     * 头部是否已经固定
     */
    fun isStickied(): Boolean {
        return mCurY == maxY
    }

    private fun calcDuration(duration: Int, timepass: Int): Int {
        return duration - timepass
    }

    fun getMaxY(): Int {
        return maxY
    }

    fun isHeadTop(): Boolean {
        return mCurY == minY
    }

    /**
     * 是否允许下拉，与PTR结合使用
     */
    fun canPtr(): Boolean {
        return verticalScrollFlag && mCurY == minY && mScrollable.isTop()
    }

    fun setTopOffset(topOffset: Int) {
        this.mTopOffset = topOffset
    }

    fun setCurrentScrollableContainer(scrollableContainer: HeaderScrollHelper.ScrollableContainer) {
        mScrollable.setCurrentScrollableContainer(scrollableContainer)
    }

    fun setIsScroll(isScroll: Boolean) {
        this.mIsScroll = isScroll
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (mIsScroll) {
            scrollTo(0, 100)
            scrollTo(0, 0)
        }

    }

    interface OnScrollListener {
        fun onScroll(currentY: Int, maxY: Int)
    }

}