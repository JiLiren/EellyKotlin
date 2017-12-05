package com.eelly.core.widget

import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.Gravity
import android.view.animation.*
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.eelly.core.R
import com.eelly.core.animation.CustomTranslateAnimation
import com.eelly.core.animation.Translate
import com.eelly.core.event.LoadAnimationListener

/**
 * @author Vurtne on 5-Dec-17.
 *
 */
class LoadingLayout(context : Context,attrs :AttributeSet) : RelativeLayout(context, attrs)  {

    lateinit var mLoadView : ImageView
    lateinit var mWindView : ImageView
    lateinit var mMsgView : TextView

    lateinit var mTextInAnimation: AlphaAnimation
    lateinit var mTextOutAnimation: AlphaAnimation

    lateinit var mWindInAnimation: AnimationSet
    lateinit var mWindOutAnimation: AnimationSet

    lateinit var mLoadAnimationIn: AnimationSet
    lateinit var mLoadAnimationOut: AnimationSet

    lateinit var mLoadMiddleAnimation: AnimationDrawable
    lateinit var mLoadJumpAnimation: CustomTranslateAnimation

    lateinit var mListener: OnFinishListener
    var sStop = false

    init {
        initView()
    }

    fun initView(){
        mLoadView = ImageView(context)
        mLoadView.setImageResource(R.mipmap.ic_bee)
        mLoadView.id = R.id.loading_bee

        addView(mLoadView, RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT))

        mWindView = ImageView(context)
        mWindView.setImageResource(R.mipmap.ic_beem)
        addView(mWindView, RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT))

        mMsgView = TextView(context)

        mMsgView.text = context.getString(R.string.text_loading)
        mMsgView.gravity = Gravity.CENTER
        mMsgView.setTextColor(ContextCompat.getColor(context,R.color.material_orange_700))
        addView(mMsgView, RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT))

        (mMsgView.layoutParams as RelativeLayout.LayoutParams).addRule(RelativeLayout.BELOW,
                R.id.loading_bee)
        (mMsgView.layoutParams as RelativeLayout.LayoutParams).setMargins(0, 20, 0, 0)


        val textInitAnimation = AlphaAnimation(0f, 0f)
        mMsgView.startAnimation(textInitAnimation)
        textInitAnimation.fillAfter = true

        mTextInAnimation = AlphaAnimation(0f, 1f)
        mTextInAnimation.duration = 500
        mTextInAnimation.fillAfter = true

        mTextOutAnimation = AlphaAnimation(1f, 0f)
        mTextOutAnimation.duration = 500
        mTextOutAnimation.fillAfter = true


        val loadInit = TranslateAnimation(Animation.RELATIVE_TO_SELF, -1f, Animation.RELATIVE_TO_SELF,
                -1f, Animation.RELATIVE_TO_SELF, 0f,Animation.RELATIVE_TO_SELF, 0f)
        loadInit.fillAfter = true
        mLoadView.startAnimation(loadInit)

        mLoadAnimationIn = AnimationSet(true)
        val translate1 = CustomTranslateAnimation(Translate.PARTONE)
        translate1.duration = 800

        val loadInAlpha = AlphaAnimation(0f, 1f)
        loadInAlpha.duration = 300
        loadInAlpha.startOffset = 0
        mLoadAnimationIn.addAnimation(translate1)
        mLoadAnimationIn.addAnimation(loadInAlpha)
        mLoadAnimationIn.fillAfter = true

        val loadSpeed = 34
        mLoadMiddleAnimation = AnimationDrawable()
        mLoadMiddleAnimation.addFrame(ContextCompat.getDrawable(context,R.mipmap.ic_bee_01), loadSpeed)
        mLoadMiddleAnimation.addFrame(ContextCompat.getDrawable(context,R.mipmap.ic_bee_02), loadSpeed)
        mLoadMiddleAnimation.addFrame(ContextCompat.getDrawable(context,R.mipmap.ic_bee_03), loadSpeed)
        mLoadMiddleAnimation.addFrame(ContextCompat.getDrawable(context,R.mipmap.ic_bee_04), loadSpeed)
        mLoadMiddleAnimation.addFrame(ContextCompat.getDrawable(context,R.mipmap.ic_bee_05), loadSpeed)
        mLoadMiddleAnimation.addFrame(ContextCompat.getDrawable(context,R.mipmap.ic_bee_06), loadSpeed)
        mLoadMiddleAnimation.isOneShot = false

        mLoadJumpAnimation = CustomTranslateAnimation(Translate.PARTTWO)
        mLoadJumpAnimation.duration = 380
        mLoadJumpAnimation.repeatMode = Animation.REVERSE

        mLoadAnimationOut = AnimationSet(true)
        val translate2 = CustomTranslateAnimation(Translate.PARTTHREE)
        translate2.duration = 800

        val loadOutAlpha = AlphaAnimation(1f, 0f)
        loadOutAlpha.duration = 500
        loadOutAlpha.startOffset = 0
        mLoadAnimationOut.addAnimation(translate2)
        mLoadAnimationOut.addAnimation(loadOutAlpha)
        mLoadAnimationOut.fillAfter = true

        val lastBeeInitAnimation = AlphaAnimation(0f, 0f)
        mWindView.startAnimation(lastBeeInitAnimation)
        lastBeeInitAnimation.fillAfter = true

        mWindInAnimation = AnimationSet(true)
        val translate3 = CustomTranslateAnimation(Translate.PARTONE)
        translate3.duration = 800

        val windowInAlpha = AlphaAnimation(0f, 1f)
        windowInAlpha.duration = 300
        windowInAlpha.startOffset = 0

        val windowOutAlpha = AlphaAnimation(1f, 0f)
        windowOutAlpha.duration = 200
        windowOutAlpha.startOffset = 600

        mWindInAnimation.addAnimation(translate3)
        mWindInAnimation.addAnimation(windowInAlpha)
        mWindInAnimation.addAnimation(windowOutAlpha)
        mWindInAnimation.fillAfter = true

        mWindOutAnimation = AnimationSet(true)
        val translate4 = CustomTranslateAnimation(Translate.PARTTHREE)
        translate4.duration = 700

        val windowInAlpha2 = AlphaAnimation(0f, 1f)
        windowInAlpha2.duration = 200
        windowInAlpha2.startOffset = 0

        val windowOutAlpha2 = AlphaAnimation(1f, 0f)
        windowOutAlpha2.duration = 200
        windowOutAlpha2.startOffset = 400

        mWindOutAnimation.addAnimation(translate4)
        mWindOutAnimation.addAnimation(windowInAlpha2)
        mWindOutAnimation.addAnimation(windowOutAlpha2)
        mWindOutAnimation.fillAfter = true

        //动画加速线
        mLoadAnimationIn.interpolator = DecelerateInterpolator()
        mLoadAnimationOut.interpolator = DecelerateInterpolator()

    }

    fun onStartAnimation() {
        sStop = false
        mLoadJumpAnimation.repeatCount = Animation.INFINITE
        // 启动动画
        mLoadView.startAnimation(mLoadAnimationIn)
        mWindView.startAnimation(mWindInAnimation)

        mWindOutAnimation.setAnimationListener(object : LoadAnimationListener(){
            override fun onAnimationEnd(animation: Animation) {
                super.onAnimationEnd(animation)
                mListener.onFinish(0)
                mListener.onFinish(1)
            }
        })

        mLoadAnimationIn.setAnimationListener(object : LoadAnimationListener() {
            override fun onAnimationEnd(animation: Animation) {
                super.onAnimationEnd(animation)
                if (!sStop) {
                    //开始跳
                    mLoadJumpAnimation.setAnimationListener(object : LoadAnimationListener() {
                        override fun onAnimationEnd(animation: Animation) {
                            super.onAnimationEnd(animation)
                            mMsgView.startAnimation(mTextOutAnimation)
                            mLoadView.setImageResource(R.mipmap.ic_bee)
                            mLoadView.startAnimation(mLoadAnimationOut)
                            mWindView.startAnimation(mWindOutAnimation)
                        }
                    })
                    mLoadView.startAnimation(mLoadJumpAnimation)
                    mLoadMiddleAnimation.start()
                    mMsgView.startAnimation(mTextInAnimation)
                    mLoadView.setImageDrawable(mLoadMiddleAnimation)
                } else {
                    //提前结束
                    mLoadView.startAnimation(mLoadAnimationOut)
                    mWindView.startAnimation(mWindOutAnimation)
                }
            }
        })
    }

    fun onStopAnimation(listener : OnFinishListener?) {
        sStop = true
        if (!mLoadJumpAnimation.hasStarted() && !mLoadAnimationIn.hasStarted() &&
                !mLoadAnimationOut.hasStarted()) {
            if (listener != null) {
                listener.onFinish(0)
                listener.onFinish(1)
            }
        }
        if (listener != null) {
            setOnLoadFinishListener(listener)
        }
        mLoadJumpAnimation.repeatCount = 1
        mLoadJumpAnimation.cancel()

    }

    public interface OnFinishListener {
        fun onFinish(status: Int)
    }

    fun setOnLoadFinishListener(listener : OnFinishListener) {
        this.mListener = listener
    }

}