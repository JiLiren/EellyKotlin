package com.eelly.core.widget.banner

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.support.annotation.ColorInt
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.View

/**
 * @author vurtne on 3-Jan-18.
 */
class ViewPagerIndicator :View{

    private var mPositionOffset = 0f
    private var mPosition: Int = 0
    private var mItemCount: Int = 0
    private var mItemWidth: Int = 0
    private var mItemHeight: Int = 0
    private var mItemGap: Int = 0
    private var mItemDrawable: Drawable? = null
    private var mItemDrawableSelected: Drawable? = null

    private var mWidth = 0;

    constructor(context: Context):this(context,null)

    constructor(context: Context, attrs: AttributeSet?):this(context, attrs,0)

    constructor(context: Context,attrs: AttributeSet?,defStyleAttr: Int):super(context,attrs,defStyleAttr)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mWidth = if (mItemCount > 1) (mItemWidth + mItemGap) * mItemCount - mItemGap else
            if (mItemCount == 1) mItemWidth else 0
        setMeasuredDimension(mWidth, mItemHeight)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val flags = Canvas.MATRIX_SAVE_FLAG or Canvas.CLIP_SAVE_FLAG or Canvas.HAS_ALPHA_LAYER_SAVE_FLAG or Canvas.
                FULL_COLOR_LAYER_SAVE_FLAG or Canvas.CLIP_TO_LAYER_SAVE_FLAG
        val sc = canvas?.saveLayer(0f, 0f, width.toFloat(), height.toFloat(), null, flags)
        val wg = mItemWidth + mItemGap
        val x = (width - width) / 2
        val y = (height - mItemHeight) / 2
        mItemDrawable?.setBounds(0,0,mItemWidth,mItemHeight)
        mItemDrawableSelected?.setBounds(0, 0, mItemWidth, mItemHeight)

        for (i in 0 until mItemCount) {
            canvas?.save()
            canvas?.translate((x + i * wg).toFloat(), y.toFloat())
            mItemDrawable?.draw(canvas)
            canvas?.restore()
        }
        canvas?.save()
        canvas?.translate(x + (mPosition + mPositionOffset) * wg, y.toFloat())
        mItemDrawableSelected?.draw(canvas)
        canvas?.restore()
        canvas?.restoreToCount(sc!!)
    }

    fun setupWithViewPager(pager: ViewPager) {
        mItemCount = pager.adapter!!.count
        pager.removeOnPageChangeListener(onPageChangeListener)
        pager.addOnPageChangeListener(onPageChangeListener)
        requestLayout()
    }

    fun setPosition(position: Int) {
        mPosition = position
        invalidate()
    }


    fun setItemSize(width: Int, height: Int): ViewPagerIndicator {
        this.mItemWidth = width
        this.mItemHeight = height
        return this
    }

    fun setItemGap(gap: Int): ViewPagerIndicator {
        this.mItemGap = gap
        return this
    }

    fun setItemColor(@ColorInt color: Int, @ColorInt selected: Int): ViewPagerIndicator {
        this.mItemDrawable = genDrawable(color)
        this.mItemDrawableSelected = genDrawable(selected)
        return this
    }

    fun setItemDrawable(normal: Drawable, selected: Drawable): ViewPagerIndicator {
        this.mItemDrawable = normal
        this.mItemDrawableSelected = selected
        return this
    }

    internal fun genDrawable(color: Int): Drawable {
        val drawable = ShapeDrawable(OvalShape())
        drawable.paint.color = color
        drawable.paint.isAntiAlias = true
        return drawable
    }

    internal var onPageChangeListener: ViewPager.OnPageChangeListener = object : ViewPager.OnPageChangeListener {
        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            mPosition = position
            mPositionOffset = positionOffset
            invalidate()
        }

        override fun onPageSelected(position: Int) {
            mPosition = position
            mPositionOffset = 0f
            invalidate()
        }

        override fun onPageScrollStateChanged(state: Int) {

        }
    }
}