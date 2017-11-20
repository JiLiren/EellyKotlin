package com.eelly.view.activity

import android.transition.ChangeBounds
import android.transition.Slide
import android.view.Gravity
import com.eelly.R
import com.eelly.core.base.XActivity
import com.eelly.view.fragment.LauncherLeftFragment

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
        val slideTransition = Slide(Gravity.START)
        slideTransition.duration = resources.getInteger(R.integer.integer_1000).toLong()
        val leftFragment = LauncherLeftFragment()
        leftFragment.reenterTransition = slideTransition
        leftFragment.exitTransition = slideTransition
        leftFragment.sharedElementEnterTransition = ChangeBounds()
        supportFragmentManager.beginTransaction()
                .replace(R.id.layout_content, leftFragment)
                .commit()
    }
}