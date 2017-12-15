package com.eelly.core.widget.refresh.tools

import android.view.animation.Interpolator
/**
 * @author Vurtne on 14-Dec-17.
 */
class ViscousFluidInterpolator :Interpolator{

    companion object {
        /** Controls the viscous fluid effect (how much of it).  */
        private val VISCOUS_FLUID_SCALE = 8.0f

        var VISCOUS_FLUID_NORMALIZE = 1.0f / viscousFluid(1.0f)
        // account for very small floating-point error
        var VISCOUS_FLUID_OFFSET = 1.0f - VISCOUS_FLUID_NORMALIZE * viscousFluid(1.0f)

        private fun viscousFluid(x: Float): Float {
            var x = x
            x *= VISCOUS_FLUID_SCALE
            if (x < 1.0f) {
                x -= 1.0f - Math.exp((-x).toDouble()).toFloat()
            } else {
                val start = 0.36787944117f
                // 1/e == exp(-1)
                x = 1.0f - Math.exp((1.0f - x).toDouble()).toFloat()
                x = start + x * (1.0f - start)
            }
            return x
        }
    }

    override fun getInterpolation(input: Float): Float {
        val interpolated = VISCOUS_FLUID_NORMALIZE * viscousFluid(input)
        return if (interpolated > 0) {
            interpolated + VISCOUS_FLUID_OFFSET
        } else interpolated
    }
}