package com.eelly.contract

import com.eelly.core.base.XPresenter
import com.eelly.core.base.XView
import com.eelly.model.MovieBean
import com.eelly.model.TheaterBean

/**
 * @author Vurtne on 21-Nov-17.
 */
interface IMainContract {

    interface IPresenter: XPresenter{
        fun refreshMovies()
        fun loadMore()
        fun getBannerMove(bean:TheaterBean):List<MovieBean>
    }

    interface IView : XView<IPresenter> {
        fun showLoading()
        fun hideLoading()
        fun setAdapter(bean: TheaterBean)
        fun addAdapter(bean: TheaterBean)
    }

}