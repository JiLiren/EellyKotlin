package com.eelly.core.base

import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.CompoundButton
import android.widget.ListAdapter
import butterknife.ButterKnife
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxAdapterView
import com.jakewharton.rxbinding2.widget.RxCompoundButton
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import java.util.concurrent.TimeUnit

/**
 * @author Vurtne on 19-Nov-17.
 */
/**
 * Created by Vurtne on 20-Oct-17.

 */

abstract class XFragment: Fragment() {

    private val DEFAULT_INTERVAL = 1


    private var mView: View? = null
    private val mCompositeDisposable: CompositeDisposable = CompositeDisposable()

    private var mArgs: Bundle? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(contentView(), container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mView = view
        ButterKnife.bind(this, mView!!)
        initView()
        //沉侵式状态栏适配
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            initStatusBar()
        }
        initData()
        initEvent()
    }


    override fun getView(): View? {
        return mView
    }

    override fun setArguments(args: Bundle?) {
        super.setArguments(args)
        mArgs = Bundle(args)
    }

    var args: Bundle?
        get() {
            if (mArgs == null) {
                if (arguments != null) {
                    return arguments
                }
            }
            return mArgs
        }
        set(args) {
            if (mArgs == null) {
                mArgs = Bundle()
            }
            if (args != null) {
                mArgs!!.putAll(args)
            }
        }

    protected fun getCompositeDisposable(): CompositeDisposable {
        return mCompositeDisposable
    }
    /**
     * 点击事件
     */
    protected fun setDefualtClick(view: View, consumer: Consumer<Any>) {
        mCompositeDisposable.add(RxView.clicks(view).subscribe(consumer))
    }

    /**
     * 带防抖动点击事件
     * 不用设置值，用了自己的默认值
     * @param view     控件
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
     * 长按事件
     * @param view     控件
     * @param consumer 回调事件
     */
    protected fun setLongClick(view: View, consumer: Consumer<Any>) {
        mCompositeDisposable.add(RxView.longClicks(view).subscribe(consumer))
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!mCompositeDisposable.isDisposed) {
            mCompositeDisposable.dispose()
        }
    }

    /**
     * item的长按事件
     * @param listView listView 和 GridView
     * @param consumer 回调事件
     */
    protected fun setListLongClick(listView: AbsListView, consumer: Consumer<Int>) {
        mCompositeDisposable.add(RxAdapterView.itemLongClicks<ListAdapter>(listView).subscribe(consumer))
    }

    /**
     * CompoundButton 状态改变监听
     * @param checkBox 需要监听的控件 ，不建议传RadioButton(可以用RxRadioGroup.checkedChanges来写)
     * @param consumer 回调事件
     */
    protected fun setCheck(checkBox: CompoundButton, consumer: Consumer<Boolean>) {
        mCompositeDisposable.add(RxCompoundButton.checkedChanges(checkBox).subscribe(consumer))
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
     */
    protected abstract fun initStatusBar()

    /**
     * 事件监听
     */
    protected abstract fun initEvent()

    /**
     * 数据处理
     */
    protected abstract fun initData()


}
