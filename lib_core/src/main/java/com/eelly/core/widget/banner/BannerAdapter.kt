package com.eelly.core.widget.banner

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.view.View
import android.view.ViewGroup
import com.eelly.core.event.OnBannerClickListener
import java.util.*


/**
 * @author Vurtne on 5-Dec-17.
 */
class BannerAdapter(var context:Context,var mEntities :List<BannerEntity>) : PagerAdapter(){


    var mLayouts : ArrayList<BannerLayout> = ArrayList()
    var mListener : OnBannerClickListener ? =null

    init {
        setLayouts()
    }

    private fun setLayouts() {
        mEntities.forEach {
            entity ->
            val layout = BannerLayout(context,null)
            layout.setEntity(entity)
            mLayouts.add(layout)
        }
    }

    override fun getCount(): Int = mLayouts.size

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view === obj
    }

    override fun instantiateItem(container: ViewGroup,position:Int):Any{
        val bannerLayout = mLayouts[position]

        bannerLayout.setOnClickListener {
            if (mListener != null) {
                mListener!!.onClick(position - 1)
            }
        }
        container.addView(bannerLayout, 0)
        return mLayouts[position]
    }


    override fun destroyItem(container:ViewGroup,position:Int, obj:Any ) {
        container.removeView(mLayouts[position])
    }

    override fun setPrimaryItem(container:ViewGroup,position:Int, obj:Any ) {
        super.setPrimaryItem(container, position, obj)
    }

    fun setOnBannerClickListener(listener: OnBannerClickListener) {
        this.mListener = listener
    }
}