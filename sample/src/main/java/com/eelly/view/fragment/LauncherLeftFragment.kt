package com.eelly.view.fragment

import android.support.v4.view.ViewCompat
import android.transition.ChangeBounds
import android.transition.Slide
import android.view.Gravity
import com.eelly.R
import com.eelly.core.base.XFragment
import io.reactivex.Flowable
import java.util.concurrent.TimeUnit


/**
 * @author Vurtne on 19-Nov-17.
 *
 */

class LauncherLeftFragment : XFragment(){

    override fun contentView(): Int {
        return R.layout.fragment_launcher_left
    }

    override fun initView() {
        ViewCompat.setTransitionName(getView()!!.findViewById(R.id.iv_logo), getString(R.string.transitionName_logo))
        ViewCompat.setTransitionName(getView()!!.findViewById(R.id.tv_git),  getString(R.string.transitionName_git))
    }

    override fun initStatusBar() {
    }

    override fun initEvent() {
    }

    override fun initData() {

        val fragment = LauncherRightFragment()
        val transition = Slide(Gravity.END)

        transition.duration = resources.getInteger(R.integer.integer_1000).toLong()
        val changeBoundsTransition = ChangeBounds()
        changeBoundsTransition.duration = resources.getInteger(R.integer.integer_1000).toLong()

        fragment.setEnterTransition(transition)
        fragment.setAllowEnterTransitionOverlap(true)
        fragment.setAllowReturnTransitionOverlap(true)
        fragment.setSharedElementEnterTransition(changeBoundsTransition)
        Flowable.timer(2, TimeUnit.SECONDS).subscribe {
            fragmentManager.beginTransaction()
                    .replace(R.id.layout_content, fragment)
                    .addToBackStack(null)
                    .addSharedElement(getView()!!.findViewById(R.id.iv_logo), getString(R.string.transitionName_logo))
                    .addSharedElement(getView()!!.findViewById(R.id.tv_git), getString(R.string.transitionName_git))
                    .commit()
        }
    }

}
