package com.eelly.core.animation

import android.view.animation.Animation
import android.view.animation.Transformation

/**
 * @author Vurtne on 5-Dec-17.
 */
class CustomTranslateAnimation(type: Int) : Animation() {

    var mFromXDelta: Float = 0.toFloat()
    var mToXDelta: Float = 0.toFloat()
    var mFromYDelta: Float = 0.toFloat()
    var mToYDelta: Float = 0.toFloat()
    var mActionType: Float = 0.toFloat()

    init {
        mActionType = type.toFloat()
        this.fillAfter = true
    }

    override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
        var dx = mFromXDelta
        var dy = mFromYDelta
        if (mFromXDelta != mToXDelta) {
            dx = mFromXDelta + (mToXDelta - mFromXDelta) * interpolatedTime
        }
        if (mFromYDelta != mToYDelta) {
            dy = mFromYDelta + (mToYDelta - mFromYDelta) * interpolatedTime
        }
        t.matrix.setTranslate(dx, dy)
    }

    override fun initialize(width: Int, height: Int, parentWidth: Int, parentHeight: Int) {
        super.initialize(width, height, parentWidth, parentHeight)
        if (mActionType == Translate.PARTONE.toFloat()) {
            mFromXDelta = (-width).toFloat()
            mToXDelta = (parentWidth / 2 - width / 2).toFloat()
            mFromYDelta = 0f
            mToYDelta = 0f
        }

        if (mActionType == Translate.PARTTWO.toFloat()) {
            mFromXDelta = (parentWidth / 2 - width / 2).toFloat()
            mToXDelta = (parentWidth / 2 - width / 2).toFloat()
            mFromYDelta = 0f
            mToYDelta = 30f
        }

        if (mActionType == Translate.PARTTHREE.toFloat()) {
            mFromXDelta = (parentWidth / 2 - width / 2).toFloat()
            mToXDelta = (parentWidth - width / 2).toFloat()
            mFromYDelta = 0f
            mToYDelta = 0f
        }
    }


}