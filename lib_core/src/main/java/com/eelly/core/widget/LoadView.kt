package com.eelly.core.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.support.annotation.Nullable
import android.util.AttributeSet
import android.view.View

/**
 * @author vurtne on 1-Mar-18.
 */
class LoadView : View {

    private var paintRed : Paint? = null
    private var paintGreen : Paint? = null
    private var paintBlue : Paint? = null
    private var paintYellow : Paint? = null

    private var mWidth = 0
    private var mHeight = 0
    private var mDegress = -3F
    /**
     * 当前偏移量
     * */
    private  var mOffset = 0
    private val MAX_OFF_SET = 280F
    private val RADUYS = 28F

    constructor(context : Context):this(context,null)

    constructor(context: Context, @Nullable attrs : AttributeSet?):this(context,attrs,0)

    constructor(context: Context, @Nullable attrs: AttributeSet?, defStyleAttr : Int):super(context,attrs,defStyleAttr)

    init {
        paintRed = Paint().apply{
            color = Color.parseColor("#AAFF0000")
            isAntiAlias = true
            isDither = true
        }
        paintGreen = Paint().apply{
            color = Color.parseColor("#AA008000")
            isAntiAlias = true
            isDither = true
        }
        paintBlue = Paint().apply{
            color = Color.parseColor("#AAFFFF00")
            isAntiAlias = true
            isDither = true
        }
        paintYellow = Paint().apply{
            color = Color.parseColor("#AAFF0000")
            isAntiAlias = true
            isDither = true
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mHeight = MeasureSpec.getSize(heightMeasureSpec)
        mWidth = MeasureSpec.getSize(widthMeasureSpec)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        mDegress += 3
        canvas?.rotate(mDegress,(mWidth / 2).toFloat(),(mHeight / 2).toFloat())
        val present = mDegress / 360
        mOffset = if (present < 0.5) (MAX_OFF_SET * present).toInt() else (MAX_OFF_SET * (1 - present)).toInt()
        drawCircle(canvas)
        if (mDegress == 360F){
            mDegress = -3F
        }
        invalidate()

    }

    private fun drawCircle(canvas: Canvas?){
        canvas?.drawCircle((mWidth / 2).toFloat(),(mHeight / 2 - mOffset).toFloat(),RADUYS,paintRed)
        canvas?.drawCircle((mWidth / 2).toFloat(),(mHeight / 2 + mOffset).toFloat(),RADUYS,paintGreen)
        canvas?.drawCircle((mWidth / 2 + mOffset).toFloat(),(mHeight / 2).toFloat(),RADUYS,paintBlue)
        canvas?.drawCircle((mWidth / 2 - mOffset).toFloat(),(mHeight / 2).toFloat(),RADUYS,paintYellow)
    }
}
