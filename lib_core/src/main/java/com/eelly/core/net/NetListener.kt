package com.eelly.core.net

/**
 * Created by vurtne on 2018/3/2.
 */
interface NetListener<T>{

    /**
     * 执行请求回调
     * @param response
     */
    fun onResponse(response: XResponse<T>)


}