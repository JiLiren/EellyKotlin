package com.eelly.core.widget.banner

import android.content.Context
import android.graphics.Color
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.text.TextUtils
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Interpolator
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Scroller
import android.widget.TextView
import com.eelly.core.R
import java.util.ArrayList

/**
 * @author vurtne on 3-Jan-18.
 */
class BannerView<Item> : FrameLayout {

    private val TAG = BannerView::class.java.simpleName

    companion object {
        val VISIBLE_AUTO = 0
        val VISIBLE_ALWAYS = 1
        val VISIBLE_NEVER = 2
    }

    /**
     * 设备密度
     * */
    private lateinit var mDm: DisplayMetrics
    /**
     * 多久后开始滚动
     * */
    private var mDelay: Long = 0
    /**
     * 滚动间隔
     * */
    private var mInterval: Long = 0
    /**
     * 是否自动滚动
     * */
    private var mIsAuto: Boolean = false
    /**
     * 最后一条 item 是否显示背景条
     * */
    private var mBarVisibleWhenLast: Boolean = false
    private var mCurrentPosition: Int = 0

    private var mIsStarted = false
    private var mIsVisible = false
    private var mIsResumed = true
    private var mIsRunning = false

    var DEBUG = false

    /**
     * 内容宽高
     * */
    private var mItemWidth: Int = 0
    private var mItemHeight = FrameLayout.LayoutParams.WRAP_CONTENT

    private lateinit var vViewPager: ViewPager
    private lateinit var vBottomBar: LinearLayout
    private lateinit var vTitleBar: TextView
    private lateinit var vIndicator: ViewPagerIndicator

    private var mIndicatorVisible: Int = 0

    private var mDataList: List<Item> = ArrayList()
    private var mOnPageChangeListener: ViewPager.OnPageChangeListener? = null
    private var mViewFactory: ViewFactory<Item>? = null


    constructor(context: Context):this(context, null)

    constructor(context: Context, attrs: AttributeSet?):this(context, attrs, 0)

    @SuppressWarnings("all")
    constructor(context: Context, attrs: AttributeSet?,defStyle : Int):super(context, attrs,defStyle){
        initView(attrs)
    }

    @SuppressWarnings("all")
    private fun initView(attrs: AttributeSet?){
        mDm = context.resources.displayMetrics
        val ta = context.obtainStyledAttributes(attrs, R.styleable.BannerView)
        var aspectRatio = ta.getFloat(R.styleable.BannerView_bvAspectRatio, 0f)
        val isLoop = ta.getBoolean(R.styleable.BannerView_bvIsLoop, true)
        mDelay = ta.getInt(R.styleable.BannerView_bvDelay, 5000).toLong()
        mInterval = ta.getInt(R.styleable.BannerView_bvInterval, 5000).toLong()
        mIsAuto = ta.getBoolean(R.styleable.BannerView_bvIsAuto, true)

        mBarVisibleWhenLast = ta.getBoolean(R.styleable.BannerView_bvBarVisibleWhenLast, true)

        val indicatorGravity = ta.getInt(R.styleable.BannerView_bvIndicatorGravity, Gravity.CENTER)
        val barColor = ta.getColor(R.styleable.BannerView_bvBarColor, Color.TRANSPARENT)
        val barPaddingLeft = ta.getDimension(R.styleable.BannerView_bvBarPaddingLeft, dp2px(10f).toFloat())
        val barPaddingTop = ta.getDimension(R.styleable.BannerView_bvBarPaddingTop, dp2px(10f).toFloat())
        val barPaddingRight = ta.getDimension(R.styleable.BannerView_bvBarPaddingRight, dp2px(10f).toFloat())
        val barPaddingBottom = ta.getDimension(R.styleable.BannerView_bvBarPaddingBottom, dp2px(10f).toFloat())

        val titleColor = ta.getColor(R.styleable.BannerView_bvTitleColor, Color.WHITE)
        val titleSize = ta.getDimension(R.styleable.BannerView_bvTitleSize, sp2px(14f))
        val titleVisible = ta.getBoolean(R.styleable.BannerView_bvTitleVisible, false)

        // auto, aways, never
        mIndicatorVisible = ta.getInteger(R.styleable.BannerView_bvIndicatorVisible, VISIBLE_AUTO)

        val indicatorWidth = ta.getDimensionPixelSize(R.styleable.BannerView_bvIndicatorWidth, dp2px(6f))
        val indicatorHeight = ta.getDimensionPixelSize(R.styleable.BannerView_bvIndicatorHeight, dp2px(6f))
        val indicatorGap = ta.getDimensionPixelSize(R.styleable.BannerView_bvIndicatorGap, dp2px(6f))
        val indicatorColor = ta.getColor(R.styleable.BannerView_bvIndicatorColor, -0x77000001)
        val indicatorColorSelected = ta.getColor(R.styleable.BannerView_bvIndicatorColorSelected, Color.WHITE)

        val indicatorDrawable = ta.getDrawable(R.styleable.BannerView_bvIndicatorDrawable)
        val indicatorDrawableSelected = ta.getDrawable(R.styleable.BannerView_bvIndicatorDrawableSelected)
        ta.recycle()


        //create ViewPager
        vViewPager = if (isLoop) LoopViewPager(context) else ViewPager(context)
        vViewPager.offscreenPageLimit = 1

        val systemAttrs = intArrayOf(android.R.attr.layout_width, android.R.attr.layout_height)
        val a = context.obtainStyledAttributes(attrs, systemAttrs)
        mItemWidth = a.getLayoutDimension(0, mDm.widthPixels)
        mItemHeight = a.getLayoutDimension(1, mItemHeight)
        a.recycle()

        if (mItemWidth < 0) {
            mItemWidth = mDm.widthPixels
        }

        if (aspectRatio > 0) {
            if (aspectRatio > 1) {
                aspectRatio = 1f
            }
            mItemHeight = (mItemWidth * aspectRatio).toInt()
        }

        Log.e(TAG, "w = $mItemWidth, h = $mItemHeight")
        val lp = FrameLayout.LayoutParams(mItemWidth, mItemHeight)
        addView(vViewPager, lp)

        // bottom bar
        vBottomBar = LinearLayout(context)
        vBottomBar.setBackgroundColor(barColor)
        vBottomBar.setPadding(barPaddingLeft.toInt(), barPaddingTop.toInt(), barPaddingRight.toInt(),
                barPaddingBottom.toInt())
        vBottomBar.clipChildren = false
        vBottomBar.clipToPadding = false
        vBottomBar.orientation = LinearLayout.HORIZONTAL
        vBottomBar.gravity = Gravity.CENTER
        addView(vBottomBar, FrameLayout.LayoutParams(mItemWidth, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM))

        vIndicator = ViewPagerIndicator(context)
        vIndicator.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT)
        vIndicator.setItemSize(indicatorWidth, indicatorHeight)
        vIndicator.setItemGap(indicatorGap)
        if (indicatorDrawable != null && indicatorDrawableSelected != null) {
            vIndicator.setItemDrawable(indicatorDrawable, indicatorDrawableSelected)
        } else {
            vIndicator.setItemColor(indicatorColor, indicatorColorSelected)
        }

        // title
        vTitleBar = TextView(context)
        vTitleBar.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f)
        vTitleBar.setSingleLine(true)
        vTitleBar.setTextColor(titleColor)
        vTitleBar.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleSize)
        vTitleBar.ellipsize = TextUtils.TruncateAt.END
        vTitleBar.visibility = if (titleVisible) View.VISIBLE else View.INVISIBLE

        when (indicatorGravity) {
            Gravity.CENTER -> vBottomBar.addView(vIndicator)
            Gravity.RIGHT -> {
                vBottomBar.addView(vTitleBar)
                vBottomBar.addView(vIndicator)

                vTitleBar.setPadding(0, 0, dp2px(10f), 0)
                vTitleBar.gravity = Gravity.LEFT
            }
            Gravity.LEFT -> {
                vBottomBar.addView(vIndicator)
                vBottomBar.addView(vTitleBar)

                vTitleBar.setPadding(dp2px(10f), 0, 0, 0)
                vTitleBar.gravity = Gravity.RIGHT
            }
        }
    }

    fun setDelay(delay: Long) {
        this.mDelay = delay
    }

    fun setInterval(interval: Long) {
        this.mInterval = interval
    }

    fun setIsAuto(isAuto: Boolean) {
        this.mIsAuto = isAuto
    }

    fun setIndicatorVisible(value: Int) {
        mIndicatorVisible = value
    }

    fun setBarVisibleWhenLast(value: Boolean) {
        this.mBarVisibleWhenLast = value
    }

    fun setBarColor(barColor: Int) {
        vBottomBar.setBackgroundColor(barColor)
    }

    fun setBarPadding(left: Float, top: Float, right: Float, bottom: Float) {
        vBottomBar.setPadding(dp2px(left), dp2px(top), dp2px(right), dp2px(bottom))
    }

    fun setTitleColor(textColor: Int) {
        vTitleBar.setTextColor(textColor)
    }

    fun setTitleSize(sp: Float) {
        vTitleBar.setTextSize(TypedValue.COMPLEX_UNIT_SP, sp)
    }

    fun setTitleVisible(isTitleVisible: Boolean) {
        vTitleBar.visibility = if (isTitleVisible) View.VISIBLE else View.INVISIBLE
    }

    fun isLoop(): Boolean {
        return vViewPager is LoopViewPager
    }

    fun getViewPager(): ViewPager {
        return vViewPager
    }

    fun getIndicator(): ViewPagerIndicator {
        return vIndicator
    }

    fun setViewFactory(factory: ViewFactory<Item>) {
        mViewFactory = factory
    }

    fun setTitleAdapter(adapter: TitleAdapter<Item>) {
        mTitleAdapter = adapter
    }

    fun setDataList(list: List<Item>) {
        mDataList = list
    }

    fun setOnPageChangeListener(listener: ViewPager.OnPageChangeListener) {
        mOnPageChangeListener = listener
    }

    fun initViewPager() {
        vViewPager.adapter = mInternalPagerAdapter
        vViewPager.removeOnPageChangeListener(mInternalPageListener)
        vViewPager.addOnPageChangeListener(mInternalPageListener)
        vViewPager.offscreenPageLimit = mDataList.size
        mInternalPagerAdapter.notifyDataSetChanged()
        try {
            if (isLoop()) {
                setDuration(vViewPager, 500)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    internal fun initIndicator() {
        vIndicator.setupWithViewPager(vViewPager)
        val visible = mIndicatorVisible == VISIBLE_ALWAYS || mIndicatorVisible == VISIBLE_AUTO && mDataList.size > 1
        vIndicator.visibility = if (visible) View.VISIBLE else View.INVISIBLE
        vIndicator.setPosition(mCurrentPosition)
    }

    internal fun setCurrentTitle(position: Int) {
        vTitleBar.text = mTitleAdapter.getTitle(mDataList[position])
    }

    private fun isValid(): Boolean {
        if (mViewFactory == null) {
            Log.e(TAG, "ViewFactory must be not null!")
            return false
        }
        return true
    }

    fun start() {
        if (!isValid()) {
            return
        }
        if (mCurrentPosition > mDataList.size - 1) {
            mCurrentPosition = 0
        }
        initViewPager()
        initIndicator()
        setCurrentTitle(mCurrentPosition)
        mIsStarted = true
        update()
    }

    private fun update() {
        if (!isValid()) {
            return
        }
        val running = mIsVisible && mIsResumed && mIsStarted && mIsAuto && mDataList.size > 1 && (isLoop() || mCurrentPosition + 1 < mDataList.size)
        if (running != mIsRunning) {
            if (running) {
                postDelayed(mRunnable, mDelay)
            } else {
                removeCallbacks(mRunnable)
            }
            mIsRunning = running
        }
        if (DEBUG) {
            Log.e("ezy", "update:running=$mIsRunning,visible=$mIsVisible,started=$mIsStarted,resumed=$mIsResumed")
            Log.e("ezy", "update:auto=" + mIsAuto + ",loop=" + isLoop() + ",size=" + mDataList.size + ",current=" + mCurrentPosition)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mIsVisible = false
        update()
    }

    override fun onWindowVisibilityChanged(visibility: Int) {
        super.onWindowVisibilityChanged(visibility)
        mIsVisible = visibility == View.VISIBLE
        update()
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val action = ev.action
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                mIsResumed = false
                update()
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                mIsResumed = true
                update()
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun dp2px(dp: Float): Int {
        return (dp * mDm.density + 0.5f).toInt()
    }

    private fun sp2px(sp: Float): Float {
        return sp * mDm.scaledDensity
    }


    private val mInternalPageListener = object : ViewPager.OnPageChangeListener {
        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            mOnPageChangeListener?.onPageScrolled(position, positionOffset, positionOffsetPixels)
        }

        override fun onPageSelected(position: Int) {
            if (DEBUG) {
                Log.e("ezy", "onPageSelected, pos=" + mCurrentPosition)
            }
            mCurrentPosition = position % mDataList.size
            setCurrentTitle(mCurrentPosition)
            vBottomBar.visibility = if (mCurrentPosition == mDataList.size - 1 && !mBarVisibleWhenLast) View.GONE else View.VISIBLE

            mOnPageChangeListener?.onPageSelected(position)
        }

        override fun onPageScrollStateChanged(state: Int) {
            mOnPageChangeListener?.onPageScrollStateChanged(state)
        }
    }


    private val mInternalPagerAdapter = object : PagerAdapter() {
        override fun getCount(): Int {
            return mDataList.size
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val inflate = mViewFactory!!.create(mDataList[position], position, container)
            container.addView(inflate)
            return inflate
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }

        override fun getItemPosition(`object`: Any): Int {
            return PagerAdapter.POSITION_NONE
        }
    }

    public interface ViewFactory<in Item> {
        fun create(item: Item, position: Int, container: ViewGroup): View
    }

    public interface TitleAdapter<in Item> {
        fun getTitle(item: Item): CharSequence
    }



    private var mTitleAdapter : TitleAdapter<Item> = object :TitleAdapter<Item>{
        override fun getTitle(item: Item): CharSequence {
            return item.toString()
        }
    }

    private fun setDuration(pager: ViewPager, duration: Int) {
        try {
            val scroller = FixedSpeedScroller(pager.context, AccelerateDecelerateInterpolator(), duration)
            val field = ViewPager::class.java.getDeclaredField("mScroller")
            field.isAccessible = true
            field.set(pager, scroller)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private class FixedSpeedScroller : Scroller {
        private var mDuration = 450

        constructor(context: Context) : super(context)

        constructor(context: Context, interpolator: Interpolator, duration: Int) : super(context, interpolator) {
            this.mDuration = duration
        }

        override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int, duration: Int) {
            super.startScroll(startX, startY, dx, dy, this.mDuration)
        }
    }

    private val mRunnable = object : Runnable {
        override fun run() {
            if (DEBUG) {
                Log.e("ezy", "running=$mIsRunning,pos=$mCurrentPosition")
            }
            if (mIsRunning) {
                vViewPager.currentItem = mCurrentPosition + 1
                if (isLoop() || mCurrentPosition + 1 < mDataList.size) {
                    postDelayed(this, mInterval)
                } else {
                    mIsRunning = false
                }
            }
        }
    }


}