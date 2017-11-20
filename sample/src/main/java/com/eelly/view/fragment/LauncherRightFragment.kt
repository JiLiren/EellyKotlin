package com.eelly.view.fragment

import android.content.Intent
import com.eelly.R
import com.eelly.core.base.XFragment
import com.eelly.view.activity.MainActivity
import io.reactivex.functions.Consumer


/**
 * @author Vurtne on 21-Nov-17.
 *
 */

class LauncherRightFragment : XFragment(){

    override fun contentView(): Int {
        return R.layout.fragment_launcher_right
    }

    override fun initView() {
    }

    override fun initStatusBar() {
    }

    override fun initEvent() {
        setClick(getView()!!.findViewById(R.id.btn_login), Consumer {
            startActivity(Intent(getContext(),MainActivity::class.java))
        })
    }

    override fun initData() {
    }

}
