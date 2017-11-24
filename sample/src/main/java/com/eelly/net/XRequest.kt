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

    @GET("in_theaters")
    fun onRequestMoviesMore(@Query("start") page: Int,@Query("count") count:String): Observable<TheaterBean>


}