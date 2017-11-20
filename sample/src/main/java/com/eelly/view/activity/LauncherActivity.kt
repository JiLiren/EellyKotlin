package com.eelly.view.activity

import android.transition.ChangeBounds
import android.transition.Slide
import android.view.Gravity
import cn.lib_core.base.XActivity
import cn.sample.view.fragment.LauncherLeftFragment
import com.eelly.R

/**
 * @author Vurtne on 19-Nov-17.
 *
 */
class LauncherActivity: XActivity() {

    override fun contentView():Int{
        return R.layout.activity_launcher
    }

    override fun initView(){

    }

    override fun initStatusBar(statusHeight: Int){

    }

    override fun initEvent(){

    }

    override fun initData(){
        val slideTransition = Slide(Gravity.LEFT)
        slideTransition.setDuration(getResources().getInteger(R.integer.integer_1500).toLong())
        val leftFragment = LauncherLeftFragment()
        leftFragment.setReenterTransition(slideTransition)
        leftFragment.setExitTransition(slideTransition)
        leftFragment.setSharedElementEnterTransition(ChangeBounds())
        supportFragmentManager.beginTransaction()
                .replace(R.id.layout_content, leftFragment)
                .commit()
    }
}