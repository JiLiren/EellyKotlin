package com.eelly.core.widget.refresh

import android.animation.ValueAnimator
import android.graphics.*
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.view.animation.LinearInterpolator

/**
 * @author Vurtne on 14-Dec-17.
 */
class ProgressDrawable : Drawable(), Animatable {

    private var mProgressDegree = 0
    private var mValueAnimator: ValueAnimator? = null
    private val mPath = Path()
    private val mPaint = Paint()

    init {
        mPaint.style = Paint.Style.FILL
        mPaint.isAntiAlias = true
        mPaint.color = -0x555556
        setupAnimators()
    }

    fun setColor(color: Int) {
        mPaint.color = color
    }

    //<editor-fold desc="Drawable">
    override fun draw(canvas: Canvas) {
        val bounds = bounds
        val width = bounds.width()
        val height = bounds.height()
        canvas.save()
        canvas.rotate(mProgressDegree.toFloat(), (width / 2).toFloat(), (height / 2).toFloat())
        val r = Math.max(1, width / 20)
        for (i in 0..11) {
            mPath.reset()
            mPath.addCircle((width - r).toFloat(), (height / 2).toFloat(), r.toFloat(), Path.Direction.CW)
            mPath.addRect((width - 5 * r).toFloat(), (height / 2 - r).toFloat(), (width - r).toFloat(), (height / 2 + r).toFloat(), Path.Direction.CW)
            mPath.addCircle((width - 5 * r).toFloat(), (height / 2).toFloat(), r.toFloat(), Path.Direction.CW)
            mPaint.alpha = (i + 5) * 0x11
            canvas.rotate(30f, (width / 2).toFloat(), (height / 2).toFloat())
            canvas.drawPath(mPath, mPaint)
        }
        canvas.restore()
    }

    override fun setAlpha(alpha: Int) {
        mPaint.alpha = alpha
    }

    override fun setColorFilter(cf: ColorFilter?) {
        mPaint.colorFilter = cf
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }
    //</editor-fold>

    private fun setupAnimators() {
        mValueAnimator = ValueAnimator.ofInt(30, 3600)
        mValueAnimator!!.addUpdateListener({
            animation ->
            val value = animation.animatedValue as Int
            mProgressDegree = 30 * (value / 30)
            invalidateSelf()
        })
        mValueAnimator!!.duration = 10000
        mValueAnimator!!.interpolator = LinearInterpolator()
        mValueAnimator!!.repeatCount = ValueAnimator.INFINITE
        mValueAnimator!!.repeatMode = ValueAnimator.RESTART
    }

    override fun start() {
        if (!mValueAnimator!!.isRunning) {
            mValueAnimator!!.start()
        }
    }

    override fun stop() {
        if (mValueAnimator!!.isRunning) {
            mValueAnimator!!.cancel()
        }
    }

    override fun isRunning(): Boolean {
        return mValueAnimator!!.isRunning
    }

    fun width(): Int {
        return bounds.width()
    }

    fun height(): Int {
        return bounds.height()
    }
}