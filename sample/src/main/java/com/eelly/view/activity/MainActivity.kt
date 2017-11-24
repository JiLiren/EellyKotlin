package com.eelly.view.activity

import android.app.ProgressDialog
import android.support.v4.widget.NestedScrollView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.View
import android.view.ViewTreeObserver
import com.eelly.R
import com.eelly.adapter.MoviesAdapter
import com.eelly.bean.TheaterBean
import com.eelly.contract.IMainContract
import com.eelly.core.base.XActivity
import com.eelly.core.util.LogUtil
import com.eelly.present.MainPresenter
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.layout_main_content.*

/**
 * @author Vurtne on 20-Nov-17.
 */
class MainActivity: XActivity(), IMainContract.IView {

    val TAG : String = "MainActivity"

    lateinit var mPresenter: IMainContract.IPresenter
    lateinit var mAdapter:MoviesAdapter
    var mDialog: ProgressDialog? = null

    var mMoreHeight : Int = 0
    var isLoading = false


    override fun setPresenter(presenter: IMainContract.IPresenter) {
        this.mPresenter = presenter
    }

    override fun contentView(): Int {
        return R.layout.activity_main
    }

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
            mPresenter.onRefreshMovies()
        })

        mScrollView.setOnScrollChangeListener { view : NestedScrollView, scrollX: Int,
                                                scrollY: Int, oldScrollX: Int, oldScrollY: Int ->
            if (scrollY >= (view.getChildAt(0).getMeasuredHeight() - mMoreHeight -
                    view.getMeasuredHeight()) && !isLoading) {
                isLoading = true
                mPresenter.onLoadMoew()
            }
        }

    }

    override fun initData() {
        LogUtil.d(TAG,"onRefreshNew")
        mPresenter.onRefreshMovies()
    }

    override fun setAdapter(bean: TheaterBean) {
        mAdapter = MoviesAdapter(bean.subjects,this)
        mRecycler.adapter = mAdapter
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
        if (mDialog == null){
            mDialog = ProgressDialog(this)
        }
        if (!mDialog!!.isShowing){
            mDialog!!.show()
        }
    }

    override fun hideLoading() {
        if (mDialog!!.isShowing){
            mDialog!!.dismiss()
        }
    }


}