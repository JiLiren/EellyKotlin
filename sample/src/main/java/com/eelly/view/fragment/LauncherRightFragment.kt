package com.eelly.view.fragment

import android.content.Intent
import com.eelly.R
import com.eelly.core.base.XFragment
import com.eelly.view.activity.MainActivity
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.fragment_launcher_right.*


/**
 * @author Vurtne on 21-Nov-17.
 *
 */

class LauncherRightFragment : XFragment(){

    override fun contentView(): Int = R.layout.fragment_launcher_right

    override fun initView() {
    }

    override fun initStatusBar() {
    }

    override fun initEvent() {
        setClick(mLoginBtn, Consumer {
            startActivity(Intent(context,MainActivity::class.java))
            activity!!.finish()
        })
    }

    override fun initData() {
    }

}
