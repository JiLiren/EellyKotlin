package com.eelly.core.util

import android.util.Log
import java.lang.Exception

/**
 * @author Vurtne on 19-Nov-17.
 */
object LogUtil {
    private val TAG = "LogUtil"
    /**
     * 日志开关
     */
    private var LOG_ENABLED = true

    /**
     * 日志级别
     */
    private val LOG_DEGREE = Log.VERBOSE

    private val LOG_FILE_DEGREE = Log.WARN

    /**
     * 打开或关闭日志
     * @param flag
     */
    fun enable(flag: Boolean) {
        LOG_ENABLED = flag
    }

    fun v(tag: String, msg: String) {
        val logDegree = Log.VERBOSE
        if (LOG_ENABLED && LOG_DEGREE <= logDegree) {
            Log.v(tag, msg)
        }
    }

    fun d(tag: String, msg: String) {
        var msg = msg
        if (LOG_ENABLED && LOG_DEGREE <= Log.DEBUG) {
            val p = 2048
            val length = msg.length.toLong()
            if (length < p || length == p.toLong())
                Log.d(tag, msg)
            else {
                while (msg.length > p) {
                    val logContent = msg.substring(0, p)
                    msg = msg.replace(logContent, "")
                    Log.d(tag, logContent)
                }
                Log.d(tag, msg)
            }
        }
    }

    fun i(tag: String, msg: String) {
        if (LOG_ENABLED && LOG_DEGREE <= Log.INFO) {
            Log.i(tag, msg)
        }
    }

    fun w(tag: String, msg: String?) {
        if (msg != null) {
            if (LOG_ENABLED && LOG_DEGREE <= Log.WARN) {
                Log.w(tag, msg)
            }
        }
    }

    fun w(tag: String, msg: String, e: Exception) {
        if (LOG_ENABLED && LOG_DEGREE <= Log.WARN) {
            Log.w(tag, msg, e)
        }
    }

    fun e(tag: String, msg: String) {
        if (LOG_ENABLED && LOG_DEGREE <= Log.ERROR) {
            Log.e(tag, msg)
        }
    }

    fun e(tag: String, e: Exception) {
        if (LOG_ENABLED && LOG_DEGREE <= Log.ERROR) {
            Log.e(tag, Log.getStackTraceString(e))
        }
    }

    fun e(tag: String, e: Error) {
        if (LOG_ENABLED && LOG_DEGREE <= Log.ERROR) {
            Log.e(tag, Log.getStackTraceString(e))
        }
    }

    fun e(tag: String, tr: Throwable, msg: String) {
        if (LOG_ENABLED && LOG_DEGREE <= Log.ERROR) {
            Log.e(tag, msg, tr)
        }
    }

}