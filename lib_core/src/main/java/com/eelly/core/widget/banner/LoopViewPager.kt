package com.eelly.core.widget.banner

import android.content.Context
import android.os.Parcelable
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import java.util.ArrayList

/**
 * @author vurtne on 3-Jan-18.
 */
class LoopViewPager : ViewPager{

    private var mAdapter: InnerLoopAdapter? = null

    private var mOnPageChangeListeners: MutableList<ViewPager.OnPageChangeListener>? = null

    companion object {
        fun toRealPosition(position: Int, count: Int): Int {
            return if (count <= 1) {
                0
            } else (position - 1 + count) % count
        }

        fun toInnerPosition(real: Int): Int {
            return real + 1
        }
    }

    constructor(context: Context):this(context,null)

    constructor(context: Context,attrs: AttributeSet?) : super(context,attrs){
        addOnPageChangeListener(mInnerListener)
    }


    override fun onLayout(p0: Boolean, p1: Int, p2: Int, p3: Int, p4: Int) {

    }

    override fun setAdapter(adapter: PagerAdapter?) {
        mAdapter = if (adapter == null) null else InnerLoopAdapter(adapter)
        super.setAdapter(mAdapter)
        setCurrentItem(0, false)
    }


    override fun getAdapter(): PagerAdapter? {
        return if (mAdapter != null) mAdapter?.pa else null
    }

    override fun getCurrentItem(): Int {
        return if (mAdapter != null) mAdapter?.getRealPosition(super.getCurrentItem())!!  else 0
    }

    override fun setCurrentItem(item: Int, smoothScroll: Boolean) {
        super.setCurrentItem(toInnerPosition(item), smoothScroll)
    }

    override fun setCurrentItem(item: Int) {
        super.setCurrentItem(toInnerPosition(item), true)
    }

    override fun setOnPageChangeListener(listener: ViewPager.OnPageChangeListener) {
        removeOnPageChangeListener(listener)
        addOnPageChangeListener(listener)
    }

    override fun addOnPageChangeListener(listener: ViewPager.OnPageChangeListener) {
        if (mOnPageChangeListeners == null) {
            mOnPageChangeListeners = ArrayList()
        }
        mOnPageChangeListeners?.add(listener)
    }

    override fun removeOnPageChangeListener(listener: ViewPager.OnPageChangeListener) {
        if (mOnPageChangeListeners != null) {
            mOnPageChangeListeners?.remove(listener)
        }
    }

    override fun clearOnPageChangeListeners() {
        if (mOnPageChangeListeners != null) {
            mOnPageChangeListeners?.clear()
        }
    }

    private fun dispatchOnPageScrolled(position: Int, offset: Float, offsetPixels: Int) {
        if (mOnPageChangeListeners != null) {
            var i = 0
            val z = mOnPageChangeListeners?.size
            while (i < z!!) {
                val listener = mOnPageChangeListeners?.get(i)
                listener?.onPageScrolled(position, offset, offsetPixels)
                i++
            }
        }
    }

    private fun dispatchOnPageSelected(position: Int) {
        if (mOnPageChangeListeners != null) {
            var i = 0
            val z = mOnPageChangeListeners?.size
            while (i < z!!) {
                val listener = mOnPageChangeListeners?.get(i)
                listener?.onPageSelected(position)
                i++
            }
        }
    }

    private fun dispatchOnScrollStateChanged(state: Int) {
        if (mOnPageChangeListeners != null) {
            var i = 0
            val z = mOnPageChangeListeners?.size
            while (i < z!!) {
                val listener = mOnPageChangeListeners?.get(i)
                listener?.onPageScrollStateChanged(state)
                i++
            }
        }
    }

    private val mInnerListener :OnPageChangeListener = object : OnPageChangeListener{

        private var mPreviousOffset = -1f
        private var mPreviousPosition = -1f

        override fun onPageScrollStateChanged(state: Int) {
            if (mAdapter != null) {
                val position = super@LoopViewPager.getCurrentItem()
                if (state == ViewPager.SCROLL_STATE_IDLE && (position == 0 || position == mAdapter?.count!! - 1)) {
                    setCurrentItem(mAdapter?.getRealPosition(position)!!, false)
                }
            }
            dispatchOnScrollStateChanged(state)
        }

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            if (mAdapter == null) {
                mPreviousOffset = positionOffset
                if (positionOffset > .5) {
                    dispatchOnPageScrolled(0, 0f, 0)
                } else {
                    dispatchOnPageScrolled(position, 0f, 0)
                }
                return
            }
            val realPosition = mAdapter?.getRealPosition(position)
            val lastPosition = mAdapter?.getCount()!! - 1

            if (positionOffset == 0f && mPreviousOffset == 0f && lastPosition != 0 && (position == 0 || position == lastPosition)) {
                setCurrentItem(realPosition!!, false)
            }

            mPreviousOffset = positionOffset

            when {
                realPosition != mAdapter?.getRealCount()!! - 1 -> dispatchOnPageScrolled(realPosition!!, positionOffset, positionOffsetPixels)
                positionOffset > .5 -> dispatchOnPageScrolled(0, 0f, 0)
                else -> dispatchOnPageScrolled(realPosition, 0f, 0)
            }
        }

        override fun onPageSelected(position: Int) {
            val real = mAdapter?.getRealPosition(position)
            if (mPreviousPosition == real?.toFloat()) {
                return
            }
            mPreviousPosition = real?.toFloat()!!
            dispatchOnPageSelected(real)
        }
    }

    private class InnerLoopAdapter internal constructor(internal val pa: PagerAdapter) : PagerAdapter() {

        private var recycler = SparseArray<Any>()

        private val realCount: Int
            get() = pa.count

        override fun notifyDataSetChanged() {
            recycler = SparseArray()
            super.notifyDataSetChanged()
        }

        fun getRealPosition(position: Int): Int {
            return toRealPosition(position, pa.count)
        }

        internal fun getRealCount(): Int {
            return pa.count
        }
        override fun getCount(): Int {
            val realCount = realCount
            return if (realCount > 1) realCount + 2 else realCount
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val real = getRealPosition(position)

            val destroy = recycler.get(position)
            if (destroy != null) {
                recycler.remove(position)
                return destroy
            }
            return pa.instantiateItem(container, real)
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            val first = 1
            val last = realCount

            if (position == first || position == last) {
                recycler.put(position, `object`)
            } else {
                pa.destroyItem(container, getRealPosition(position), `object`)
            }
        }

        override fun finishUpdate(container: ViewGroup) {
            pa.finishUpdate(container)
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return pa.isViewFromObject(view, `object`)
        }

        override fun restoreState(bundle: Parcelable?, classLoader: ClassLoader?) {
            pa.restoreState(bundle, classLoader)
        }

        override fun saveState(): Parcelable? {
            return pa.saveState()
        }

        override fun startUpdate(container: ViewGroup) {
            pa.startUpdate(container)
        }

        override fun setPrimaryItem(container: ViewGroup, position: Int, `object`: Any) {
            pa.setPrimaryItem(container, position, `object`)
        }
    }

}