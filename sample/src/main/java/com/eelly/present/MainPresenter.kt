package com.eelly.present

import com.eelly.bean.TheaterBean
import com.eelly.contract.IMainContract
import com.eelly.core.util.LogUtil
import com.eelly.net.XNetty
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer

/**
 * @author Vurtne on 21-Nov-17.
 */
class MainPresenter(val mView: IMainContract.IView, val mCompositeDisposable: CompositeDisposable):
        IMainContract.IPresenter{

    lateinit var mMovies : List<TheaterBean>
    lateinit var mHolder : XNetty<TheaterBean>
    var mCurPage : Int = 0


    init {
        mView.setPresenter(this)

        mHolder = XNetty()
    }

    override fun onRefreshMovies() {
        mView.showLoading()
        mHolder.onRequest(mCompositeDisposable,mHolder.getRequest().onRequestMoviesList(), Consumer{
            entity ->
            mView.setAdapter(entity)
            mView.hideLoading()
        } ,
                Consumer{
                    throwable ->
                    mView.hideLoading()
                    LogUtil.e("1111",throwable.message.toString())
                })
    }

    override fun onDestroy() {

    }

}