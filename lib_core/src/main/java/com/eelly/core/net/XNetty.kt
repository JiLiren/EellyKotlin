package com.eelly.core.net

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers

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
    fun onRequest(compositeDisposable: CompositeDisposable, observable: Observable<T>,
                  successConsumer: Consumer<T>,errorConsumer : Consumer<Throwable>) {
        compositeDisposable.add(observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(successConsumer,errorConsumer))
    }

}