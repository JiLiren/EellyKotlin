package com.eelly.core.util

import android.content.Context
import android.content.res.ColorStateList
import android.os.Build
import android.support.annotation.ColorRes

/**
 * @author Vurtne on 19-Nov-17.
 */
object ColorUtil {

    fun getColor(context: Context, @ColorRes color: Int): Int {

        val returnColor: Int

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            returnColor = context.resources.getColor(color, null)
        } else {
            returnColor = context.resources.getColor(color)
        }
        return returnColor
    }

    fun getColorStateList(context: Context, @ColorRes resId: Int): ColorStateList {

        val colorStateList: ColorStateList

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            colorStateList = context.resources.getColorStateList(resId, null)
        } else {
            colorStateList = context.resources.getColorStateList(resId)

        }
        return colorStateList
    }


    /**
     * 判断颜色的深浅
     * @return TRUE 浅色
     * *
     */
    fun colorDepth(color: Int): Boolean {
        val R = getColorR(color)
        val G = getColorG(color)
        val B = getColorB(color)
        val grayLevel = R * 0.299 + G * 0.587 + B * 0.114
        return grayLevel >= 192
    }

    /**
     * 获取颜色的R值
     * @return R值
     * *
     */
    fun getColorR(color: Int): Int {
        return color and 0xff0000 shr 16
    }

    /**
     * 获取颜色的G值
     * @return G值
     * *
     */
    fun getColorG(color: Int): Int {
        return color and 0x00ff00 shr 8
    }

    /**
     * 获取颜色的B值
     * @return B值
     * *
     */
    fun getColorB(color: Int): Int {
        return color and 0x0000ff
    }
}