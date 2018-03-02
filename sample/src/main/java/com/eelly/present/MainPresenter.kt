package com.eelly.present

import com.eelly.contract.IMainContract
import com.eelly.core.net.XNetty
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
                    entity ->
                    mTheaterBean = entity
                    mView.setAdapter(entity)
                    mView.hideLoading()
                } ,
                Consumer{
                    mView.hideLoading()
                })
    }

    override fun loadMore() {
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

    override fun getBannerMove(bean:TheaterBean):List<MovieBean>{
        val list = ArrayList<MovieBean>()
        list.addAll(bean.subjects)
        return SortHolder.onQuickSort(list).subList(0,5)
    }


}