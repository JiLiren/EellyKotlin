package com.eelly.net

import com.eelly.core.util.LogUtil
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers

/**
 * @author Vurtne on 21-Nov-17.
 */
open class XSignal<T> {

     /**
     * 获取请求对象
     * */
    fun getRequest(): XRequest = XClient().retrofit()!!.create(XRequest::class.java)

    /**
     * 请求Observable<T> observable
     */
    fun onRequest(compositeDisposable: CompositeDisposable, observable: Observable<T>,
                  successConsumer: Consumer<T>,errorConsumer : Consumer<Throwable>) {
        LogUtil.e("1111","222222")
        compositeDisposable.add(observable.observeOn(Schedulers.io()).
                subscribeOn(AndroidSchedulers.mainThread()).subscribe(successConsumer,errorConsumer))
    }

}