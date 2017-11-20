package com.eelly.core.util

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Rect
import android.support.annotation.ColorInt

/**
 * @author Vurtne on 19-Nov-17.
 */
object DeviceUtil {


    /**
     * 根据手机的分辨率从 dip 的单位 转成为px
     * @param context 上下文
     * @param dpValue 值
     * @return
     */
    fun dip2Px(context: Context, dpValue: Float): Int {
        return (dpValue * context.resources.displayMetrics.density + 0.5f).toInt()
    }

    /**
     * 计算颜色 和 透明度

     * @param color 颜色
     * *
     * @param alpha 透明度
     */
    fun calculateColor(@ColorInt color: Int, alpha: Int): Int {
        if (alpha == 0) {
            return color
        }
        val al = 1 - alpha / 255f
        var red = color shr 16 and 0xff
        var green = color shr 8 and 0xff
        var blue = color and 0xff
        red = (red * al + 0.5).toInt()
        green = (green * al + 0.5).toInt()
        blue = (blue * al + 0.5).toInt()
        return 0xff shl 24 or (red shl 16) or (green shl 8) or blue
    }

    /**
     * 获取状态栏的高度
     * @param context
     * @return
     */
    fun getStatusBarHeight(context: Context): Int {
        var statusHeight = 0
        val localRect = Rect()
        (context as Activity).window.decorView.getWindowVisibleDisplayFrame(localRect)
        statusHeight = localRect.top
        if (0 == statusHeight) {
            val localClass: Class<*>
            try {
                localClass = Class.forName("com.android.internal.R\$dimen")
                val localObject = localClass.newInstance()
                val i5 = Integer.parseInt(localClass.getField("status_bar_height").get(localObject).toString())
                statusHeight = context.getResources().getDimensionPixelSize(i5)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
        return statusHeight
    }

    /**
     * 获取版本名称
     * @param context 上下文
     * @return 版本名称
     */
    fun getVersionName(context: Context): String {
        var versionName = ""
        try {
            val packageManager = context.packageManager
            val packInfo = packageManager.getPackageInfo(context.packageName, 0)
            versionName = packInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        return versionName
    }


}
