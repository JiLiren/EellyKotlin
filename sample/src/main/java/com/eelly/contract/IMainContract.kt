package com.eelly.contract

import com.eelly.bean.TheaterBean
import com.eelly.core.base.XPresenter
import com.eelly.core.base.XView

/**
 * @author Vurtne on 21-Nov-17.
 */
interface IMainContract {

    interface IPresenter: XPresenter{
        fun onRefreshMovies()
    }

    interface IView : XView<IPresenter> {
        fun showLoading()
        fun hideLoading()
        fun setAdapter(bean: TheaterBean)
    }

}