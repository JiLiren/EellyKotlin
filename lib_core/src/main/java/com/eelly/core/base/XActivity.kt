package com.eelly.core.base

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import butterknife.ButterKnife
import com.eelly.core.util.DeviceUtil
import com.eelly.core.util.StatusBarUtil
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import java.util.concurrent.TimeUnit

/**
 * @author Vurtne on 19-Nov-17.
 *
 */

open abstract class XActivity : AppCompatActivity(){

    private val DEFAULT_INTERVAL = 1

    protected var context : Context ? =null

    private val mCompositeDisposable: CompositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(contentView())
        context = this
        ButterKnife.bind(this)
        initView()
        if (StatusBarUtil.canStatusChangeColor()) {
            StatusBarUtil.setTranslucentForImageView(this, 0, null, true)
        } else {
            StatusBarUtil.setTranslucentForImageView(this, 40, null!!)
        }
        //沉侵式状态栏适配
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            initStatusBar(DeviceUtil.getStatusBarHeight(this))
        }
        initEvent()
        initData()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!mCompositeDisposable.isDisposed()) {
            mCompositeDisposable.dispose()
        }
    }

    final protected fun getCompositeDisposable(): CompositeDisposable {
        return mCompositeDisposable
    }

    /**
     * 点击事件
     */
    protected fun setDefaultClick(view: View, consumer: Consumer<Any>) {
        mCompositeDisposable.add(RxView.clicks(view).subscribe(consumer))
    }

    /**
     * 带防抖动点击事件
     * 不用设置值，用了自己的默认值

     * @param view     控件
     * *
     * @param consumer 回调事件
     */
    protected fun setClick(view: View, consumer: Consumer<Any>) {
        setClick(view, DEFAULT_INTERVAL, consumer)
    }


    /**
     * 带防抖动点击事件
     * 需要设置点击有效事件范围，默认事件单位是秒

     * @param throttle 点击有效事件，在此事件内点击只实现第一次
     */
    protected fun setClick(view: View, throttle: Int, consumer: Consumer<Any>) {
        setClick(view, throttle, TimeUnit.SECONDS, consumer)
    }

    /**
     * 带防抖动点击事件
     * 需要设置点击有效事件范围、时间单位

     * @param unit 时间单位
     */
    protected fun setClick(view: View, throttle: Int, unit: TimeUnit, consumer: Consumer<Any>) {
        mCompositeDisposable.add(RxView.clicks(view).throttleFirst(throttle.toLong(), unit).subscribe(consumer))
    }

    /**
     * 设置ContentView

     * @return 布局ID
     */
    protected abstract fun contentView(): Int

    /**
     * 界面设置
     */
    protected abstract fun initView()

    /**
     * 状态栏设置
     * 当4.4以上才会调用这个方法
     * @param statusHeight 状态栏高度
     */
    protected abstract fun initStatusBar(statusHeight: Int)

    /**
     * 事件监听
     */
    protected abstract fun initEvent()

    /**
     * 数据处理
     */
    protected abstract fun initData()

}