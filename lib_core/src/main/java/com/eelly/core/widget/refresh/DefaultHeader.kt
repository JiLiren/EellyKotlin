package com.eelly.core.widget.refresh

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.drawable.Animatable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.annotation.ColorInt
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.support.annotation.RequiresApi
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.eelly.core.R
import com.eelly.core.util.DeviceUtil
import com.eelly.core.widget.refresh.api.IRefreshHeader
import com.eelly.core.widget.refresh.api.IRefreshKernel
import com.eelly.core.widget.refresh.api.IRefreshLayout
import com.eelly.core.widget.refresh.constant.RefreshState
import com.eelly.core.widget.refresh.constant.SpinnerStyle
import com.eelly.core.widget.refresh.path.PathsDrawable
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author Vurtne on 14-Dec-17.
 */
class DefaultHeader : RelativeLayout, IRefreshHeader {

    var REFRESH_HEADER_PULLDOWN = "下拉可以刷新"
    var REFRESH_HEADER_REFRESHING = "正在刷新..."
    var REFRESH_HEADER_LOADING = "正在加载..."
    var REFRESH_HEADER_RELEASE = "释放立即刷新"
    var REFRESH_HEADER_FINISH = "刷新完成"
    var REFRESH_HEADER_FAILED = "刷新失败"
    var REFRESH_HEADER_LASTTIME = "上次更新 M-d HH:mm"
    var REFRESH_HEADER_SECOND_FLOOR = "释放进入二楼"
//    public static String REFRESH_HEADER_LASTTIME = "'Last update' M-d HH:mm";

    protected var KEY_LAST_UPDATE_TIME = "LAST_UPDATE_TIME"

    protected var mLastTime: Date? = null
    protected var mTitleText: TextView ? = null
    protected var mLastUpdateText: TextView ? = null
    protected var mArrowView: ImageView ? = null
    protected var mProgressView: ImageView ? = null
    protected var mShared: SharedPreferences? = null
    protected var mRefreshKernel: IRefreshKernel? = null
    protected var mArrowDrawable: PathsDrawable? = null
    protected var mProgressDrawable: ProgressDrawable? = null
    protected var mSpinnerStyle = SpinnerStyle.Translate
    protected var mFormat: DateFormat = SimpleDateFormat(REFRESH_HEADER_LASTTIME, Locale.CHINA)
    protected var mFinishDuration = 500
    protected var mBackgroundColor: Int = 0
    protected var mPaddingTop = 20
    protected var mPaddingBottom = 20
    protected var mEnableLastTime = true

    //<editor-fold desc="RelativeLayout">
    constructor(context: Context):this(context, null)

    constructor(context: Context, attrs: AttributeSet?): this(context, attrs,0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int):super(context, attrs, defStyleAttr){
        initView(context, attrs)
    }

    private fun initView(context: Context, attrs: AttributeSet?) {
        val layout = LinearLayout(context)
        layout.id = android.R.id.widget_frame
        layout.gravity = Gravity.CENTER_HORIZONTAL
        layout.orientation = LinearLayout.VERTICAL
        mTitleText = TextView(context)
        mTitleText!!.text = REFRESH_HEADER_PULLDOWN
        mTitleText!!.setTextColor(-0x99999a)

        mLastUpdateText = TextView(context)
        mLastUpdateText!!.setTextColor(-0x838384)
        val lpHeaderText = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        layout.addView(mTitleText, lpHeaderText)
        val lpUpdateText = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        layout.addView(mLastUpdateText, lpUpdateText)

        val lpHeaderLayout = RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        lpHeaderLayout.addRule(RelativeLayout.CENTER_IN_PARENT)
        addView(layout, lpHeaderLayout)

        val lpArrow = RelativeLayout.LayoutParams(DeviceUtil.dip2Px(context, 20f),
                DeviceUtil.dip2Px(context, 20f))
        lpArrow.addRule(RelativeLayout.CENTER_VERTICAL)
        lpArrow.addRule(RelativeLayout.LEFT_OF, android.R.id.widget_frame)
        mArrowView = ImageView(context)
        addView(mArrowView, lpArrow)

        val lpProgress = RelativeLayout.LayoutParams(lpArrow as ViewGroup.LayoutParams)
        lpProgress.addRule(RelativeLayout.CENTER_VERTICAL)
        lpProgress.addRule(RelativeLayout.LEFT_OF, android.R.id.widget_frame)
        mProgressView = ImageView(context)
        mProgressView!!.animate().interpolator = LinearInterpolator()
        addView(mProgressView, lpProgress)

        if (isInEditMode) {
            mArrowView!!.visibility = View.GONE
            mTitleText!!.text = REFRESH_HEADER_REFRESHING
        } else {
            mProgressView!!.visibility = View.GONE
        }

        val ta = context.obtainStyledAttributes(attrs, R.styleable.DefaultHeader)

        lpUpdateText.topMargin = ta.getDimensionPixelSize(R.styleable.DefaultHeader_srlTextTimeMarginTop,
                DeviceUtil.dip2Px(context, 0f))
        lpProgress.rightMargin = ta.getDimensionPixelSize(R.styleable.DefaultFooter_srlDrawableMarginRight,
                DeviceUtil.dip2Px(context, 20f))
        lpArrow.rightMargin = lpProgress.rightMargin

        lpArrow.width = ta.getLayoutDimension(R.styleable.DefaultHeader_srlDrawableArrowSize, lpArrow.width)
        lpArrow.height = ta.getLayoutDimension(R.styleable.DefaultHeader_srlDrawableArrowSize, lpArrow.height)
        lpProgress.width = ta.getLayoutDimension(R.styleable.DefaultHeader_srlDrawableProgressSize, lpProgress.width)
        lpProgress.height = ta.getLayoutDimension(R.styleable.DefaultHeader_srlDrawableProgressSize, lpProgress.height)

        lpArrow.width = ta.getLayoutDimension(R.styleable.DefaultHeader_srlDrawableSize, lpArrow.width)
        lpArrow.height = ta.getLayoutDimension(R.styleable.DefaultHeader_srlDrawableSize, lpArrow.height)
        lpProgress.width = ta.getLayoutDimension(R.styleable.DefaultHeader_srlDrawableSize, lpProgress.width)
        lpProgress.height = ta.getLayoutDimension(R.styleable.DefaultHeader_srlDrawableSize, lpProgress.height)

        mFinishDuration = ta.getInt(R.styleable.DefaultHeader_srlFinishDuration, mFinishDuration)
        mEnableLastTime = ta.getBoolean(R.styleable.DefaultHeader_srlEnableLastTime, mEnableLastTime)
        mSpinnerStyle = SpinnerStyle.values()[ta.getInt(R.styleable.DefaultHeader_srlDefaultSpinnerStyle, mSpinnerStyle.ordinal)]

        mLastUpdateText!!.visibility = if (mEnableLastTime) View.VISIBLE else View.GONE

        if (ta.hasValue(R.styleable.DefaultHeader_srlDrawableArrow)) {
            mArrowView!!.setImageDrawable(ta.getDrawable(R.styleable.DefaultHeader_srlDrawableArrow))
        } else {
            mArrowDrawable = PathsDrawable()
            mArrowDrawable!!.parserColors(-0x99999a)
            mArrowDrawable!!.parserPaths("M20,12l-1.41,-1.41L13,16.17V4h-2v12.17l-5.58,-5.59L4,12l8,8 8,-8z")
            mArrowView!!.setImageDrawable(mArrowDrawable)
        }

        if (ta.hasValue(R.styleable.DefaultHeader_srlDrawableProgress)) {
            mProgressView!!.setImageDrawable(ta.getDrawable(R.styleable.DefaultHeader_srlDrawableProgress))
        } else {
            mProgressDrawable = ProgressDrawable()
            mProgressDrawable!!.setColor(-0x99999a)
            mProgressView!!.setImageDrawable(mProgressDrawable)
        }

        if (ta.hasValue(R.styleable.DefaultHeader_srlTextSizeTitle)) {
            mTitleText!!.setTextSize(TypedValue.COMPLEX_UNIT_PX, ta.getDimensionPixelSize(R.styleable.DefaultHeader_srlTextSizeTitle,
                    DeviceUtil.dip2Px(context,16f)).toFloat())
        } else {
            mTitleText!!.textSize = 16f
        }

        if (ta.hasValue(R.styleable.DefaultHeader_srlTextSizeTime)) {
            mLastUpdateText!!.setTextSize(TypedValue.COMPLEX_UNIT_PX, ta.getDimensionPixelSize(R.styleable.DefaultHeader_srlTextSizeTime,
                    DeviceUtil.dip2Px(context,12f)).toFloat())
        } else {
            mLastUpdateText!!.textSize = 12f
        }

        if (ta.hasValue(R.styleable.DefaultHeader_srlPrimaryColor)) {
            setPrimaryColor(ta.getColor(R.styleable.DefaultHeader_srlPrimaryColor, 0))
        }
        if (ta.hasValue(R.styleable.DefaultHeader_srlAccentColor)) {
            setAccentColor(ta.getColor(R.styleable.DefaultHeader_srlAccentColor, 0))
        }

        ta.recycle()

        if (paddingTop == 0) {
            if (paddingBottom == 0) {
                mPaddingTop = DeviceUtil.dip2Px(context, 20f)
                mPaddingBottom = DeviceUtil.dip2Px(context, 20f)
                setPadding(paddingLeft, mPaddingTop, paddingRight, mPaddingBottom)
            } else {
                mPaddingTop = DeviceUtil.dip2Px(context, 20f)
                mPaddingBottom = paddingBottom
                setPadding(paddingLeft,mPaddingTop,paddingRight, mPaddingBottom)
            }
        } else {
            if (paddingBottom == 0) {
                mPaddingTop = paddingTop
                mPaddingBottom = DeviceUtil.dip2Px(context, 20f)
                setPadding(paddingLeft,mPaddingTop, paddingRight,mPaddingBottom)
            } else {
                mPaddingTop = paddingTop
                mPaddingBottom = paddingBottom
            }
        }

        try {//try 不能删除-否则会出现兼容性问题
            if (context is FragmentActivity) {
                val manager = context.supportFragmentManager
                if (manager != null) {
                    @SuppressLint("RestrictedApi")
                    val fragments = manager.fragments
                    if (fragments != null && fragments.size > 0) {
                        setLastUpdateTime(Date())
                        return
                    }
                }
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        KEY_LAST_UPDATE_TIME += context.javaClass.name
        mShared = context.getSharedPreferences("DefaultHeader", Context.MODE_PRIVATE)
        setLastUpdateTime(Date(mShared!!.getLong(KEY_LAST_UPDATE_TIME, System.currentTimeMillis())))

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (View.MeasureSpec.getMode(heightMeasureSpec) == View.MeasureSpec.EXACTLY) {
            setPadding(paddingLeft, 0, paddingRight, 0)
        } else {
            setPadding(paddingLeft, mPaddingTop, paddingRight, mPaddingBottom)
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    //</editor-fold>

    //<editor-fold desc="RefreshHeader">
    override fun onInitialized(kernel: IRefreshKernel, height: Int, extendHeight: Int) {
        mRefreshKernel = kernel
        mRefreshKernel!!.requestDrawBackgoundForHeader(mBackgroundColor)
    }

    override fun isSupportHorizontalDrag(): Boolean {
        return false
    }

    override fun onHorizontalDrag(percentX: Float, offsetX: Int, offsetMax: Int) {}

    override fun onPullingDown(percent: Float, offset: Int, height: Int, extendHeight: Int) {}

    override fun onReleasing(percent: Float, offset: Int, height: Int, extendHeight: Int) {

    }

    override fun onRefreshReleased(layout: IRefreshLayout, headerHeight: Int, extendHeight: Int) {
        if (mProgressDrawable != null) {
            mProgressDrawable!!.start()
        } else {
            val drawable = mProgressView!!.drawable
            if (drawable is Animatable) {
                (drawable as Animatable).start()
            } else {
                mProgressView!!.animate().rotation(36000f).duration = 100000
            }
        }
    }

    override fun onStartAnimator(layout: IRefreshLayout, height: Int, extendHeight: Int) {

    }

    override fun onFinish(layout: IRefreshLayout, success: Boolean): Int {
        if (mProgressDrawable != null) {
            mProgressDrawable!!.stop()
        } else {
            val drawable = mProgressView!!.drawable
            if (drawable is Animatable) {
                (drawable as Animatable).stop()
            } else {
                mProgressView!!.animate().rotation(0f).duration = 300
            }
        }
        mProgressView!!.visibility = View.GONE
        if (success) {
            mTitleText!!.text = REFRESH_HEADER_FINISH
            if (mLastTime != null) {
                setLastUpdateTime(Date())
            }
        } else {
            mTitleText!!.text = REFRESH_HEADER_FAILED
        }
        //延迟500毫秒之后再弹回
        return mFinishDuration
    }

    @Deprecated("")
    override fun setPrimaryColors(@ColorInt colors: IntArray) {
        if (colors.isNotEmpty()) {
            if (background !is BitmapDrawable) {
                setPrimaryColor(colors[0])
            }
            if (colors.size > 1) {
                setAccentColor(colors[1])
            } else {
                setAccentColor(if (colors[0] == -0x1) -0x99999a else -0x1)
            }
        }
    }

    override fun getView(): View {
        return this
    }

    override fun getSpinnerStyle(): SpinnerStyle {
        return mSpinnerStyle
    }

    override fun onStateChanged(refreshLayout: IRefreshLayout, oldState: RefreshState, newState: RefreshState) {
        when (newState) {
            RefreshState.None -> {
                mLastUpdateText!!.visibility = if (mEnableLastTime) View.VISIBLE else View.GONE
                mTitleText!!.text = REFRESH_HEADER_PULLDOWN
                mArrowView!!.visibility = View.VISIBLE
                mProgressView!!.visibility = View.GONE
                mArrowView!!.animate().rotation(0f)
            }
            RefreshState.PullDownToRefresh -> {
                mTitleText!!.text = REFRESH_HEADER_PULLDOWN
                mArrowView!!.visibility = View.VISIBLE
                mProgressView!!.visibility = View.GONE
                mArrowView!!.animate().rotation(0f)
            }
            RefreshState.Refreshing, RefreshState.RefreshReleased -> {
                mTitleText!!.text = REFRESH_HEADER_REFRESHING
                mProgressView!!.visibility = View.VISIBLE
                mArrowView!!.visibility = View.GONE
            }
            RefreshState.ReleaseToRefresh -> {
                mTitleText!!.text = REFRESH_HEADER_RELEASE
                mArrowView!!.animate().rotation(180f)
            }
            RefreshState.ReleaseToTwoLevel -> {
                mTitleText!!.text = REFRESH_HEADER_SECOND_FLOOR
                mArrowView!!.animate().rotation(0f)
            }
            RefreshState.Loading -> {
                mArrowView!!.visibility = View.GONE
                mProgressView!!.visibility = View.GONE
                mLastUpdateText!!.visibility = View.GONE
                mTitleText!!.text = REFRESH_HEADER_LOADING
            }
            else -> {
            }
        }
    }
    //</editor-fold>

    //<editor-fold desc="API">
    fun setProgressBitmap(bitmap: Bitmap): DefaultHeader {
        mProgressDrawable = null
        mProgressView!!.setImageBitmap(bitmap)
        return this
    }

    fun setProgressDrawable(drawable: Drawable): DefaultHeader {
        mProgressDrawable = null
        mProgressView!!.setImageDrawable(drawable)
        return this
    }

    fun setProgressResource(@DrawableRes resId: Int): DefaultHeader {
        mProgressDrawable = null
        mProgressView!!.setImageResource(resId)
        return this
    }

    fun setArrowBitmap(bitmap: Bitmap): DefaultHeader {
        mArrowDrawable = null
        mArrowView!!.setImageBitmap(bitmap)
        return this
    }

    fun setArrowDrawable(drawable: Drawable): DefaultHeader {
        mArrowDrawable = null
        mArrowView!!.setImageDrawable(drawable)
        return this
    }

    fun setArrowResource(@DrawableRes resId: Int): DefaultHeader {
        mArrowDrawable = null
        mArrowView!!.setImageResource(resId)
        return this
    }

    fun setLastUpdateTime(time: Date): DefaultHeader {
        mLastTime = time
        mLastUpdateText!!.text = mFormat.format(time)
        if (mShared != null && !isInEditMode) {
            mShared!!.edit().putLong(KEY_LAST_UPDATE_TIME, time.time).apply()
        }
        return this
    }

    fun setLastUpdateText(text: CharSequence): DefaultHeader {
        mLastTime = null
        mLastUpdateText!!.setText(text)
        return this
    }

    fun setTimeFormat(format: DateFormat): DefaultHeader {
        mFormat = format
        if (mLastTime != null) {
            mLastUpdateText!!.setText(mFormat.format(mLastTime))
        }
        return this
    }

    fun setSpinnerStyle(style: SpinnerStyle): DefaultHeader {
        this.mSpinnerStyle = style
        return this
    }

    fun setPrimaryColor(@ColorInt primaryColor: Int): DefaultHeader {
        mBackgroundColor = primaryColor
        setBackgroundColor(mBackgroundColor)
        if (mRefreshKernel != null) {
            mRefreshKernel!!.requestDrawBackgoundForHeader(mBackgroundColor)
        }
        return this
    }

    fun setAccentColor(@ColorInt accentColor: Int): DefaultHeader {
        if (mArrowDrawable != null) {
            mArrowDrawable!!.parserColors(accentColor)
        }
        if (mProgressDrawable != null) {
            mProgressDrawable!!.setColor(accentColor)
        }
        mTitleText!!.setTextColor(accentColor)
        mLastUpdateText!!.setTextColor(accentColor and 0x00ffffff or -0x34000000)
        return this
    }

    fun setPrimaryColorId(@ColorRes colorId: Int): DefaultHeader {
        setPrimaryColor(ContextCompat.getColor(context, colorId))
        return this
    }

    fun setAccentColorId(@ColorRes colorId: Int): DefaultHeader {
        setAccentColor(ContextCompat.getColor(context, colorId))
        return this
    }

    fun setFinishDuration(delay: Int): DefaultHeader {
        mFinishDuration = delay
        return this
    }

    fun setEnableLastTime(enable: Boolean): DefaultHeader {
        mEnableLastTime = enable
        mLastUpdateText!!.visibility = if (enable) View.VISIBLE else View.GONE
        if (mRefreshKernel != null) {
            mRefreshKernel!!.requestRemeasureHeightForHeader()
        }
        return this
    }

    fun setTextSizeTitle(size: Float): DefaultHeader {
        mTitleText!!.textSize = size
        if (mRefreshKernel != null) {
            mRefreshKernel!!.requestRemeasureHeightForHeader()
        }
        return this
    }

    fun setTextSizeTitle(unit: Int, size: Float): DefaultHeader {
        mTitleText!!.setTextSize(unit, size)
        if (mRefreshKernel != null) {
            mRefreshKernel!!.requestRemeasureHeightForHeader()
        }
        return this
    }

    fun setTextSizeTime(size: Float): DefaultHeader {
        mLastUpdateText!!.textSize = size
        if (mRefreshKernel != null) {
            mRefreshKernel!!.requestRemeasureHeightForHeader()
        }
        return this
    }

    fun setTextSizeTime(unit: Int, size: Float): DefaultHeader {
        mLastUpdateText!!.setTextSize(unit, size)
        if (mRefreshKernel != null) {
            mRefreshKernel!!.requestRemeasureHeightForHeader()
        }
        return this
    }

    fun setTextTimeMarginTop(dp: Float): DefaultHeader {
        return setTextTimeMarginTopPx(DeviceUtil.dip2Px(context,dp))
    }

    fun setTextTimeMarginTopPx(px: Int): DefaultHeader {
        val lp = mLastUpdateText!!.layoutParams as ViewGroup.MarginLayoutParams
        lp.topMargin = px
        mLastUpdateText!!.layoutParams = lp
        return this
    }

    fun setDrawableMarginRight(dp: Float): DefaultHeader {
        return setDrawableMarginRightPx(DeviceUtil.dip2Px(context,dp))
    }

    fun setDrawableMarginRightPx(px: Int): DefaultHeader {
        val lpArrow = mArrowView!!.layoutParams as ViewGroup.MarginLayoutParams
        val lpProgress = mProgressView!!.layoutParams as ViewGroup.MarginLayoutParams
        lpProgress.rightMargin = px
        lpArrow.rightMargin = lpProgress.rightMargin
        mArrowView!!.layoutParams = lpArrow
        mProgressView!!.layoutParams = lpProgress
        return this
    }

    fun setDrawableSize(dp: Float): DefaultHeader {
        return setDrawableSizePx(DeviceUtil.dip2Px(context,dp))
    }

    fun setDrawableSizePx(px: Int): DefaultHeader {
        val lpArrow = mArrowView!!.layoutParams
        val lpProgress = mProgressView!!.layoutParams
        lpProgress.width = px
        lpArrow.width = lpProgress.width
        lpProgress.height = px
        lpArrow.height = lpProgress.height
        mArrowView!!.layoutParams = lpArrow
        mProgressView!!.layoutParams = lpProgress
        return this
    }

    fun setDrawableArrowSize(dp: Float): DefaultHeader {
        return setDrawableArrowSizePx(DeviceUtil.dip2Px(context,dp))
    }

    fun setDrawableArrowSizePx(px: Int): DefaultHeader {
        val lpArrow = mArrowView!!.layoutParams
        lpArrow.width = px
        lpArrow.height = px
        mArrowView!!.layoutParams = lpArrow
        return this
    }

    fun setDrawableProgressSize(dp: Float): DefaultHeader {
        return setDrawableProgressSizePx(DeviceUtil.dip2Px(context,dp))
    }

    fun setDrawableProgressSizePx(px: Int): DefaultHeader {
        val lpProgress = mProgressView!!.layoutParams
        lpProgress.width = px
        lpProgress.height = px
        mProgressView!!.layoutParams = lpProgress
        return this
    }

    fun getArrowView(): ImageView {
        return mArrowView!!
    }

    fun getProgressView(): ImageView {
        return mProgressView!!
    }

    fun getTitleText(): TextView {
        return mTitleText!!
    }

    fun getLastUpdateText(): TextView {
        return mLastUpdateText!!
    }

    //</editor-fold>

}