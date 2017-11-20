package cn.sample.view.fragment

import cn.lib_core.base.XFragment

/**
 * @author Vurtne on 19-Nov-17.
 *
 */

class LauncherLeftFragment :XFragment(){

    fun newInstance(): LauncherLeftFragment {
        val fragment = LauncherLeftFragment()
        return fragment
    }

    override fun contentView(): Int {
        return 0
    }

    override fun initView() {
    }

    override fun initStatusBar() {
    }

    override fun initEvent() {
    }

    override fun initData() {
    }

}
