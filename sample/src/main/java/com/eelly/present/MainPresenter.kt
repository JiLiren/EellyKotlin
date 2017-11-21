package com.eelly.present

import com.eelly.bean.MoviesEntity
import com.eelly.constract.IMainConstract

/**
 * @author Vurtne on 21-Nov-17.
 */
class MainPresenter(val view: IMainConstract.IView):IMainConstract.IPresenter{

    lateinit var mMovies : List<MoviesEntity>
    var mCurPage : Int = 0

    init {
        view.setPresenter(this)
    }

    override fun onRefreshMovies() {

    }

    override fun onDestroy() {

    }

}