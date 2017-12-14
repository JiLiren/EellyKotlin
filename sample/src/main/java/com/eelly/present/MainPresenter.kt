package com.eelly.present

import com.eelly.contract.IMainContract
import com.eelly.core.widget.banner.BannerEntity
import com.eelly.core.net.XNetty
import com.eelly.model.TheaterBean
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer

/**
 * @author Vurtne on 21-Nov-17.
 */
class MainPresenter(val mView: IMainContract.IView, val mCompositeDisposable: CompositeDisposable):
        IMainContract.IPresenter{


    lateinit var mTheaterBean : TheaterBean
    var mHolder : XNetty<TheaterBean>
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

    override fun onLoadMore() {
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

    override fun onGetBanner(): List<BannerEntity> {
        var entity : BannerEntity
        val list : ArrayList<BannerEntity> = ArrayList()
        mTheaterBean.subjects.forEach{
            bean ->
            entity = BannerEntity(bean.images.large,bean.title,bean.rating.average)
            list.add(entity)
        }
        onQuickSort(list,0,list.size-1)
        return list.subList(0,Math.max(list.size,5))
    }

    override fun onDestroy() {

    }

    /**
     * 根据评分排序
     * */
    private fun onQuickSort(list: ArrayList<BannerEntity>, left: Int, right: Int) {
        if (left >= right) {
            return
        }
        var i = left
        var j = right
        val key = list[left]
        while (i < j) {
            while (i < j && list[j].average <= key.average) {
                j--
            }
            list[i] = list[j]
            while (i < j && list[i].average > key.average) {
                i++
            }
            list[j] = list[i]
        }
        list[i] = key
        onQuickSort(list, left, i - 1)
        onQuickSort(list, i + 1, right)
    }

}