package com.eelly.present

import com.eelly.bean.TheaterBean
import com.eelly.contract.IMainContract
import com.eelly.net.XNetty
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer

/**
 * @author Vurtne on 21-Nov-17.
 */
class MainPresenter(val mView: IMainContract.IView, val mCompositeDisposable: CompositeDisposable):
        IMainContract.IPresenter{

    lateinit var mTheaterBean : TheaterBean
    lateinit var mHolder : XNetty<TheaterBean>
    var mCurPage : Int = 1
    val COUNT_PAGE  = "20"



    init {
        mView.setPresenter(this)

        mHolder = XNetty()
    }

    override fun onRefreshMovies() {
        mView.showLoading()
        mHolder.onRequest(mCompositeDisposable,mHolder.getRequest().onRequestMoviesList(),
                Consumer{
                    entity ->
                    mTheaterBean = entity
                    mView.setAdapter(entity)
                    mView.hideLoading()
                } ,
                Consumer{
                    mView.hideLoading()
                })
    }

    override fun onLoadMoew() {
//        mHolder.onRequest(mCompositeDisposable,mHolder.getRequest().onRequestMoviesMore(
//                (mTheaterBean.count- 1).toString(),COUNT_PAGE),
        mHolder.onRequest(mCompositeDisposable,mHolder.getRequest().onRequestMoviesMore(
                mTheaterBean.count + 1,COUNT_PAGE),
                Consumer{
                    entity ->
                    mView.addAdapter(entity)
                    mTheaterBean.count += entity.count
                    mTheaterBean.subjects.addAll(entity.subjects)
                } ,
                Consumer{
                })
    }

    override fun onDestroy() {

    }

}