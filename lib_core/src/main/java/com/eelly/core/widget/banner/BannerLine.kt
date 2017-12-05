package com.eelly.core.widget.banner

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View
import com.eelly.core.R

/**
 * @author Vurtne on 5-Dec-17.
 */
class BannerLine(context: Context,attrs: AttributeSet) : View(context,attrs) {




    private var mPositionOffset = 0F
    private var mPageSize = 0
    private var mPosition = 0
    private val mWidth:Float
    private var mPageWidth = 0f
    private var mLineColor = ContextCompat.getColor(getContext(), R.color.material_orange_700)

    private val mPaint: Paint by lazy{
        Paint().apply {
            color = mLineColor
            strokeWidth = 1000f
            isAntiAlias = true
        }
    }

    init {
        val dm = resources.displayMetrics
        mWidth = dm.widthPixels.toFloat()
    }


    override fun onDraw(canvas: Canvas) {
        if (mPosition == 0) {
            canvas.drawLine((mPageSize - 3) * mPageWidth + mPageWidth * mPositionOffset, 0F,
                    (mPageSize - 2) * mPageWidth + mPageWidth * mPositionOffset, 0F, mPaint)
            canvas.drawLine(0F, 0F, mPageWidth * mPositionOffset, 0F, mPaint)
        } else if (mPosition == mPageSize - 2) {
            canvas.drawLine((mPosition - 1) * mPageWidth + mPageWidth * mPositionOffset, 0F,
                    mPosition * mPageWidth + mPageWidth * mPositionOffset, 0F, mPaint)
            canvas.drawLine(0F, 0F, mPageWidth * mPositionOffset, 0F, mPaint)
        } else {
            canvas.drawLine((mPosition - 1) * mPageWidth + mPageWidth * mPositionOffset, 0F,
                    mPosition * mPageWidth + mPageWidth * mPositionOffset, 0F, mPaint)
        }

    }

    fun setPageWidth(pageSize:Int) {
        mPageSize = pageSize
        calcPageWidth()
    }

    fun calcPageWidth() {
        this.mPageWidth = this.mWidth / (this.mPageSize - 2)
    }

    fun setPageScrolled(position:Int,positionOffset:Float) {
        mPosition = position
        mPositionOffset = positionOffset
        invalidate()
    }

    fun setLineColor(lineColor: Int) {
        mLineColor = lineColor
        mPaint.color = mLineColor
    }
}