package com.eelly.contract

import com.eelly.core.base.XPresenter
import com.eelly.core.base.XView
import com.eelly.core.widget.banner.BannerEntity
import com.eelly.model.TheaterBean

/**
 * @author Vurtne on 21-Nov-17.
 */
interface IMainContract {

    interface IPresenter: XPresenter{
        fun onRefreshMovies()
        fun onLoadMore()
        fun onGetBanner():List<BannerEntity>
    }

    interface IView : XView<IPresenter> {
        fun showLoading()
        fun hideLoading()
        fun setAdapter(bean: TheaterBean)
        fun addAdapter(bean: TheaterBean)
    }

}