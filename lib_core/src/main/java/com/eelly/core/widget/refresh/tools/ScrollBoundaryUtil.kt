package com.eelly.core.widget.refresh.tools

import android.graphics.PointF
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView

/**
 * @author Vurtne on 14-Dec-17.
 */
class ScrollBoundaryUtil {

    //<editor-fold desc="滚动判断">
    fun canRefresh(targetView: View, event: MotionEvent?): Boolean {
        var event = event
        if (canScrollUp(targetView) && targetView.visibility == View.VISIBLE) {
            return false
        }
        if (targetView is ViewGroup && event != null) {
            val childCount = targetView.childCount
            val point = PointF()
            for (i in childCount downTo 1) {
                val child = targetView.getChildAt(i - 1)
                if (isTransformedTouchPointInView(targetView, child, event!!.x, event.y, point)) {
                    event = MotionEvent.obtain(event)
                    event!!.offsetLocation(point.x, point.y)
                    return canRefresh(child, event)
                }
            }
        }
        return true
    }

    fun canLoadmore(targetView: View, event: MotionEvent?): Boolean {
        var event = event
        if (!canScrollDown(targetView) && canScrollUp(targetView) && targetView.visibility == View.VISIBLE) {
            return true
        }
        if (targetView is ViewGroup && event != null) {
            val childCount = targetView.childCount
            val point = PointF()
            for (i in 0 until childCount) {
                val child = targetView.getChildAt(i)
                if (isTransformedTouchPointInView(targetView, child, event!!.x, event.y, point)) {
                    event = MotionEvent.obtain(event)
                    event!!.offsetLocation(point.x, point.y)
                    return canLoadmore(child, event)
                }
            }
        }
        return false
    }

    fun canScrollDown(targetView: View, event: MotionEvent?): Boolean {
        var event = event
        if (canScrollDown(targetView) && targetView.visibility == View.VISIBLE) {
            return true
        }
        if (targetView is ViewGroup && event != null) {
            val childCount = targetView.childCount
            val point = PointF()
            for (i in 0 until childCount) {
                val child = targetView.getChildAt(i)
                if (isTransformedTouchPointInView(targetView, child, event!!.x, event.y, point)) {
                    event = MotionEvent.obtain(event)
                    event!!.offsetLocation(point.x, point.y)
                    return canScrollDown(child, event)
                }
            }
        }
        return false
    }

    fun canScrollUp(targetView: View): Boolean {
        return if (android.os.Build.VERSION.SDK_INT < 14) {
            if (targetView is AbsListView) {
                targetView.childCount > 0 && (targetView.firstVisiblePosition > 0 || targetView.getChildAt(0)
                        .top < targetView.paddingTop)
            } else {
                targetView.scrollY > 0
            }
        } else {
            targetView.canScrollVertically(-1)
        }
    }

    fun canScrollDown(targetView: View): Boolean {
        return if (android.os.Build.VERSION.SDK_INT < 14) {
            if (targetView is AbsListView) {
                targetView.childCount > 0 && (targetView.lastVisiblePosition < targetView.childCount - 1 || targetView.getChildAt(targetView.childCount - 1).bottom > targetView.paddingBottom)
            } else {
                targetView.scrollY < 0
            }
        } else {
            targetView.canScrollVertically(1)
        }
    }

    //</editor-fold>

    //<editor-fold desc="transform Point">

    fun isTransformedTouchPointInView(group: ViewGroup, child: View, x: Float, y: Float, outLocalPoint: PointF?): Boolean {
        if (child.visibility != View.VISIBLE) {
            return false
        }
        val point = FloatArray(2)
        point[0] = x
        point[1] = y
        transformPointToViewLocal(group, child, point)
        val isInView = pointInView(child, point[0], point[1], 0f)
        if (isInView && outLocalPoint != null) {
            outLocalPoint.set(point[0] - x, point[1] - y)
        }
        return isInView
    }

    fun pointInView(view: View, localX: Float, localY: Float, slop: Float): Boolean {
        val left = /*Math.max(view.getPaddingLeft(), 0)*/ -slop
        val top = /*Math.max(view.getPaddingTop(), 0)*/ -slop
        val width = view.width.toFloat()/* - Math.max(view.getPaddingLeft(), 0) - Math.max(view.getPaddingRight(), 0)*/
        val height = view.height.toFloat()/* - Math.max(view.getPaddingTop(), 0) - Math.max(view.getPaddingBottom(), 0)*/
        return localX >= left && localY >= top && localX < width + slop &&
                localY < height + slop
    }

    fun transformPointToViewLocal(group: ViewGroup, child: View, point: FloatArray) {
        point[0] += (group.scrollX - child.left).toFloat()
        point[1] += (group.scrollY - child.top).toFloat()
    }
    //</editor-fold>
}