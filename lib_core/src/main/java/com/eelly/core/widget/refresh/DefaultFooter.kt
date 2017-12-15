package com.eelly.core.widget.refresh

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.support.annotation.ColorInt
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.eelly.core.R
import com.eelly.core.util.DeviceUtil
import com.eelly.core.widget.refresh.api.IRefreshFooter
import com.eelly.core.widget.refresh.api.IRefreshKernel
import com.eelly.core.widget.refresh.api.IRefreshLayout
import com.eelly.core.widget.refresh.constant.RefreshState
import com.eelly.core.widget.refresh.constant.SpinnerStyle
import com.eelly.core.widget.refresh.path.PathsDrawable

/**
 * @author Vurtne on 14-Dec-17.
 */
class DefaultFooter : RelativeLayout, IRefreshFooter {

    var REFRESH_FOOTER_PULLUP = "上拉加载更多"
    var REFRESH_FOOTER_RELEASE = "释放立即加载"
    var REFRESH_FOOTER_LOADING = "正在加载..."
    var REFRESH_FOOTER_REFRESHING = "正在刷新..."
    var REFRESH_FOOTER_FINISH = "加载完成"
    var REFRESH_FOOTER_FAILED = "加载失败"
    var REFRESH_FOOTER_ALLLOADED = "全部加载完成"

    protected var mTitleText: TextView ?=null
    protected var mArrowView: ImageView ?=null
    protected var mProgressView: ImageView ?=null
    protected var mArrowDrawable: PathsDrawable? = null
    protected var mProgressDrawable: ProgressDrawable? = null
    protected var mSpinnerStyle = SpinnerStyle.Translate
    protected var mRefreshKernel: IRefreshKernel? = null
    protected var mFinishDuration = 500
    protected var mBackgroundColor = 0
    protected var mLoadmoreFinished = false
    protected var mPaddingTop = 20
    protected var mPaddingBottom  = 20
    //<editor-fold desc="LinearLayout">
    constructor(context: Context): this(context, null)

    constructor(context: Context, attrs: AttributeSet?):this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int):super(context, attrs, defStyleAttr){
        initView(context, attrs!!, defStyleAttr)
    }

    fun initView(context: Context, attrs: AttributeSet, defStyleAttr: Int) {
        mTitleText = TextView(context)
        mTitleText!!.id = android.R.id.widget_frame
        mTitleText!!.setTextColor(-0x99999a)
        mTitleText!!.text =REFRESH_FOOTER_PULLUP

        val lpBottomText = RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        lpBottomText.addRule(RelativeLayout.CENTER_IN_PARENT)
        addView(mTitleText, lpBottomText)

        val lpArrow = RelativeLayout.LayoutParams(DeviceUtil.dip2Px(context, 20F),
                DeviceUtil.dip2Px(context, 20F))
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

        if (!isInEditMode) {
            mProgressView!!.visibility=View.GONE
        } else {
            mArrowView!!.visibility=View.GONE
        }
        val ta = context.obtainStyledAttributes(attrs, R.styleable.DefaultFooter)

        lpProgress.rightMargin = ta.getDimensionPixelSize(R.styleable.DefaultFooter_srlDrawableMarginRight,
                DeviceUtil.dip2Px(context, 20F))
        lpArrow.rightMargin = lpProgress.rightMargin

        lpArrow.width = ta.getLayoutDimension(R.styleable.DefaultHeader_srlDrawableArrowSize, lpArrow.width)
        lpArrow.height = ta.getLayoutDimension(R.styleable.DefaultHeader_srlDrawableArrowSize, lpArrow.height)
        lpProgress.width = ta.getLayoutDimension(R.styleable.DefaultHeader_srlDrawableProgressSize, lpProgress.width)
        lpProgress.height = ta.getLayoutDimension(R.styleable.DefaultHeader_srlDrawableProgressSize, lpProgress.height)

        lpArrow.width = ta.getLayoutDimension(R.styleable.DefaultHeader_srlDrawableSize, lpArrow.width)
        lpArrow.height = ta.getLayoutDimension(R.styleable.DefaultHeader_srlDrawableSize, lpArrow.height)
        lpProgress.width = ta.getLayoutDimension(R.styleable.DefaultHeader_srlDrawableSize, lpProgress.width)
        lpProgress.height = ta.getLayoutDimension(R.styleable.DefaultHeader_srlDrawableSize, lpProgress.height)

        mFinishDuration = ta.getInt(R.styleable.DefaultFooter_srlFinishDuration, mFinishDuration)
        mSpinnerStyle = SpinnerStyle.values()[ta.getInt(R.styleable.DefaultFooter_srlDefaultSpinnerStyle,
                mSpinnerStyle.ordinal)]

        if (ta.hasValue(R.styleable.DefaultFooter_srlDrawableArrow)) {
            mArrowView!!.setImageDrawable(ta.getDrawable(R.styleable.DefaultFooter_srlDrawableArrow))
        } else {
            mArrowDrawable = PathsDrawable()
            mArrowDrawable!!.parserColors(-0x99999a)
            mArrowDrawable!!.parserPaths("M20,12l-1.41,-1.41L13,16.17V4h-2v12.17l-5.58,-5.59L4,12l8,8 8,-8z")
            mArrowView!!.setImageDrawable(mArrowDrawable)
        }

        if (ta.hasValue(R.styleable.DefaultFooter_srlDrawableProgress)) {
            mProgressView!!.setImageDrawable(ta.getDrawable(R.styleable.DefaultFooter_srlDrawableProgress))
        } else {
            mProgressDrawable = ProgressDrawable()
            mProgressDrawable!!.setColor(-0x99999a)
            mProgressView!!.setImageDrawable(mProgressDrawable)
        }

        if (ta.hasValue(R.styleable.DefaultFooter_srlTextSizeTitle)) {
            mTitleText!!.setTextSize(TypedValue.COMPLEX_UNIT_PX, ta.getDimensionPixelSize(
                    R.styleable.DefaultFooter_srlTextSizeTitle, DeviceUtil.dip2Px(context,
                    16F)).toFloat())
        } else {
            mTitleText!!.textSize = 16f
        }

        if (ta.hasValue(R.styleable.DefaultFooter_srlPrimaryColor)) {
            setPrimaryColor(ta.getColor(R.styleable.DefaultFooter_srlPrimaryColor, 0))
        }
        if (ta.hasValue(R.styleable.DefaultFooter_srlAccentColor)) {
            setAccentColor(ta.getColor(R.styleable.DefaultFooter_srlAccentColor, 0))
        }

        ta.recycle()

        if (paddingTop == 0) {
            if (paddingBottom == 0) {
                mPaddingTop = DeviceUtil.dip2Px(context, 20F)
                mPaddingBottom = DeviceUtil.dip2Px(context, 20F)
                setPadding(paddingLeft,mPaddingTop.toInt(), paddingRight,mPaddingBottom.toInt())
            } else {
                mPaddingTop = DeviceUtil.dip2Px(context, 20F) 
                mPaddingBottom = paddingBottom 
                setPadding(paddingLeft, mPaddingTop.toInt(), paddingRight, mPaddingBottom.toInt())
            }
        } else {
            if (paddingBottom == 0) {
                mPaddingTop = paddingTop 
                mPaddingBottom = DeviceUtil.dip2Px(context, 20F) 
                setPadding(paddingLeft, mPaddingTop.toInt(), paddingRight, mPaddingBottom.toInt())
            } else {
                mPaddingTop = paddingTop 
                mPaddingBottom = paddingBottom 
            }
        }
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

    //<editor-fold desc="RefreshFooter">

    override fun onInitialized(kernel: IRefreshKernel, height: Int, extendHeight: Int) {
        mRefreshKernel = kernel
        mRefreshKernel!!.requestDrawBackgoundForFooter(mBackgroundColor)
    }

    override fun isSupportHorizontalDrag(): Boolean {
        return false
    }

    override fun onHorizontalDrag(percentX: Float, offsetX: Int, offsetMax: Int) {}

    override fun onPullingUp(percent: Float, offset: Int, footerHeight: Int, extendHeight: Int) {

    }

    override fun onPullReleasing(percent: Float, offset: Int, headHeight: Int, extendHeight: Int) {

    }

    override fun onLoadMoreReleased(layout: IRefreshLayout, footerHeight: Int, extendHeight: Int) {
        if (!mLoadmoreFinished) {
            mProgressView!!.visibility = View.VISIBLE
            if (mProgressDrawable != null) {
                mProgressDrawable!!.start()
            } else {
                mProgressView!!.animate().rotation(36000f).duration = 100000
            }
        }
    }

    override fun onStartAnimator(layout: IRefreshLayout, headHeight: Int, extendHeight: Int) {

    }

    override fun onFinish(layout: IRefreshLayout, success: Boolean): Int {
        if (!mLoadmoreFinished) {
            if (mProgressDrawable != null) {
                mProgressDrawable!!.stop()
            } else {
                mProgressView!!.animate().rotation(0f).duration = 300
            }
            mProgressView!!.visibility = View.GONE
            if (success) {
                mTitleText!!.text = REFRESH_FOOTER_FINISH
            } else {
                mTitleText!!.text = REFRESH_FOOTER_FAILED
            }
            return mFinishDuration
        }
        return 0
    }

    /**
     * ClassicsFooter 在(SpinnerStyle.FixedBehind)时才有主题色
     */
    @Deprecated("")
    override fun setPrimaryColors(@ColorInt colors: IntArray) {
        if (mSpinnerStyle === SpinnerStyle.FixedBehind) {
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
    }

    /**
     * 设置数据全部加载完成，将不能再次触发加载功能
     */
    override fun setLoadMoreFinished(finished: Boolean): Boolean {
        if (mLoadmoreFinished != finished) {
            mLoadmoreFinished = finished
            if (finished) {
                mTitleText!!.text =REFRESH_FOOTER_ALLLOADED
                mArrowView!!.visibility = View.GONE
            } else {
                mTitleText!!.text = REFRESH_FOOTER_PULLUP
                mArrowView!!.visibility = View.VISIBLE
            }
            if (mProgressDrawable != null) {
                mProgressDrawable!!.stop()
            } else {
                mProgressView!!.animate().rotation(0f).duration = 300
            }
            mProgressView!!.visibility = View.GONE
        }
        return true
    }

    override fun getView(): View {
        return this
    }

    override fun getSpinnerStyle(): SpinnerStyle {
        return mSpinnerStyle
    }

    override fun onStateChanged(refreshLayout: IRefreshLayout, oldState: RefreshState, newState: RefreshState) {
        if (!mLoadmoreFinished) {
            when (newState) {
                RefreshState.None -> {
                    //                    restoreRefreshLayoutBackground();
                    mArrowView!!.visibility = View.VISIBLE
                    mTitleText!!.text = REFRESH_FOOTER_PULLUP
                    mArrowView!!.animate().rotation(180f)
                }
                RefreshState.PullToUpLoad -> {
                    mTitleText!!.text = REFRESH_FOOTER_PULLUP
                    mArrowView!!.animate().rotation(180f)
                }
                RefreshState.Loading, RefreshState.LoadReleased -> {
                    mArrowView!!.visibility = View.GONE
                    mTitleText!!.text = REFRESH_FOOTER_LOADING
                }
                RefreshState.ReleaseToLoad -> {
                    mTitleText!!.text = REFRESH_FOOTER_RELEASE
                    mArrowView!!.animate().rotation(0f)
                }
                RefreshState.Refreshing -> {
                    mTitleText!!.text = REFRESH_FOOTER_REFRESHING
                    mProgressView!!.visibility = View.GONE
                    mArrowView!!.visibility = View.GONE
                }
                else -> {
                }
            }//                    replaceRefreshLayoutBackground(refreshLayout);
        }
    }
    //</editor-fold>

    //<editor-fold desc="Background">
//    private Runnable restoreRunable;
//    private void restoreRefreshLayoutBackground() {
//        if (restoreRunable != null) {
//            restoreRunable.run();
//            restoreRunable = null;
//        }
//    }
//
//    private void replaceRefreshLayoutBackground(final RefreshLayout refreshLayout) {
//        if (restoreRunable == null && mSpinnerStyle == SpinnerStyle.FixedBehind) {
//            restoreRunable = new Runnable() {
//                Drawable drawable = refreshLayout.getLayout().getBackground();
//                @Override
//                public void run() {
//                    refreshLayout.getLayout().setBackgroundDrawable(drawable);
//                }
//            };
//            refreshLayout.getLayout().setBackgroundDrawable(getBackground());
//        }
//    }
    //</editor-fold>

    //<editor-fold desc="API">
    fun setProgressBitmap(bitmap: Bitmap): DefaultFooter {
        mProgressDrawable = null
        mProgressView!!.setImageBitmap(bitmap)
        return this
    }

    fun setProgressDrawable(drawable: Drawable): DefaultFooter {
        mProgressDrawable = null
        mProgressView!!.setImageDrawable(drawable)
        return this
    }

    fun setProgressResource(@DrawableRes resId: Int): DefaultFooter {
        mProgressDrawable = null
        mProgressView!!.setImageResource(resId)
        return this
    }

    fun setArrowBitmap(bitmap: Bitmap): DefaultFooter {
        mArrowDrawable = null
        mArrowView!!.setImageBitmap(bitmap)
        return this
    }

    fun setArrowDrawable(drawable: Drawable): DefaultFooter {
        mArrowDrawable = null
        mArrowView!!.setImageDrawable(drawable)
        return this
    }

    fun setArrowResource(@DrawableRes resId: Int): DefaultFooter {
        mArrowDrawable = null
        mArrowView!!.setImageResource(resId)
        return this
    }

    fun setSpinnerStyle(style: SpinnerStyle): DefaultFooter {
        this.mSpinnerStyle = style
        return this
    }

    fun setAccentColor(@ColorInt accentColor: Int): DefaultFooter {
        mTitleText!!.setTextColor(accentColor)
        if (mProgressDrawable != null) {
            mProgressDrawable!!.setColor(accentColor)
        }
        if (mArrowDrawable != null) {
            mArrowDrawable!!.parserColors(accentColor)
        }
        return this
    }

    fun setPrimaryColor(@ColorInt primaryColor: Int): DefaultFooter {
        mBackgroundColor = primaryColor
        setBackgroundColor(primaryColor)
        if (mRefreshKernel != null) {
            mRefreshKernel!!.requestDrawBackgoundForFooter(mBackgroundColor)
        }
        return this
    }

    fun setPrimaryColorId(@ColorRes colorId: Int): DefaultFooter {
        setPrimaryColor(ContextCompat.getColor(context, colorId))
        return this
    }

    fun setAccentColorId(@ColorRes colorId: Int): DefaultFooter {
        setAccentColor(ContextCompat.getColor(context, colorId))
        return this
    }

    fun setFinishDuration(delay: Int): DefaultFooter {
        mFinishDuration = delay
        return this
    }

    fun setTextSizeTitle(size: Float): DefaultFooter {
        mTitleText!!.textSize = size
        if (mRefreshKernel != null) {
            mRefreshKernel!!.requestRemeasureHeightForFooter()
        }
        return this
    }

    fun setTextSizeTitle(unit: Int, size: Float): DefaultFooter {
        mTitleText!!.setTextSize(unit, size)
        if (mRefreshKernel != null) {
            mRefreshKernel!!.requestRemeasureHeightForFooter()
        }
        return this
    }

    fun setDrawableMarginRight(dp: Float): DefaultFooter {
        return setDrawableMarginRightPx(DeviceUtil.dip2Px(context,dp))
    }

    fun setDrawableMarginRightPx(px: Int): DefaultFooter {
        val lpArrow = mArrowView!!.layoutParams as ViewGroup.MarginLayoutParams
        val lpProgress = mProgressView!!.layoutParams as ViewGroup.MarginLayoutParams
        lpProgress.rightMargin = px
        lpArrow.rightMargin = lpProgress.rightMargin
        mArrowView!!.layoutParams = lpArrow
        mProgressView!!.layoutParams = lpProgress
        return this
    }

    fun setDrawableSize(dp: Float): DefaultFooter {
        return setDrawableSizePx(DeviceUtil.dip2Px(context,dp))
    }

    fun setDrawableSizePx(px: Int): DefaultFooter {
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

    fun setDrawableArrowSize(dp: Float): DefaultFooter {
        return setDrawableArrowSizePx(DeviceUtil.dip2Px(context,dp))
    }

    fun setDrawableArrowSizePx(px: Int): DefaultFooter {
        val lpArrow = mArrowView!!.layoutParams
        lpArrow.width = px
        lpArrow.height = px
        mArrowView!!.layoutParams = lpArrow
        return this
    }

    fun setDrawableProgressSize(dp: Float): DefaultFooter {
        return setDrawableProgressSizePx(DeviceUtil.dip2Px(context,dp))
    }

    fun setDrawableProgressSizePx(px: Int): DefaultFooter {
        val lpProgress = mProgressView!!.layoutParams
        lpProgress.width = px
        lpProgress.height = px
        mProgressView!!.layoutParams = lpProgress
        return this
    }

    fun getTitleText(): TextView {
        return mTitleText!!
    }

    fun getProgressView(): ImageView {
        return mProgressView!!
    }

    fun getArrowView(): ImageView {
        return mArrowView!!
    }

    //</editor-fold>

}