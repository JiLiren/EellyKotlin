package com.eelly.core.net

import android.text.TextUtils
import com.google.gson.JsonElement

/**
 * @author vurtne on 2-Mar-18.
 */
class XResponse<T> {

    private var mData: T? = null

    private var mError: String? = null

    private var netState: Int = 0

    private val mJsonElement: JsonElement? = null

    /**
     * 缓存响应标示
     */
    private var mIsCache: Boolean = false

    /**
     * 刷新响应标示
     */
    private var mIsRefresh: Boolean = false

    private var shouldReturn = true
    private var statusCode = -1

    fun isShouldReturn(): Boolean {
        return shouldReturn
    }

    fun setShouldReturn(shouldReturn: Boolean) {
        this.shouldReturn = shouldReturn
    }

    /**
     * 设置返回数据
     *
     * @param t
     */
    fun setResponse(t: T?) {
        mData = t
    }

    /**
     * 获取对象数据
     *
     * @return
     */
    fun get(): T? {
        return mData
    }

    /**
     * 获取JsonElement
     *
     * @return
     */
    fun getJson(): JsonElement? {
        return mJsonElement
    }

    /**
     * 设置缓存标示
     */
    fun setIsCache(isCache: Boolean) {
        mIsCache = isCache
    }

    /**
     * 当前的响应是否来自缓存
     *
     * @return
     */
    fun isCache(): Boolean {
        return mIsCache
    }

    /**
     * 设置缓存标示
     */
    internal fun setIsRefresh(isRefresh: Boolean) {
        mIsRefresh = isRefresh
    }

    /**
     * 当前的响应是否来自缓存刷新请求
     *
     * @return
     */
    fun isRefresh(): Boolean {
        return mIsRefresh
    }

    /**
     * 是否出现请求错误
     *
     * @return
     */
    fun hasError(): Boolean {
        return !TextUtils.isEmpty(mError)
    }


    fun getStatusCode(): Int {
        return statusCode
    }

    /**
     * @param statusCode2
     */
    fun setStatusCode(statusCode2: Int) {
        this.statusCode = statusCode2

    }

    fun getNetState(): Int {
        return netState
    }

    fun setNetState(netState: Int) {
        this.netState = netState
    }

}