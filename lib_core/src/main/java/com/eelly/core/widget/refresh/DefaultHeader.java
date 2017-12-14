package com.eelly.core.widget.refresh;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.eelly.core.R;
import com.eelly.core.util.DeviceUtil;
import com.eelly.core.widget.refresh.api.IRefreshHeader;
import com.eelly.core.widget.refresh.api.IRefreshKernel;
import com.eelly.core.widget.refresh.api.IRefreshLayout;
import com.eelly.core.widget.refresh.constant.RefreshState;
import com.eelly.core.widget.refresh.constant.SpinnerStyle;
import com.eelly.core.widget.refresh.path.PathsDrawable;
import com.eelly.core.widget.refresh.tools.DensityUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * @author Vurtne on 14-Dec-17.
 */

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class DefaultHeader extends RelativeLayout implements IRefreshHeader {

    public static String REFRESH_HEADER_PULLDOWN = "下拉可以刷新";
    public static String REFRESH_HEADER_REFRESHING = "正在刷新...";
    public static String REFRESH_HEADER_LOADING = "正在加载...";
    public static String REFRESH_HEADER_RELEASE = "释放立即刷新";
    public static String REFRESH_HEADER_FINISH = "刷新完成";
    public static String REFRESH_HEADER_FAILED = "刷新失败";
    public static String REFRESH_HEADER_LASTTIME = "上次更新 M-d HH:mm";
    public static String REFRESH_HEADER_SECOND_FLOOR = "释放进入二楼";
//    public static String REFRESH_HEADER_LASTTIME = "'Last update' M-d HH:mm";

    protected String KEY_LAST_UPDATE_TIME = "LAST_UPDATE_TIME";

    protected Date mLastTime;
    protected TextView mTitleText;
    protected TextView mLastUpdateText;
    protected ImageView mArrowView;
    protected ImageView mProgressView;
    protected SharedPreferences mShared;
    protected IRefreshKernel mRefreshKernel;
    protected PathsDrawable mArrowDrawable;
    protected ProgressDrawable mProgressDrawable;
    protected SpinnerStyle mSpinnerStyle = SpinnerStyle.Translate;
    protected DateFormat mFormat = new SimpleDateFormat(REFRESH_HEADER_LASTTIME, Locale.CHINA);
    protected int mFinishDuration = 500;
    protected int mBackgroundColor;
    protected int mPaddingTop = 20;
    protected int mPaddingBottom = 20;
    protected boolean mEnableLastTime = true;

    //<editor-fold desc="RelativeLayout">
    public DefaultHeader(Context context) {
        super(context);
        this.initView(context, null);
    }

    public DefaultHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initView(context, attrs);
    }

    public DefaultHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initView(context, attrs);
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public DefaultHeader(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {

        LinearLayout layout = new LinearLayout(context);
        layout.setId(android.R.id.widget_frame);
        layout.setGravity(Gravity.CENTER_HORIZONTAL);
        layout.setOrientation(LinearLayout.VERTICAL);
        mTitleText = new TextView(context);
        mTitleText.setText(REFRESH_HEADER_PULLDOWN);
        mTitleText.setTextColor(0xff666666);

        mLastUpdateText = new TextView(context);
        mLastUpdateText.setTextColor(0xff7c7c7c);
        LinearLayout.LayoutParams lpHeaderText = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        layout.addView(mTitleText, lpHeaderText);
        LinearLayout.LayoutParams lpUpdateText = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        layout.addView(mLastUpdateText, lpUpdateText);

        LayoutParams lpHeaderLayout = new LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        lpHeaderLayout.addRule(CENTER_IN_PARENT);
        addView(layout,lpHeaderLayout);

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

        if (isInEditMode()) {
            mArrowView.setVisibility(GONE);
            mTitleText.setText(REFRESH_HEADER_REFRESHING);
        } else {
            mProgressView.setVisibility(GONE);
        }

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.DefaultHeader);

        lpUpdateText.topMargin = ta.getDimensionPixelSize(R.styleable.DefaultHeader_srlTextTimeMarginTop,
                DensityUtil.dip2px(context,0));
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

        mFinishDuration = ta.getInt(R.styleable.DefaultHeader_srlFinishDuration, mFinishDuration);
        mEnableLastTime = ta.getBoolean(R.styleable.DefaultHeader_srlEnableLastTime, mEnableLastTime);
        mSpinnerStyle = SpinnerStyle.values()[ta.getInt(R.styleable.DefaultHeader_srlDefaultSpinnerStyle,mSpinnerStyle.ordinal())];

        mLastUpdateText.setVisibility(mEnableLastTime ? VISIBLE : GONE);

        if (ta.hasValue(R.styleable.DefaultHeader_srlDrawableArrow)) {
            mArrowView.setImageDrawable(ta.getDrawable(R.styleable.DefaultHeader_srlDrawableArrow));
        } else {
            mArrowDrawable = new PathsDrawable();
            mArrowDrawable.parserColors(0xff666666);
            mArrowDrawable.parserPaths("M20,12l-1.41,-1.41L13,16.17V4h-2v12.17l-5.58,-5.59L4,12l8,8 8,-8z");
            mArrowView.setImageDrawable(mArrowDrawable);
        }

        if (ta.hasValue(R.styleable.DefaultHeader_srlDrawableProgress)) {
            mProgressView.setImageDrawable(ta.getDrawable(R.styleable.DefaultHeader_srlDrawableProgress));
        } else {
            mProgressDrawable = new ProgressDrawable();
            mProgressDrawable.setColor(0xff666666);
            mProgressView.setImageDrawable(mProgressDrawable);
        }

        if (ta.hasValue(R.styleable.DefaultHeader_srlTextSizeTitle)) {
            mTitleText.setTextSize(TypedValue.COMPLEX_UNIT_PX, ta.getDimensionPixelSize(R.styleable.DefaultHeader_srlTextSizeTitle, DensityUtil.dp2px(16)));
        } else {
            mTitleText.setTextSize(16);
        }

        if (ta.hasValue(R.styleable.DefaultHeader_srlTextSizeTime)) {
            mLastUpdateText.setTextSize(TypedValue.COMPLEX_UNIT_PX, ta.getDimensionPixelSize(R.styleable.DefaultHeader_srlTextSizeTime, DensityUtil.dp2px(12)));
        } else {
            mLastUpdateText.setTextSize(12);
        }

        if (ta.hasValue(R.styleable.DefaultHeader_srlPrimaryColor)) {
            setPrimaryColor(ta.getColor(R.styleable.DefaultHeader_srlPrimaryColor, 0));
        }
        if (ta.hasValue(R.styleable.DefaultHeader_srlAccentColor)) {
            setAccentColor(ta.getColor(R.styleable.DefaultHeader_srlAccentColor, 0));
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

        try {//try 不能删除-否则会出现兼容性问题
            if (context instanceof FragmentActivity) {
                FragmentManager manager = ((FragmentActivity) context).getSupportFragmentManager();
                if (manager != null) {
                    @SuppressLint("RestrictedApi")
                    List<Fragment> fragments = manager.getFragments();
                    if (fragments != null && fragments.size() > 0) {
                        setLastUpdateTime(new Date());
                        return;
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        KEY_LAST_UPDATE_TIME += context.getClass().getName();
        mShared = context.getSharedPreferences("DefaultHeader", Context.MODE_PRIVATE);
        setLastUpdateTime(new Date(mShared.getLong(KEY_LAST_UPDATE_TIME, System.currentTimeMillis())));

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

    //<editor-fold desc="RefreshHeader">
    @Override
    public void onInitialized(IRefreshKernel kernel, int height, int extendHeight) {
        mRefreshKernel = kernel;
        mRefreshKernel.requestDrawBackgoundForHeader(mBackgroundColor);
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
        if (mProgressDrawable != null) {
            mProgressDrawable.start();
        } else {
            Drawable drawable = mProgressView.getDrawable();
            if (drawable instanceof Animatable) {
                ((Animatable) drawable).start();
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
        if (mProgressDrawable != null) {
            mProgressDrawable.stop();
        } else {
            Drawable drawable = mProgressView.getDrawable();
            if (drawable instanceof Animatable) {
                ((Animatable) drawable).stop();
            } else {
                mProgressView.animate().rotation(0).setDuration(300);
            }
        }
        mProgressView.setVisibility(GONE);
        if (success) {
            mTitleText.setText(REFRESH_HEADER_FINISH);
            if (mLastTime != null) {
                setLastUpdateTime(new Date());
            }
        } else {
            mTitleText.setText(REFRESH_HEADER_FAILED);
        }
        //延迟500毫秒之后再弹回
        return mFinishDuration;
    }

    @Override
    @Deprecated
    public void setPrimaryColors(@ColorInt int ... colors) {
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

    @Override
    @NonNull
    public View getView() {
        return this;
    }

    @Override
    public SpinnerStyle getSpinnerStyle() {
        return mSpinnerStyle;
    }

    @Override
    public void onStateChanged(IRefreshLayout refreshLayout, RefreshState oldState, RefreshState newState) {
        switch (newState) {
            case None:
                mLastUpdateText.setVisibility(mEnableLastTime ? VISIBLE : GONE);
            case PullDownToRefresh:
                mTitleText.setText(REFRESH_HEADER_PULLDOWN);
                mArrowView.setVisibility(VISIBLE);
                mProgressView.setVisibility(GONE);
                mArrowView.animate().rotation(0);
                break;
            case Refreshing:
            case RefreshReleased:
                mTitleText.setText(REFRESH_HEADER_REFRESHING);
                mProgressView.setVisibility(VISIBLE);
                mArrowView.setVisibility(GONE);
                break;
            case ReleaseToRefresh:
                mTitleText.setText(REFRESH_HEADER_RELEASE);
                mArrowView.animate().rotation(180);
                break;
            case ReleaseToTwoLevel:
                mTitleText.setText(REFRESH_HEADER_SECOND_FLOOR);
                mArrowView.animate().rotation(0);
                break;
            case Loading:
                mArrowView.setVisibility(GONE);
                mProgressView.setVisibility(GONE);
                mLastUpdateText.setVisibility(GONE);
                mTitleText.setText(REFRESH_HEADER_LOADING);
                break;
            default:break;    
        }
    }
    //</editor-fold>

    //<editor-fold desc="API">
    public DefaultHeader setProgressBitmap(Bitmap bitmap) {
        mProgressDrawable = null;
        mProgressView.setImageBitmap(bitmap);
        return this;
    }
    public DefaultHeader setProgressDrawable(Drawable drawable) {
        mProgressDrawable = null;
        mProgressView.setImageDrawable(drawable);
        return this;
    }
    public DefaultHeader setProgressResource(@DrawableRes int resId) {
        mProgressDrawable = null;
        mProgressView.setImageResource(resId);
        return this;
    }
    public DefaultHeader setArrowBitmap(Bitmap bitmap) {
        mArrowDrawable = null;
        mArrowView.setImageBitmap(bitmap);
        return this;
    }
    public DefaultHeader setArrowDrawable(Drawable drawable) {
        mArrowDrawable = null;
        mArrowView.setImageDrawable(drawable);
        return this;
    }
    public DefaultHeader setArrowResource(@DrawableRes int resId) {
        mArrowDrawable = null;
        mArrowView.setImageResource(resId);
        return this;
    }

    public DefaultHeader setLastUpdateTime(Date time) {
        mLastTime = time;
        mLastUpdateText.setText(mFormat.format(time));
        if (mShared != null && !isInEditMode()) {
            mShared.edit().putLong(KEY_LAST_UPDATE_TIME, time.getTime()).apply();
        }
        return this;
    }

    public DefaultHeader setLastUpdateText(CharSequence text) {
        mLastTime = null;
        mLastUpdateText.setText(text);
        return this;
    }

    public DefaultHeader setTimeFormat(DateFormat format) {
        mFormat = format;
        if (mLastTime != null) {
            mLastUpdateText.setText(mFormat.format(mLastTime));
        }
        return this;
    }

    public DefaultHeader setSpinnerStyle(SpinnerStyle style) {
        this.mSpinnerStyle = style;
        return this;
    }

    public DefaultHeader setPrimaryColor(@ColorInt int primaryColor) {
        setBackgroundColor(mBackgroundColor = primaryColor);
        if (mRefreshKernel != null) {
            mRefreshKernel.requestDrawBackgoundForHeader(mBackgroundColor);
        }
        return this;
    }

    public DefaultHeader setAccentColor(@ColorInt int accentColor) {
        if (mArrowDrawable != null) {
            mArrowDrawable.parserColors(accentColor);
        }
        if (mProgressDrawable != null) {
            mProgressDrawable.setColor(accentColor);
        }
        mTitleText.setTextColor(accentColor);
        mLastUpdateText.setTextColor(accentColor&0x00ffffff|0xcc000000);
        return this;
    }

    public DefaultHeader setPrimaryColorId(@ColorRes int colorId) {
        setPrimaryColor(ContextCompat.getColor(getContext(), colorId));
        return this;
    }

    public DefaultHeader setAccentColorId(@ColorRes int colorId) {
        setAccentColor(ContextCompat.getColor(getContext(), colorId));
        return this;
    }

    public DefaultHeader setFinishDuration(int delay) {
        mFinishDuration = delay;
        return this;
    }

    public DefaultHeader setEnableLastTime(boolean enable) {
        mEnableLastTime = enable;
        mLastUpdateText.setVisibility(enable ? VISIBLE : GONE);
        if (mRefreshKernel != null) {
            mRefreshKernel.requestRemeasureHeightForHeader();
        }
        return this;
    }

    public DefaultHeader setTextSizeTitle(float size) {
        mTitleText.setTextSize(size);
        if (mRefreshKernel != null) {
            mRefreshKernel.requestRemeasureHeightForHeader();
        }
        return this;
    }

    public DefaultHeader setTextSizeTitle(int unit, float size) {
        mTitleText.setTextSize(unit, size);
        if (mRefreshKernel != null) {
            mRefreshKernel.requestRemeasureHeightForHeader();
        }
        return this;
    }

    public DefaultHeader setTextSizeTime(float size) {
        mLastUpdateText.setTextSize(size);
        if (mRefreshKernel != null) {
            mRefreshKernel.requestRemeasureHeightForHeader();
        }
        return this;
    }

    public DefaultHeader setTextSizeTime(int unit, float size) {
        mLastUpdateText.setTextSize(unit, size);
        if (mRefreshKernel != null) {
            mRefreshKernel.requestRemeasureHeightForHeader();
        }
        return this;
    }

    public DefaultHeader setTextTimeMarginTop(float dp) {
        return setTextTimeMarginTopPx(DensityUtil.dp2px(dp));
    }

    public DefaultHeader setTextTimeMarginTopPx(int px) {
        MarginLayoutParams lp = (MarginLayoutParams)mLastUpdateText.getLayoutParams();
        lp.topMargin = px;
        mLastUpdateText.setLayoutParams(lp);
        return this;
    }

    public DefaultHeader setDrawableMarginRight(float dp) {
        return setDrawableMarginRightPx(DensityUtil.dp2px(dp));
    }

    public DefaultHeader setDrawableMarginRightPx(int px) {
        MarginLayoutParams lpArrow = (MarginLayoutParams)mArrowView.getLayoutParams();
        MarginLayoutParams lpProgress = (MarginLayoutParams)mProgressView.getLayoutParams();
        lpArrow.rightMargin = lpProgress.rightMargin = px;
        mArrowView.setLayoutParams(lpArrow);
        mProgressView.setLayoutParams(lpProgress);
        return this;
    }

    public DefaultHeader setDrawableSize(float dp) {
        return setDrawableSizePx(DensityUtil.dp2px(dp));
    }

    public DefaultHeader setDrawableSizePx(int px) {
        ViewGroup.LayoutParams lpArrow = mArrowView.getLayoutParams();
        ViewGroup.LayoutParams lpProgress = mProgressView.getLayoutParams();
        lpArrow.width = lpProgress.width = px;
        lpArrow.height = lpProgress.height = px;
        mArrowView.setLayoutParams(lpArrow);
        mProgressView.setLayoutParams(lpProgress);
        return this;
    }

    public DefaultHeader setDrawableArrowSize(float dp) {
        return setDrawableArrowSizePx(DensityUtil.dp2px(dp));
    }

    public DefaultHeader setDrawableArrowSizePx(int px) {
        ViewGroup.LayoutParams lpArrow = mArrowView.getLayoutParams();
        lpArrow.width = px;
        lpArrow.height = px;
        mArrowView.setLayoutParams(lpArrow);
        return this;
    }

    public DefaultHeader setDrawableProgressSize(float dp) {
        return setDrawableProgressSizePx(DensityUtil.dp2px(dp));
    }

    public DefaultHeader setDrawableProgressSizePx(int px) {
        ViewGroup.LayoutParams lpProgress = mProgressView.getLayoutParams();
        lpProgress.width = px;
        lpProgress.height = px;
        mProgressView.setLayoutParams(lpProgress);
        return this;
    }

    public ImageView getArrowView() {
        return mArrowView;
    }

    public ImageView getProgressView() {
        return mProgressView;
    }

    public TextView getTitleText() {
        return mTitleText;
    }

    public TextView getLastUpdateText() {
        return mLastUpdateText;
    }

    //</editor-fold>

}
