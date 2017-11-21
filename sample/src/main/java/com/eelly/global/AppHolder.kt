package com.eelly.global

import com.eelly.net.XClient
import com.eelly.net.XRequest
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers

/**
 * @author Vurtne on 21-Nov-17.
 */
open class AppHolder<T> {

     /**
     * 获取请求对象
     * */
    fun getRequest(): XRequest = XClient().retrofit()!!.create(XRequest::class.java)

    /**
     * 请求Observable<T> observable
     */
    fun on(compositeDisposable: CompositeDisposable, observable: Observable<T>, observer: Observer<T>) {
        val consumer = Consumer<T> { }
        val error = Consumer<Throwable> { }
        compositeDisposable.add(observable.observeOn(Schedulers.io()).subscribeOn(AndroidSchedulers.mainThread()).subscribe(consumer,error))
    }

}