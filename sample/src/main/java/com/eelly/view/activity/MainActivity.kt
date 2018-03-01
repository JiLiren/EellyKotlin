package com.eelly.view.activity

import android.support.v4.widget.NestedScrollView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.View
import android.view.ViewTreeObserver
import com.eelly.R
import com.eelly.adapter.MoviesAdapter
import com.eelly.contract.IMainContract
import com.eelly.core.base.XActivity
import com.eelly.core.util.LogUtil
import com.eelly.holder.GlideImageLoader
import com.eelly.model.TheaterBean
import com.eelly.present.MainPresenter
import com.youth.banner.BannerConfig
import com.youth.banner.Transformer
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.layout_main_content.*

/**
 * @author Vurtne on 20-Nov-17.
 */
class MainActivity: XActivity(), IMainContract.IView {

    val TAG : String = "MainActivity"

    lateinit var mPresenter: IMainContract.IPresenter
    lateinit var mAdapter:MoviesAdapter

    var mMoreHeight : Int = 0
    var isLoading = false


    override fun setPresenter(presenter: IMainContract.IPresenter) {
        this.mPresenter = presenter
    }

    override fun contentView(): Int = R.layout.activity_main

    override fun initView() {
        MainPresenter(this,getCompositeDisposable())
        mRecycler.layoutManager = StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
        mRecycler.isNestedScrollingEnabled = false


        mMoreLayout.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                mMoreHeight = mMoreLayout.measuredHeight
                mMoreLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }

    override fun initStatusBar(statusHeight: Int) {
        val params = mToolbar.layoutParams
        params.height += statusHeight
        mToolbar.layoutParams = params
    }

    override fun initEvent() {
        setClick(mRefreshBtn, Consumer {
            mPresenter.refreshMovies()
        })

        mScrollView.setOnScrollChangeListener { view : NestedScrollView, scrollX: Int,
                                                scrollY: Int, oldScrollX: Int, oldScrollY: Int ->
            if (scrollY >= (view.getChildAt(0).measuredHeight - mMoreHeight -
                    view.measuredHeight) && !isLoading) {
                isLoading = true
                mPresenter.loadMore()
            }
        }

    }

    override fun initData() {
        LogUtil.d(TAG,"onRefreshNew")
        mPresenter.refreshMovies()
    }

    override fun setAdapter(bean: TheaterBean) {
        mAdapter = MoviesAdapter(bean.subjects,this)
        isLoading = false
        mMoreLayout.visibility = View.VISIBLE
        mRecycler.adapter = mAdapter
        initBanner(bean)
    }

    override fun addAdapter(bean: TheaterBean) {
        if (bean.subjects.size % 20 == 0){
            isLoading = false
        }else{
            mMoreLayout.visibility = View.GONE
        }
        mAdapter.addBean(bean.subjects)
    }

    override fun showLoading() {
        onShowLoading()
    }

    override fun hideLoading() {
        onShowContent()
    }

    override fun onStart() {
        super.onStart()
        mBannerLayout.startAutoPlay()
    }

    override fun onStop() {
        super.onStop()
        mBannerLayout.stopAutoPlay()
    }

    private fun initBanner(bean:TheaterBean){
        val list = mPresenter.getBannerMove(bean)
        val images = ArrayList<String>()
        val titles = ArrayList<String>()

        list.mapTo(images){ it.images.large }
        list.mapTo(titles){ it.title}

        mBannerLayout.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE)
        mBannerLayout.setImageLoader(GlideImageLoader())
        mBannerLayout.setImages(images)
        mBannerLayout.setBannerAnimation(Transformer.DepthPage)
        mBannerLayout.setBannerTitles(titles)
        mBannerLayout.isAutoPlay(true)
        mBannerLayout.setDelayTime(3000)
        mBannerLayout.setIndicatorGravity(BannerConfig.CENTER)
        mBannerLayout.start()
    }



}