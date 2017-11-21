package com.eelly.net

import com.eelly.bean.MovieEntity
import io.reactivex.Observable

/**
 * @author Vurtne on 21-Nov-17.
 */
interface XRequest {

    @retrofit2.http.GET("in_theaters")
    fun onRequestMoviesList(): Observable<List<MovieEntity>>



}