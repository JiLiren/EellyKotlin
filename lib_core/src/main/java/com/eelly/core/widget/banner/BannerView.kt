package com.eelly.core.widget.banner

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Message
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.eelly.core.R
import com.eelly.core.event.OnBannerClickListener
import kotlinx.android.synthetic.main.layout_banner.view.*
import java.util.*


/**
 * @author Vurtne on 5-Dec-17.
 */
class BannerView : FrameLayout {

    private var mAutoScrollDelay: Long = 5

    private val mEntities:ArrayList<BannerEntity> = ArrayList()

    private var mAdapter = BannerAdapter(context,mEntities)

    private var currentIdx = 0

    private val mMainHandler: Handler

    private var mTimer: Timer? = null
    private var mAutoScrollTimer: Timer? = null


    constructor(context: Context) :this(context,null)

    constructor(context: Context, attrs: AttributeSet?):this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int):
            super(context,attrs,defStyleAttr){

        View.inflate(getContext(), R.layout.layout_banner, this)
        val typeArray = context.obtainStyledAttributes(attrs,
                R.styleable.banner)
        val lineColor = typeArray.getColor(R.styleable.banner_lineColor,
                ContextCompat.getColor(getContext(), R.color.color_orange_700))
        typeArray.recycle()
        mLine.setLineColor(lineColor)
        mMainHandler = Handler(context.mainLooper)
    }

    @SuppressLint("HandlerLeak")
    private val mHandler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (currentIdx >= mEntities.size) {
                currentIdx = 0
            }
            mViewPager.setCurrentItem(currentIdx, true)
            currentIdx++
        }
    }

    fun setEntities(entities: List<BannerEntity>) {
        addExtraPage(entities)
        showBanner()
    }

    private fun addExtraPage(entities: List<BannerEntity>) {
        mEntities.clear()
        mEntities.add(entities[entities.size - 1])
        mEntities.addAll(entities)
        mEntities.add(entities[0])
        mAdapter.notifyDataSetChanged()
    }

    private fun showBanner() {
        mLine.setPageWidth(mEntities.size)
        mViewPager.adapter = mAdapter
        mViewPager.setCurrentItem(1, true)
        currentIdx = 1
        mViewPager.clearOnPageChangeListeners()
        mViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                mLine.setPageScrolled(position, positionOffset)
                currentIdx = position
                if (positionOffsetPixels.toDouble() == 0.0) {
                    setViewPagerItemPosition(position)
                }
            }

            override fun onPageSelected(position: Int) {

            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })
    }

    private fun setViewPagerItemPosition(position: Int) {
        if (position == mEntities.size - 1) {
            mViewPager.setCurrentItem(1, false)
        } else if (position == 0) {
            mViewPager.setCurrentItem(mEntities.size - 2, false)
        } else {
            mViewPager.currentItem = position
        }
    }

    private fun nextScroll() {
        val position = mViewPager.currentItem
        mLine.setPageScrolled(position + 1, 0f)
        setViewPagerItemPosition(position + 1)
    }
//
//    fun startAutoScroll() {
//        mAutoScrollTimer = Timer()
//        mAutoScrollTimer.schedule(object : TimerTask() {
//            override fun run() {
//                mMainHandler.post { nextScroll() }
//            }
//        }, mAutoScrollDelay, mAutoScrollDelay)
//    }
//
//    fun stopAutoScroll() {
//        mAutoScrollTimer.cancel()
//    }
    fun setAutoScrollDelay(delay: Long) {
        mAutoScrollDelay = delay
    }

    fun setOnBannerClickListener(listener: OnBannerClickListener) {
        mAdapter.setOnBannerClickListener(listener)
    }


}

