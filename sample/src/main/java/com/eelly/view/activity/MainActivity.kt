package com.eelly.view.activity

import com.eelly.R
import com.eelly.core.base.XActivity

//import kotlinx.android.synthetic.main.activity_main.*

/**
 * @author Vurtne on 20-Nov-17.
 */
class MainActivity: XActivity() {


    override fun contentView(): Int {
        return R.layout.activity_main;
    }

    override fun initView() {
//        mRecycler.layoutManager = StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)


    }

    override fun initStatusBar(statusHeight: Int) {
    }

    override fun initEvent() {
    }

    override fun initData() {
    }



}