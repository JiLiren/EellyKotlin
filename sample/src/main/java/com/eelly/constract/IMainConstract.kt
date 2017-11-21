package com.eelly.constract

import com.eelly.core.base.XPresenter
import com.eelly.core.base.XView

/**
 * @author Vurtne on 21-Nov-17.
 */
interface IMainConstract {

    interface IPresenter: XPresenter{
        fun onRefreshMovies()
    }

    interface IView : XView<IPresenter> {
        fun showLoading()
        fun hideLoading()
    }

}