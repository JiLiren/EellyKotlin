
package com.eelly.widget

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import com.eelly.R
import java.math.BigDecimal


/**
 * @author Vurtne on 24-Nov-17.
 */
class XRatingBar (context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    private var mClickable: Boolean = false
    private var halfstart: Boolean = false
    private var starCount: Int = 0
    private var starNum: Int = 0
    private var onRatingChangeListener: OnRatingChangeListener? = null
    private var starImageSize: Float = 0.toFloat()
    private var starImageWidth: Float = 0.toFloat()
    private var starImageHeight: Float = 0.toFloat()
    private var starImagePadding: Float = 0.toFloat()
    private var starEmptyDrawable: Drawable? = null
    private var starFillDrawable: Drawable? = null
    private var starHalfDrawable: Drawable? = null
    private var y = 1
    private val isEmpty = true

    fun setStarHalfDrawable(starHalfDrawable: Drawable) {
        this.starHalfDrawable = starHalfDrawable
    }


    fun setOnRatingChangeListener(onRatingChangeListener: OnRatingChangeListener) {
        this.onRatingChangeListener = onRatingChangeListener
    }

    fun setmClickable(clickable: Boolean) {
        this.mClickable = clickable
    }

    fun halfStar(halfstart: Boolean) {
        this.halfstart = halfstart
    }

    fun setStarFillDrawable(starFillDrawable: Drawable) {
        this.starFillDrawable = starFillDrawable
    }

    fun setStarEmptyDrawable(starEmptyDrawable: Drawable) {
        this.starEmptyDrawable = starEmptyDrawable
    }

    fun setStarImageSize(starImageSize: Float) {
        this.starImageSize = starImageSize
    }

    fun setStarImageWidth(starImageWidth: Float) {
        this.starImageWidth = starImageWidth
    }

    fun setStarImageHeight(starImageHeight: Float) {
        this.starImageHeight = starImageHeight
    }


    fun setStarCount(starCount: Int) {
        this.starCount = starCount
    }

    fun setImagePadding(starImagePadding: Float) {
        this.starImagePadding = starImagePadding
    }


    private fun getStarImageView(context: Context, isEmpty: Boolean): ImageView {
        val imageView = ImageView(context)
        val para = ViewGroup.LayoutParams(
                Math.round(starImageWidth),
                Math.round(starImageHeight)
        )
        imageView.setLayoutParams(para)
        imageView.setPadding(0, 0, Math.round(starImagePadding), 0)
        if (isEmpty) {
            imageView.setImageDrawable(starEmptyDrawable)
        } else {
            imageView.setImageDrawable(starFillDrawable)
        }
        return imageView
    }

    fun setStar(starCount: Float) {
        var starCount = starCount

        val fint = starCount.toInt()
        val b1 = BigDecimal(java.lang.Float.toString(starCount))
        val b2 = BigDecimal(Integer.toString(fint))
        val fPoint = b1.subtract(b2).toFloat()


        starCount = (if (fint > this.starCount) this.starCount else fint).toFloat()
        starCount = if (starCount < 0) 0F else starCount

        //drawfullstar
        run {
            var i = 0
            while (i < starCount) {
                (getChildAt(i) as ImageView).setImageDrawable(starFillDrawable)
                ++i
            }
        }

        //drawhalfstar
        if (fPoint > 0) {
            (getChildAt(fint) as ImageView).setImageDrawable(starHalfDrawable)

            //drawemptystar
            var i = this.starCount - 1
            while (i >= starCount + 1) {
                (getChildAt(i) as ImageView).setImageDrawable(starEmptyDrawable)
                --i
            }

        } else {
            //drawemptystar
            var i = this.starCount - 1
            while (i >= starCount) {
                (getChildAt(i) as ImageView).setImageDrawable(starEmptyDrawable)
                --i
            }

        }

    }

    /**
     * change start listener
     */
    interface OnRatingChangeListener {
        fun onRatingChange(RatingCount: Float)
    }

    init {
        orientation = LinearLayout.HORIZONTAL
        val mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.RatingBar)
        starHalfDrawable = mTypedArray.getDrawable(R.styleable.RatingBar_starHalf)
        starEmptyDrawable = mTypedArray.getDrawable(R.styleable.RatingBar_starEmpty)
        starFillDrawable = mTypedArray.getDrawable(R.styleable.RatingBar_starFill)
        starImageSize = mTypedArray.getDimension(R.styleable.RatingBar_starImageSize, 120f)
        starImageWidth = mTypedArray.getDimension(R.styleable.RatingBar_starImageWidth, 60f)
        starImageHeight = mTypedArray.getDimension(R.styleable.RatingBar_starImageHeight, 120f)
        starImagePadding = mTypedArray.getDimension(R.styleable.RatingBar_starImagePadding, 15f)
        starCount = mTypedArray.getInteger(R.styleable.RatingBar_starCount, 5)
        starNum = mTypedArray.getInteger(R.styleable.RatingBar_starNum, 0)
        mClickable = mTypedArray.getBoolean(R.styleable.RatingBar_clickable, true)
        halfstart = mTypedArray.getBoolean(R.styleable.RatingBar_halfstart, false)
        for (i in 0..starNum - 1) {
            val imageView = getStarImageView(context, false)
            addView(imageView)
        }
        for (i in 0..starCount - 1) {
            val imageView = getStarImageView(context, isEmpty)
            imageView.setOnClickListener(
                    object : View.OnClickListener {
                        override fun onClick(v: View) {
                            if (mClickable) {
                                if (halfstart) {
                                    if (y % 2 == 0) {
                                        setStar(indexOfChild(v) + 1f)
                                    } else {
                                        setStar(indexOfChild(v) + 0.5f)
                                    }
                                    if (onRatingChangeListener != null) {
                                        if (y % 2 == 0) {
                                            onRatingChangeListener!!.onRatingChange(indexOfChild(v) + 1f)
                                            y++
                                        } else {
                                            onRatingChangeListener!!.onRatingChange(indexOfChild(v) + 0.5f)
                                            y++
                                        }
                                    }
                                } else {
                                    setStar(indexOfChild(v) + 1f)
                                    if (onRatingChangeListener != null) {
                                        onRatingChangeListener!!.onRatingChange(indexOfChild(v) + 1f)
                                    }
                                }

                            }

                        }
                    }
            )
            addView(imageView)
        }
    }
}