package com.eelly.core.util

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.support.annotation.ColorInt
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.LinearLayout
import com.eelly.core.R

/**
 * @author Vurtne on 19-Nov-17.
 */
object StatusBarUtil {
    val DEFAULT_STATUS_BAR_ALPHA = 112
    private val FAKE_STATUS_BAR_VIEW_ID = R.id.statusbarutil_fake_status_bar_view
    private val FAKE_TRANSLUCENT_VIEW_ID = R.id.statusbarutil_translucent_view
    private val TAG_KEY_HAVE_SET_OFFSET = -123


    /**
     * 设置状态栏颜色
     * @param activity 需要设置的 activity
     * @param color    状态栏颜色值
     */
    fun setColor(activity: Activity, @ColorInt color: Int) {
        setColor(activity, color, DEFAULT_STATUS_BAR_ALPHA)
    }

    /**
     * 设置状态栏颜色
     * @param activity       需要设置的activity
     * @param color          状态栏颜色值
     * @param statusBarAlpha 状态栏透明度
     */
    fun setColor(activity: Activity, @ColorInt color: Int, statusBarAlpha: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            activity.window.statusBarColor = DeviceUtil.calculateColor(color, statusBarAlpha)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            val decorView = activity.window.decorView as ViewGroup
            val fakeStatusBarView = decorView.findViewById<View>(FAKE_STATUS_BAR_VIEW_ID)
            if (fakeStatusBarView != null) {
                if (fakeStatusBarView.visibility == View.GONE) {
                    fakeStatusBarView.visibility = View.VISIBLE
                }
                fakeStatusBarView.setBackgroundColor(DeviceUtil.calculateColor(color, statusBarAlpha))
            } else {
                decorView.addView(createStatusBarView(activity, color, statusBarAlpha))
            }
            setRootView(activity)
        }
    }

    /**
     * 设置根布局参数
     */
    private fun setRootView(activity: Activity) {
        val parent = activity.findViewById<ViewGroup>(android.R.id.content)
        var i = 0
        val count = parent.childCount
        while (i < count) {
            val childView = parent.getChildAt(i)
            if (childView is ViewGroup) {
                childView.setFitsSystemWindows(true)
                childView.clipToPadding = true
            }
            i++
        }
    }

    /**
     * 生成一个和状态栏大小相同的半透明矩形条
     * @param activity 需要设置的activity
     * @param color    状态栏颜色值
     * @param alpha    透明值
     * @return 状态栏矩形条
     */
    private fun createStatusBarView(activity: Activity, @ColorInt color: Int, alpha: Int): View {
        // 绘制一个和状态栏一样高的矩形
        val statusBarView = View(activity)
        val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DeviceUtil.getStatusBarHeight(activity))
        statusBarView.layoutParams = params
        statusBarView.setBackgroundColor(DeviceUtil.calculateColor(color, alpha))
        statusBarView.id = FAKE_STATUS_BAR_VIEW_ID
        return statusBarView
    }

    /**
     * 设置透明
     */
    private fun setTransparentForWindow(activity: Activity, dark: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.window.statusBarColor = Color.TRANSPARENT
            if (dark) {
                activity.window
                        .decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            } else {
                activity.window
                        .decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            activity.window
                    .setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                            WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }
    }

    /**
     * 添加半透明矩形条
     * @param activity       需要设置的 activity
     * @param statusBarAlpha 透明值
     */
    private fun addTranslucentView(activity: Activity, statusBarAlpha: Int) {
        val contentView = activity.findViewById<ViewGroup>(android.R.id.content)
        val fakeTranslucentView = contentView.findViewById<View>(FAKE_TRANSLUCENT_VIEW_ID)
        if (fakeTranslucentView != null) {
            if (fakeTranslucentView.visibility == View.GONE) {
                fakeTranslucentView.visibility = View.VISIBLE
            }
            fakeTranslucentView.setBackgroundColor(Color.argb(statusBarAlpha, 0, 0, 0))
        } else {
            contentView.addView(createTranslucentStatusBarView(activity, statusBarAlpha))
        }
    }

    /**
     * 创建半透明矩形 View
     * @param alpha 透明值
     * @return 半透明 View
     */
    private fun createTranslucentStatusBarView(activity: Activity, alpha: Int): View {
        // 绘制一个和状态栏一样高的矩形
        val statusBarView = View(activity)
        val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                DeviceUtil.getStatusBarHeight(activity))
        statusBarView.layoutParams = params
        statusBarView.setBackgroundColor(Color.argb(alpha, 0, 0, 0))
        statusBarView.id = FAKE_TRANSLUCENT_VIEW_ID
        return statusBarView
    }

    /**
     * 为头部是 ImageView 的界面设置状态栏透明(使用默认透明度)
     * @param activity       需要设置的activity
     * @param needOffsetView 需要向下偏移的 View
     */
    fun setTranslucentForImageView(activity: Activity, needOffsetView: View) {
        setTranslucentForImageView(activity, DEFAULT_STATUS_BAR_ALPHA, needOffsetView)
    }

    /**
     * 为头部是 ImageView 的界面设置状态栏透明
     * @param activity       需要设置的activity
     * @param statusBarAlpha 状态栏透明度
     * @param needOffsetView 需要向下偏移的 View
     */
    fun setTranslucentForImageView(activity: Activity, statusBarAlpha: Int, needOffsetView: View) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return
        }
        setTranslucentForImageView(activity, statusBarAlpha, needOffsetView, false)
    }

    /**
     * 为头部是 ImageView 的界面设置状态栏透明
     * @param activity       需要设置的activity
     * @param statusBarAlpha 状态栏透明度
     * @param needOffsetView 需要向下偏移的 View
     */
    fun setTranslucentForImageView(activity: Activity, statusBarAlpha: Int, needOffsetView: View?, dark: Boolean) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return
        }
        setTransparentForWindow(activity, dark)
        addTranslucentView(activity, statusBarAlpha)
        if (needOffsetView != null) {
            val haveSetOffset = needOffsetView.getTag(TAG_KEY_HAVE_SET_OFFSET)
            if (haveSetOffset != null && haveSetOffset as Boolean) {
                return
            }
            val layoutParams = needOffsetView.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.setMargins(layoutParams.leftMargin, layoutParams.topMargin + DeviceUtil.getStatusBarHeight(activity),
                    layoutParams.rightMargin, layoutParams.bottomMargin)
            needOffsetView.setTag(TAG_KEY_HAVE_SET_OFFSET, true)
        }
    }

    /**
     * 偏移View
     * @param activity       需要设置的activity
     * @param needOffsetView 需要向下偏移的 View
     */
    fun onTranslateView(activity: Activity, needOffsetView: View?) {
        if (needOffsetView != null) {
            val haveSetOffset = needOffsetView.getTag(TAG_KEY_HAVE_SET_OFFSET)
            if (haveSetOffset != null && haveSetOffset as Boolean) {
                return
            }
            val layoutParams = needOffsetView.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.setMargins(layoutParams.leftMargin, layoutParams.topMargin + DeviceUtil.getStatusBarHeight(activity),
                    layoutParams.rightMargin, layoutParams.bottomMargin)
            needOffsetView.setTag(TAG_KEY_HAVE_SET_OFFSET, true)
        }
    }


    /**
     * 修改状态栏的颜色 ， 需要6.0以上的系统， 魅族 和 小米 也可以
     * @param activity 需要设置的窗口
     * @param dark 是否把状态栏字体及图标颜色设置为深色
     */
    fun setStatusContentColor(activity: Activity, dark: Boolean): Boolean {
        if (ModelUtil.isFlyme()) {
            setFlymeStatusBarLightMode(activity.window, dark)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                activity.window
                        .decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            }
            return true
        } else if (ModelUtil.isMIUI()) {
            if (ModelUtil.isMIUI6()) {
                if (ModelUtil.isMIUI9()) {
                    if (dark) {
                        activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                    } else {
                        activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    }
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        activity.window
                                .decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    }
                    SetMiNiStatusBarLightMode(activity.window, dark)
                }
                return true
            }
            return false
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (dark) {
                activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else {
                activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            }

            return true
        }
        return false
    }

    /**
     * 是否可以修改状态栏的颜色 ， 需要6.0以上的系统， 魅族 和 小米 也可以
     */
    fun canStatusChangeColor(): Boolean {
        if (ModelUtil.isFlyme()) {
            return true
        } else if (ModelUtil.isMIUI()) {
            if (ModelUtil.isMIUI6()) {
                return true
            }
            return false
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return true
        }
        return false
    }


    /**
     * 设置状态栏图标为深色和魅族特定的文字风格
     * 可以用来判断是否为Flyme用户
     * @param window 需要设置的窗口
     * @param dark 是否把状态栏字体及图标颜色设置为深色
     * @return  boolean 成功执行返回true
     */
    fun setFlymeStatusBarLightMode(window: Window?, dark: Boolean) {
        if (window != null) {
            try {
                val lp = window.attributes
                val darkFlag = WindowManager.LayoutParams::class.java
                        .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON")
                val meizuFlags = WindowManager.LayoutParams::class.java
                        .getDeclaredField("meizuFlags")
                darkFlag.isAccessible = true
                meizuFlags.isAccessible = true
                val bit = darkFlag.getInt(null)
                var value = meizuFlags.getInt(lp)
                if (dark) {
                    value = value or bit
                } else {
                    value = value and bit.inv()
                }
                meizuFlags.setInt(lp, value)
                window.attributes = lp
            } catch (e: Exception) {
                LogUtil.e("StatusColorException", e.toString())
            }

        }
    }

    /**
     * 设置状态栏字体图标为深色，需要MIUIV6以上
     * @param window 需要设置的窗口
     * @param dark 是否把状态栏字体及图标颜色设置为深色
     */
    fun SetMiNiStatusBarLightMode(window: Window?, dark: Boolean) {
        if (window != null) {
            val clazz = window.javaClass
            try {
                var darkModeFlag = 0
                val layoutParams = Class.forName("android.view.MiuiWindowManager\$LayoutParams")
                val field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE")
                darkModeFlag = field.getInt(layoutParams)
                val extraFlagField = clazz.getMethod("setExtraFlags", Int::class.javaPrimitiveType, Int::class.javaPrimitiveType)
                extraFlagField.invoke(window, if (dark) darkModeFlag else 0, darkModeFlag)
            } catch (e: Exception) {
                LogUtil.e("StatusColorException", e.toString())
            }

        }
    }

}