package com.eelly.view.fragment

import android.annotation.TargetApi
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.view.ViewCompat
import android.transition.ChangeBounds
import android.transition.Slide
import android.view.Gravity
import com.eelly.R
import com.eelly.core.base.XFragment
import io.reactivex.Flowable
import kotlinx.android.synthetic.main.fragment_launcher_left.*
import java.util.concurrent.TimeUnit


/**
 * @author Vurtne on 19-Nov-17.
 *
 */

class LauncherLeftFragment : XFragment(){

    override fun contentView(): Int = R.layout.fragment_launcher_left

    override fun initView() {
        ViewCompat.setTransitionName(mLogoIv, getString(R.string.transitionName_logo))
        ViewCompat.setTransitionName(mGitTv,  getString(R.string.transitionName_git))
    }

    override fun initStatusBar() {

    }

    override fun initEvent() {
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun initData() {

        val fragment = LauncherRightFragment()
        val transition = Slide(Gravity.END)

        transition.duration = resources.getInteger(R.integer.integer_1000).toLong()
        val changeBoundsTransition = ChangeBounds()
        changeBoundsTransition.duration = resources.getInteger(R.integer.integer_1000).toLong()

        fragment.enterTransition = transition
        fragment.allowEnterTransitionOverlap = true
        fragment.allowReturnTransitionOverlap = true
        fragment.sharedElementEnterTransition = changeBoundsTransition
        Flowable.timer(2, TimeUnit.SECONDS).subscribe {
            if(isVisible){
                fragmentManager!!.beginTransaction()
                        .replace(R.id.layout_content, fragment)
                        .addToBackStack(null)
                        .addSharedElement(mLogoIv, getString(R.string.transitionName_logo))
                        .addSharedElement(mGitTv, getString(R.string.transitionName_git))
                        .commit()
            }
        }
    }

}
