package com.eelly.adapter

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.eelly.R

/**
 * @author Vurtne on 6-Dec-17.
 */
class DetailsPagerAdapter(context: Context, supportFragmentManager: FragmentManager, var mLeftFragment: Fragment,
                          var mRightFragment: Fragment) : FragmentPagerAdapter(supportFragmentManager){

    private val mTitles : Array<String> = arrayOf(context.getString(R.string.text_table_comment),
            context.getString(R.string.text_table_discuss))

    override fun getCount(): Int = 2

    override fun getItem(position: Int): Fragment = if (position == 0) mLeftFragment else mRightFragment

    override fun getPageTitle(position: Int): CharSequence = mTitles[position]

}
