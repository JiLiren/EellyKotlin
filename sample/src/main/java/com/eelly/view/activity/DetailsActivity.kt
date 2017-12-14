package com.eelly.view.activity

import com.eelly.R
import com.eelly.adapter.DetailsPagerAdapter
import com.eelly.core.base.XActivity
import com.eelly.model.MovieBean
import com.eelly.view.fragment.CommentFragment
import com.eelly.view.fragment.DiscussFragment
import kotlinx.android.synthetic.main.activity_details.*

/**
 * @author Vurtne on 25-Nov-17.
 */
class DetailsActivity : XActivity() {

    private val mLeftFragment : CommentFragment = CommentFragment()
    private val mRightFragment : DiscussFragment = DiscussFragment()
    private var mMovie : MovieBean? = null

    private lateinit var mAdapter : DetailsPagerAdapter


    override fun contentView(): Int = R.layout.activity_details

    override fun initView() {

    }

    override fun initStatusBar(statusHeight: Int) {

    }

    override fun initEvent() {
    }

    override fun initData() {
//        mMovie = intent.getParcelableExtra("data")
//
//        Glide.with(context).load(mMovie!!.images.large).
//                into(mBannerIv)

        mAdapter = DetailsPagerAdapter(this,supportFragmentManager,mLeftFragment,mRightFragment)
        mViewPager.adapter = mAdapter

    }


}