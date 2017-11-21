package com.eelly.present

import com.eelly.bean.MovieEntity
import com.eelly.constract.IMainConstract
import com.eelly.global.AppHolder
import io.reactivex.disposables.CompositeDisposable

/**
 * @author Vurtne on 21-Nov-17.
 */
class MainPresenter(val mView: IMainConstract.IView, val mCompositeDisposable: CompositeDisposable):
        IMainConstract.IPresenter{

    lateinit var mMovies : List<MovieEntity>
    lateinit var mHolder : AppHolder<List<MovieEntity>>
    var mCurPage : Int = 0


    init {
        mView.setPresenter(this)
        mHolder = AppHolder()
    }

    override fun onRefreshMovies() {
//        mHolder.onRequest(mCompositeDisposable,mHolder.getRequest().onRequestMoviesList(), Consumer {
//
//        })
    }

    override fun onDestroy() {

    }

}