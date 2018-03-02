package com.eelly.core.net

import com.google.gson.JsonElement
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import retrofit2.Response

/**
 * @author Vurtne on 21-Nov-17.
 */
open class XNetty<T> {

     /**
     * 获取请求对象
     * */
    fun getRequest(): XRequest = XClient().retrofit()!!.create(XRequest::class.java)

    /**
     * 请求Observable<T> observable
     */
    fun onRequest(compositeDisposable: CompositeDisposable, observable: Observable<Response<JsonElement>>,
                  errorConsumer : Consumer<Throwable>,listener: NetListener<T>,clazz: Class<T>) {
        compositeDisposable.add(onParseMap(observable.subscribeOn(Schedulers.io()),clazz)
                .observeOn(AndroidSchedulers.mainThread()).subscribe(createConsumer(listener),errorConsumer))
    }


    private fun createConsumer(listener: NetListener<T>):Consumer<XResponse<T>>{
        return Consumer<XResponse<T>> { response ->
            listener.onResponse(response)
        }
    }

    private fun onParseMap(observable: Observable<Response<JsonElement>>,clazz: Class<T>): Observable<XResponse<T>> {
        return observable.map<XResponse<T>>(Function<Response<JsonElement>, XResponse<T>> { response ->
            val apiResponse = XResponse<T>()
            try {
                onResponse(apiResponse, response,clazz)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            apiResponse
        })
    }

    /**
     * 在这进行数据的解析,包括缓存数据或者服务端返回的数据
     */
    @Throws(Exception::class)
    private fun onResponse(apiResponse: XResponse<T>, response: Response<JsonElement>, clazz: Class<T>): XResponse<T> {
        var bean: T? = null
        bean = JsonFormat.parseJsonElement(response.body()!!, clazz, "")
        apiResponse.setResponse(bean)
        return apiResponse
    }


}