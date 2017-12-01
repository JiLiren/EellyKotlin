package com.eelly.net

import com.eelly.bean.TheaterBean
import com.google.gson.JsonElement
import io.reactivex.Observable
import retrofit2.http.*


/**
 * @author Vurtne on 21-Nov-17.
 */
interface XRequest {

    @GET("in_theaters")
    fun onRequestMoviesList(): Observable<TheaterBean>

    @GET("in_theaters")
    fun onRequestMoviesMore(@Query("start") page: Int,@Query("count") count:String): Observable<TheaterBean>


    @POST
    fun post(@Url path: String, @Body dataJson: String): Observable<JsonElement>
}