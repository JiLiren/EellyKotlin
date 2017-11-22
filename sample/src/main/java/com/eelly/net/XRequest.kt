package com.eelly.net

import com.eelly.bean.TheaterBean
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query


/**
 * @author Vurtne on 21-Nov-17.
 */
interface XRequest {

    @GET("in_theaters")
    fun onRequestMoviesList(): Observable<TheaterBean>

    @GET("client/getRecordListBySubmitUserId")
    abstract fun getRecordListBySubmitUserId(
            @Query("page") page: String): Observable<TheaterBean> //信息的记录列表

}