package com.eelly.core.widget.refresh;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.eelly.core.R;
import com.eelly.core.widget.refresh.api.IRefreshFooter;
import com.eelly.core.widget.refresh.api.IRefreshKernel;
import com.eelly.core.widget.refresh.api.IRefreshLayout;
import com.eelly.core.widget.refresh.constant.RefreshState;
import com.eelly.core.widget.refresh.constant.SpinnerStyle;
import com.eelly.core.widget.refresh.path.PathsDrawable;
import com.eelly.core.widget.refresh.tools.DensityUtil;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * @author Vurtne on 14-Dec-17.
 */
public class DefaultFooter extends RelativeLayout implements IRefreshFooter {

    public static String REFRESH_FOOTER_PULLUP = "上拉加载更多";
    public static String REFRESH_FOOTER_RELEASE = "释放立即加载";
    public static String REFRESH_FOOTER_LOADING = "正在加载...";
    public static String REFRESH_FOOTER_REFRESHING = "正在刷新...";
    public static String REFRESH_FOOTER_FINISH = "加载完成";
    public static String REFRESH_FOOTER_FAILED = "加载失败";
    public static String REFRESH_FOOTER_ALLLOADED = "全部加载完成";

    protected TextView mTitleText;
    protected ImageView mArrowView;
    protected ImageView mProgressView;
    protected PathsDrawable mArrowDrawable;
    protected ProgressDrawable mProgressDrawable;
    protected SpinnerStyle mSpinnerStyle = SpinnerStyle.Translate;
    protected IRefreshKernel mRefreshKernel;
    protected int mFinishDuration = 500;
    protected int mBackgroundColor = 0;
    protected boolean mLoadmoreFinished = false;
    protected int mPaddingTop = 20;
    protected int mPaddingBottom = 20;

    //<editor-fold desc="LinearLayout">
    public DefaultFooter(Context context) {
        this(context,null);
    }

    public DefaultFooter(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public DefaultFooter(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initView(context, attrs, defStyleAttr);
    }

    private void initView(Context context, AttributeSet attrs, int defStyleAttr) {

        mTitleText = new TextView(context);
        mTitleText.setId(android.R.id.widget_frame);
        mTitleText.setTextColor(0xff666666);
        mTitleText.setText(REFRESH_FOOTER_PULLUP);

        LayoutParams lpBottomText = new LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        lpBottomText.addRule(CENTER_IN_PARENT);
        addView(mTitleText, lpBottomText);

        LayoutParams lpArrow = new LayoutParams(DensityUtil.dip2px(context,20),
                DensityUtil.dip2px(context,20));
        lpArrow.addRule(CENTER_VERTICAL);
        lpArrow.addRule(LEFT_OF, android.R.id.widget_frame);
        mArrowView = new ImageView(context);
        addView(mArrowView, lpArrow);

        LayoutParams lpProgress = new LayoutParams((ViewGroup.LayoutParams)lpArrow);
        lpProgress.addRule(CENTER_VERTICAL);
        lpProgress.addRule(LEFT_OF, android.R.id.widget_frame);
        mProgressView = new ImageView(context);
        mProgressView.animate().setInterpolator(new LinearInterpolator());
        addView(mProgressView, lpProgress);

        if (!isInEditMode()) {
            mProgressView.setVisibility(GONE);
        } else {
            mArrowView.setVisibility(GONE);
        }

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.DefaultFooter);

        lpProgress.rightMargin = ta.getDimensionPixelSize(R.styleable.DefaultFooter_srlDrawableMarginRight,
                DensityUtil.dip2px(context,20));
        lpArrow.rightMargin = lpProgress.rightMargin;

        lpArrow.width = ta.getLayoutDimension(R.styleable.DefaultHeader_srlDrawableArrowSize, lpArrow.width);
        lpArrow.height = ta.getLayoutDimension(R.styleable.DefaultHeader_srlDrawableArrowSize, lpArrow.height);
        lpProgress.width = ta.getLayoutDimension(R.styleable.DefaultHeader_srlDrawableProgressSize, lpProgress.width);
        lpProgress.height = ta.getLayoutDimension(R.styleable.DefaultHeader_srlDrawableProgressSize, lpProgress.height);

        lpArrow.width = ta.getLayoutDimension(R.styleable.DefaultHeader_srlDrawableSize, lpArrow.width);
        lpArrow.height = ta.getLayoutDimension(R.styleable.DefaultHeader_srlDrawableSize, lpArrow.height);
        lpProgress.width = ta.getLayoutDimension(R.styleable.DefaultHeader_srlDrawableSize, lpProgress.width);
        lpProgress.height = ta.getLayoutDimension(R.styleable.DefaultHeader_srlDrawableSize, lpProgress.height);

        mFinishDuration = ta.getInt(R.styleable.DefaultFooter_srlFinishDuration, mFinishDuration);
        mSpinnerStyle = SpinnerStyle.values()[ta.getInt(R.styleable.DefaultFooter_srlDefaultSpinnerStyle, mSpinnerStyle.ordinal())];

        if (ta.hasValue(R.styleable.DefaultFooter_srlDrawableArrow)) {
            mArrowView.setImageDrawable(ta.getDrawable(R.styleable.DefaultFooter_srlDrawableArrow));
        } else {
            mArrowDrawable = new PathsDrawable();
            mArrowDrawable.parserColors(0xff666666);
            mArrowDrawable.parserPaths("M20,12l-1.41,-1.41L13,16.17V4h-2v12.17l-5.58,-5.59L4,12l8,8 8,-8z");
            mArrowView.setImageDrawable(mArrowDrawable);
        }

        if (ta.hasValue(R.styleable.DefaultFooter_srlDrawableProgress)) {
            mProgressView.setImageDrawable(ta.getDrawable(R.styleable.DefaultFooter_srlDrawableProgress));
        } else {
            mProgressDrawable = new ProgressDrawable();
            mProgressDrawable.setColor(0xff666666);
            mProgressView.setImageDrawable(mProgressDrawable);
        }

        if (ta.hasValue(R.styleable.DefaultFooter_srlTextSizeTitle)) {
            mTitleText.setTextSize(TypedValue.COMPLEX_UNIT_PX, ta.getDimensionPixelSize(R.styleable.DefaultFooter_srlTextSizeTitle, DensityUtil.dp2px(16)));
        } else {
            mTitleText.setTextSize(16);
        }

        if (ta.hasValue(R.styleable.DefaultFooter_srlPrimaryColor)) {
            setPrimaryColor(ta.getColor(R.styleable.DefaultFooter_srlPrimaryColor, 0));
        }
        if (ta.hasValue(R.styleable.DefaultFooter_srlAccentColor)) {
            setAccentColor(ta.getColor(R.styleable.DefaultFooter_srlAccentColor, 0));
        }

        ta.recycle();

        if (getPaddingTop() == 0) {
            if (getPaddingBottom() == 0) {
                setPadding(getPaddingLeft(), mPaddingTop = DensityUtil.dip2px(context,20),
                        getPaddingRight(), mPaddingBottom = DensityUtil.dip2px(context,20));
            } else {
                setPadding(getPaddingLeft(), mPaddingTop = DensityUtil.dip2px(context,20),
                        getPaddingRight(), mPaddingBottom = getPaddingBottom());
            }
        } else {
            if (getPaddingBottom() == 0) {
                setPadding(getPaddingLeft(), mPaddingTop = getPaddingTop(), getPaddingRight(),
                        mPaddingBottom = DensityUtil.dip2px(context,20));
            } else {
                mPaddingTop = getPaddingTop();
                mPaddingBottom = getPaddingBottom();
            }
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY) {
            setPadding(getPaddingLeft(), 0, getPaddingRight(), 0);
        } else {
            setPadding(getPaddingLeft(), mPaddingTop, getPaddingRight(), mPaddingBottom);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    //</editor-fold>

    //<editor-fold desc="RefreshFooter">

    @Override
    public void onInitialized(IRefreshKernel kernel, int height, int extendHeight) {
        mRefreshKernel = kernel;
        mRefreshKernel.requestDrawBackgoundForFooter(mBackgroundColor);
    }

    @Override
    public boolean isSupportHorizontalDrag() {
        return false;
    }

    @Override
    public void onHorizontalDrag(float percentX, int offsetX, int offsetMax) {
    }

    @Override
    public void onPullingUp(float percent, int offset, int footerHeight, int extendHeight) {

    }

    @Override
    public void onPullReleasing(float percent, int offset, int headHeight, int extendHeight) {

    }

    @Override
    public void onLoadMoreReleased(IRefreshLayout layout, int footerHeight, int extendHeight) {
        if (!mLoadmoreFinished) {
            mProgressView.setVisibility(VISIBLE);
            if (mProgressDrawable != null) {
                mProgressDrawable.start();
            } else {
                mProgressView.animate().rotation(36000).setDuration(100000);
            }
        }
    }

    @Override
    public void onStartAnimator(IRefreshLayout layout, int headHeight, int extendHeight) {

    }

    @Override
    public int onFinish(IRefreshLayout layout, boolean success) {
        if (!mLoadmoreFinished) {
            if (mProgressDrawable != null) {
                mProgressDrawable.stop();
            } else {
                mProgressView.animate().rotation(0).setDuration(300);
            }
            mProgressView.setVisibility(GONE);
            if (success) {
                mTitleText.setText(REFRESH_FOOTER_FINISH);
            } else {
                mTitleText.setText(REFRESH_FOOTER_FAILED);
            }
            return mFinishDuration;
        }
        return 0;
    }

    /**
     * ClassicsFooter 在(SpinnerStyle.FixedBehind)时才有主题色
     */
    @Override
    @Deprecated
    public void setPrimaryColors(@ColorInt int ... colors) {
        if (mSpinnerStyle == SpinnerStyle.FixedBehind) {
            if (colors.length > 0) {
                if (!(getBackground() instanceof BitmapDrawable)) {
                    setPrimaryColor(colors[0]);
                }
                if (colors.length > 1) {
                    setAccentColor(colors[1]);
                } else {
                    setAccentColor(colors[0] == 0xffffffff ? 0xff666666 : 0xffffffff);
                }
            }
        }
    }

    /**
     * 设置数据全部加载完成，将不能再次触发加载功能
     */
    @Override
    public boolean setLoadMoreFinished(boolean finished) {
        if (mLoadmoreFinished != finished) {
            mLoadmoreFinished = finished;
            if (finished) {
                mTitleText.setText(REFRESH_FOOTER_ALLLOADED);
                mArrowView.setVisibility(GONE);
            } else {
                mTitleText.setText(REFRESH_FOOTER_PULLUP);
                mArrowView.setVisibility(VISIBLE);
            }
            if (mProgressDrawable != null) {
                mProgressDrawable.stop();
            } else {
                mProgressView.animate().rotation(0).setDuration(300);
            }
            mProgressView.setVisibility(GONE);
        }
        return true;
    }

    @NonNull
    @Override
    public View getView() {
        return this;
    }

    @Override
    public SpinnerStyle getSpinnerStyle() {
        return mSpinnerStyle;
    }

    @Override
    public void onStateChanged(IRefreshLayout refreshLayout, RefreshState oldState, RefreshState newState) {
        if (!mLoadmoreFinished) {
            switch (newState) {
                case None:
//                    restoreRefreshLayoutBackground();
                    mArrowView.setVisibility(VISIBLE);
                case PullToUpLoad:
                    mTitleText.setText(REFRESH_FOOTER_PULLUP);
                    mArrowView.animate().rotation(180);
                    break;
                case Loading:
                case LoadReleased:
                    mArrowView.setVisibility(GONE);
                    mTitleText.setText(REFRESH_FOOTER_LOADING);
                    break;
                case ReleaseToLoad:
                    mTitleText.setText(REFRESH_FOOTER_RELEASE);
                    mArrowView.animate().rotation(0);
//                    replaceRefreshLayoutBackground(refreshLayout);
                    break;
                case Refreshing:
                    mTitleText.setText(REFRESH_FOOTER_REFRESHING);
                    mProgressView.setVisibility(GONE);
                    mArrowView.setVisibility(GONE);
                    break;
                default:break;
            }
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
    public DefaultFooter setProgressBitmap(Bitmap bitmap) {
        mProgressDrawable = null;
        mProgressView.setImageBitmap(bitmap);
        return this;
    }
    public DefaultFooter setProgressDrawable(Drawable drawable) {
        mProgressDrawable = null;
        mProgressView.setImageDrawable(drawable);
        return this;
    }
    public DefaultFooter setProgressResource(@DrawableRes int resId) {
        mProgressDrawable = null;
        mProgressView.setImageResource(resId);
        return this;
    }
    public DefaultFooter setArrowBitmap(Bitmap bitmap) {
        mArrowDrawable = null;
        mArrowView.setImageBitmap(bitmap);
        return this;
    }
    public DefaultFooter setArrowDrawable(Drawable drawable) {
        mArrowDrawable = null;
        mArrowView.setImageDrawable(drawable);
        return this;
    }
    public DefaultFooter setArrowResource(@DrawableRes int resId) {
        mArrowDrawable = null;
        mArrowView.setImageResource(resId);
        return this;
    }
    public DefaultFooter setSpinnerStyle(SpinnerStyle style) {
        this.mSpinnerStyle = style;
        return this;
    }
    public DefaultFooter setAccentColor(@ColorInt int accentColor) {
        mTitleText.setTextColor(accentColor);
        if (mProgressDrawable != null) {
            mProgressDrawable.setColor(accentColor);
        }
        if (mArrowDrawable != null) {
            mArrowDrawable.parserColors(accentColor);
        }
        return this;
    }
    public DefaultFooter setPrimaryColor(@ColorInt int primaryColor) {
        setBackgroundColor(mBackgroundColor = primaryColor);
        if (mRefreshKernel != null) {
            mRefreshKernel.requestDrawBackgoundForFooter(mBackgroundColor);
        }
        return this;
    }

    public DefaultFooter setPrimaryColorId(@ColorRes int colorId) {
        setPrimaryColor(ContextCompat.getColor(getContext(), colorId));
        return this;
    }

    public DefaultFooter setAccentColorId(@ColorRes int colorId) {
        setAccentColor(ContextCompat.getColor(getContext(), colorId));
        return this;
    }

    public DefaultFooter setFinishDuration(int delay) {
        mFinishDuration = delay;
        return this;
    }

    public DefaultFooter setTextSizeTitle(float size) {
        mTitleText.setTextSize(size);
        if (mRefreshKernel != null) {
            mRefreshKernel.requestRemeasureHeightForFooter();
        }
        return this;
    }

    public DefaultFooter setTextSizeTitle(int unit, float size) {
        mTitleText.setTextSize(unit, size);
        if (mRefreshKernel != null) {
            mRefreshKernel.requestRemeasureHeightForFooter();
        }
        return this;
    }

    public DefaultFooter setDrawableMarginRight(float dp) {
        return setDrawableMarginRightPx(DensityUtil.dp2px(dp));
    }

    public DefaultFooter setDrawableMarginRightPx(int px) {
        MarginLayoutParams lpArrow = (MarginLayoutParams)mArrowView.getLayoutParams();
        MarginLayoutParams lpProgress = (MarginLayoutParams)mProgressView.getLayoutParams();
        lpArrow.rightMargin = lpProgress.rightMargin = px;
        mArrowView.setLayoutParams(lpArrow);
        mProgressView.setLayoutParams(lpProgress);
        return this;
    }

    public DefaultFooter setDrawableSize(float dp) {
        return setDrawableSizePx(DensityUtil.dp2px(dp));
    }

    public DefaultFooter setDrawableSizePx(int px) {
        ViewGroup.LayoutParams lpArrow = mArrowView.getLayoutParams();
        ViewGroup.LayoutParams lpProgress = mProgressView.getLayoutParams();
        lpArrow.width = lpProgress.width = px;
        lpArrow.height = lpProgress.height = px;
        mArrowView.setLayoutParams(lpArrow);
        mProgressView.setLayoutParams(lpProgress);
        return this;
    }

    public DefaultFooter setDrawableArrowSize(float dp) {
        return setDrawableArrowSizePx(DensityUtil.dp2px(dp));
    }

    public DefaultFooter setDrawableArrowSizePx(int px) {
        ViewGroup.LayoutParams lpArrow = mArrowView.getLayoutParams();
        lpArrow.width = px;
        lpArrow.height = px;
        mArrowView.setLayoutParams(lpArrow);
        return this;
    }

    public DefaultFooter setDrawableProgressSize(float dp) {
        return setDrawableProgressSizePx(DensityUtil.dp2px(dp));
    }

    public DefaultFooter setDrawableProgressSizePx(int px) {
        ViewGroup.LayoutParams lpProgress = mProgressView.getLayoutParams();
        lpProgress.width = px;
        lpProgress.height = px;
        mProgressView.setLayoutParams(lpProgress);
        return this;
    }

    public TextView getTitleText() {
        return mTitleText;
    }

    public ImageView getProgressView() {
        return mProgressView;
    }

    public ImageView getArrowView() {
        return mArrowView;
    }

    //</editor-fold>

}
