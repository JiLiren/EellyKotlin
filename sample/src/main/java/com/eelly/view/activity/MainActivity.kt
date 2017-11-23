package com.eelly.view.activity

import android.app.ProgressDialog
import android.support.v7.widget.StaggeredGridLayoutManager
import com.eelly.R
import com.eelly.adapter.MoviesAdapter
import com.eelly.bean.TheaterBean
import com.eelly.contract.IMainContract
import com.eelly.core.base.XActivity
import com.eelly.present.MainPresenter
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.layout_main_content.*

/**
 * @author Vurtne on 20-Nov-17.
 */
class MainActivity: XActivity(), IMainContract.IView {


    lateinit var mPresenter: IMainContract.IPresenter
    lateinit var mAdapter:MoviesAdapter
    var mDialog: ProgressDialog? = null


    companion object {
        val TAG : String = "MainActivity"
    }

    override fun setPresenter(presenter: IMainContract.IPresenter) {
        this.mPresenter = presenter
    }

    override fun contentView(): Int {
        return R.layout.activity_main;
    }

    override fun initView() {
        MainPresenter(this,getCompositeDisposable())
        mRecycler.layoutManager = StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
        mRecycler.isNestedScrollingEnabled = false
//  mRecycler.adapter(MoviesAdapter(null,this))

    }

    override fun initStatusBar(statusHeight: Int) {
    }

    override fun initEvent() {
        setClick(mRefreshBtn, Consumer {
            mPresenter.onRefreshMovies()
        })
    }

    override fun initData() {
        mPresenter.onRefreshMovies()
    }

    override fun setAdapter(bean: TheaterBean) {
        mAdapter = MoviesAdapter(bean.subjects,this)
        mRecycler.adapter = mAdapter
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