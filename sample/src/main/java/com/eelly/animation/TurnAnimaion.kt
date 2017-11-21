package com.eelly.animation

import android.graphics.Camera
import android.graphics.Matrix
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.Transformation

/**
 * @author Vurtne on 21-Nov-17.
 *
 */
class TurnAnimaion(var orientation: Boolean,var dur:Long) : Animation() {

    var mCenterX:Int = 0
    var mCenterY:Int = 0
    val camera : Camera = Camera()

    override fun initialize(width:Int,height:Int,parentWidth:Int,parentHeight:Int) {
        super.initialize(width, height, parentWidth, parentHeight)
        mCenterX = width/2
        mCenterY = height/2
        duration = dur
        fillAfter = true
        interpolator = LinearInterpolator()
    }

    override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
        super.applyTransformation(interpolatedTime, t)
        camera.save()
        val  matrix : Matrix = t!!.matrix
        if(interpolatedTime>=0.5){
            camera.rotateY((70 + (interpolatedTime-0.5)*90*2).toFloat())
        }
        else{
            camera.rotateY(270.toFloat());
        }
        camera.getMatrix(matrix);
        camera.restore()
        matrix.preTranslate(-mCenterX.toFloat(), -mCenterY.toFloat())
        matrix.postTranslate(mCenterX.toFloat(), mCenterY.toFloat())
    }


}