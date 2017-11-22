package com.eelly.present

import com.eelly.bean.MovieEntity
import com.eelly.constract.IMainConstract
import com.eelly.core.util.LogUtil
import com.eelly.net.XNetty
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer

/**
 * @author Vurtne on 21-Nov-17.
 */
class MainPresenter(val mView: IMainConstract.IView, val mCompositeDisposable: CompositeDisposable):
        IMainConstract.IPresenter{

    lateinit var mMovies : List<MovieEntity>
    lateinit var mHolder : XNetty<MovieEntity>
    var mCurPage : Int = 0


    init {
        mView.setPresenter(this)
        mHolder = XNetty()
    }

    override fun onRefreshMovies() {
        LogUtil.e("1111","11111")

        mHolder.onRequest(mCompositeDisposable,mHolder.getRequest().onRequestMoviesList(), Consumer{
            entity -> LogUtil.e("1111",entity.toString())
        } ,
                Consumer{
                    throwable ->
                    LogUtil.e("1111",throwable.message.toString())
                })
    }

    override fun onDestroy() {

    }

}