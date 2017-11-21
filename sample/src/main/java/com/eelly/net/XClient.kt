package com.eelly.net

import com.eelly.manage.AccountManager
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * @author Vurtne on 21-Nov-17.
 */
class XClient {

    var retrofit: Retrofit? = null

    fun retrofit(): Retrofit? {
        if (retrofit == null) {
            val builder = OkHttpClient.Builder()
            val addQueryParameterInterceptor = Interceptor { chain ->
                val originalRequest = chain.request()
                val request: Request
                val modifiedUrl = originalRequest.url().newBuilder()
                        .addQueryParameter("token", AccountManager().getToken())
                        .build()
                request = originalRequest.newBuilder().url(modifiedUrl).build()
                chain.proceed(request)
            }
            builder.addInterceptor(addQueryParameterInterceptor)
            builder.connectTimeout(15, TimeUnit.SECONDS)
            builder.readTimeout(20, TimeUnit.SECONDS)
            builder.writeTimeout(20, TimeUnit.SECONDS)
            builder.retryOnConnectionFailure(true)
            val okHttpClient = builder.build()
            retrofit = Retrofit.Builder()
                    .baseUrl(NetConstants.API_SERVER_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(okHttpClient)
                    .build()
        }
        return retrofit
    }
}