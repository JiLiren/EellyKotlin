package com.eelly.present

import com.eelly.contract.IMainContract
import com.eelly.core.net.NetListener
import com.eelly.core.net.XNetty
import com.eelly.core.net.XResponse
import com.eelly.holder.SortHolder
import com.eelly.model.MovieBean
import com.eelly.model.TheaterBean
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer

/**
 * @author Vurtne on 21-Nov-17.
 */
class MainPresenter(val mView: IMainContract.IView, val mCompositeDisposable: CompositeDisposable):
        IMainContract.IPresenter{

    override fun onDestroy() {
    }


    lateinit var mTheaterBean : TheaterBean
    var mHolder : XNetty<TheaterBean>
    val COUNT_PAGE  = "20"



    init {
        mView.setPresenter(this)
        mHolder = XNetty()
    }

    /**
     * @param
     * */
    override fun refreshMovies() {
        mView.showLoading()
        mHolder.onRequest(mCompositeDisposable,mHolder.getRequest().onRequestMoviesList(),
                Consumer{
                mView.hideLoading()
                },
                object:NetListener<TheaterBean> {
                    override fun onResponse(response: XResponse<TheaterBean>) {
                        mTheaterBean = response.get()!!
                        mView.setAdapter(mTheaterBean)
                        mView.hideLoading()
                    }
                },
                TheaterBean::class.java)
    }

    override fun loadMore() {
        mHolder.onRequest(mCompositeDisposable,mHolder.getRequest().onRequestMoviesMore(
                mTheaterBean.count + 1,COUNT_PAGE),
                Consumer{
                },
                object:NetListener<TheaterBean> {
                    override fun onResponse(response: XResponse<TheaterBean>) {
                        val entity = response.get()
                        mView.addAdapter(entity!!)
                        mTheaterBean.count += entity.count
                        mTheaterBean.subjects.addAll(entity.subjects)
                    }
                },
                TheaterBean::class.java)
    }

    override fun getBannerMove(bean:TheaterBean):List<MovieBean>{
        val list = ArrayList<MovieBean>()
        list.addAll(bean.subjects)
        return SortHolder.onQuickSort(list).subList(0,5)
    }


}