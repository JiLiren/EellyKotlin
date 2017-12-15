package com.eelly.core.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.support.annotation.ColorInt
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.Transformation
import com.eelly.core.R
import com.eelly.core.util.DeviceUtil
import com.eelly.core.widget.refresh.api.IRefreshHeader
import com.eelly.core.widget.refresh.api.IRefreshKernel
import com.eelly.core.widget.refresh.api.IRefreshLayout
import com.eelly.core.widget.refresh.constant.RefreshState
import com.eelly.core.widget.refresh.constant.SpinnerStyle
import com.eelly.core.widget.refresh.path.PathsDrawable
import java.util.*

/**
 * @author Vurtne 14-Dec-17
 * */
class TaurusHeader : View, IRefreshHeader {

    //<editor-fold desc="Field">
    private val SCALE_START_PERCENT = 0.5f
    private val ANIMATION_DURATION = 1000

    //1.05f;
    private val SIDE_CLOUDS_INITIAL_SCALE = 0.6f
    //1.55f;
    private val SIDE_CLOUDS_FINAL_SCALE = 1f

    //0.8f;
    private val CENTER_CLOUDS_INITIAL_SCALE = 0.8f
    //1.30f;
    private val CENTER_CLOUDS_FINAL_SCALE = 1f

    private val ACCELERATE_DECELERATE_INTERPOLATOR = AccelerateDecelerateInterpolator()

    // Multiply with this animation interpolator time
    private val LOADING_ANIMATION_COEFFICIENT = 80
    private val SLOW_DOWN_ANIMATION_COEFFICIENT = 6
    // Amount of lines when is going lading animation
    private val WIND_SET_AMOUNT = 10
    private val Y_SIDE_CLOUDS_SLOW_DOWN_COF = 4
    private val X_SIDE_CLOUDS_SLOW_DOWN_COF = 2
    private val MIN_WIND_LINE_WIDTH = 50
    private val MAX_WIND_LINE_WIDTH = 300
    private val MIN_WIND_X_OFFSET = 1000
    private val MAX_WIND_X_OFFSET = 2000
    private val RANDOM_Y_COEFFICIENT = 5

    private var mAirplane: PathsDrawable? = null
    private var mCloudCenter: PathsDrawable? = null
    private var mMatrix: Matrix? = null
    private var mPercent: Float = 0.toFloat()
    private var mHeaderHeight: Int = 0
    private var mAnimation: Animation? = null

    private var isRefreshing = false
    private var mLoadingAnimationTime: Float = 0.toFloat()
    private var mLastAnimationTime: Float = 0.toFloat()

    private var mRandom: Random? = null
    private var mEndOfRefreshing: Boolean = false

    //KEY: Y position, Value: X offset of wind
    private var mWinds: MutableMap<Float, Float>? = null
    private var mWindPaint: Paint? = null
    private var mWindLineWidth: Float = 0.toFloat()
    private var mNewWindSet: Boolean = false
    private var mInverseDirection: Boolean = false

    private enum class AnimationPart {
        FIRST,
        SECOND,
        THIRD,
        FOURTH
    }


    //<editor-fold desc="View">
    constructor(context: Context):this(context, null)

    constructor(context: Context, attrs: AttributeSet?):this(context, attrs,0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int): super(context, attrs, defStyleAttr){
        initView(context, attrs)
    }

    private fun initView(context: Context, attrs: AttributeSet?) {
        minimumHeight = DeviceUtil.dip2Px(context,100f)

        mMatrix = Matrix()
        mWinds = HashMap<Float, Float>()
        mRandom = Random()

        mWindPaint = Paint()
        mWindPaint!!.color = -0x1
        mWindPaint!!.strokeWidth = DeviceUtil.dip2Px(context,3f).toFloat()
        mWindPaint!!.alpha = 50

        setupAnimations()
        setupPathDrawable()

        val ta = context.obtainStyledAttributes(attrs, R.styleable.TaurusHeader)

        val primaryColor = ta.getColor(R.styleable.TaurusHeader_thPrimaryColor, 0)
        if (primaryColor != 0) {
            setBackgroundColor(primaryColor)
        } else {
            setBackgroundColor(-0xee4401)
        }

        ta.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(View.resolveSize(suggestedMinimumWidth, widthMeasureSpec),
                View.resolveSize(suggestedMinimumHeight, heightMeasureSpec))
    }

    //</editor-fold>

    //<editor-fold desc="RefreshHeader">
    override fun onInitialized(kernel: IRefreshKernel, height: Int, extendHeight: Int) {

    }

    override fun isSupportHorizontalDrag(): Boolean {
        return false
    }

    override fun onHorizontalDrag(percentX: Float, offsetX: Int, offsetMax: Int) {}

    override fun onPullingDown(percent: Float, offset: Int, headHeight: Int, extendHeight: Int) {
        mPercent = percent
        mEndOfRefreshing = false
        mHeaderHeight = headHeight
    }

    override fun onReleasing(percent: Float, offset: Int, headHeight: Int, extendHeight: Int) {
        mPercent = percent
        mHeaderHeight = headHeight
    }

    override fun onStartAnimator(layout: IRefreshLayout, headHeight: Int, extendHeight: Int) {
        isRefreshing = true
        startAnimation(mAnimation)
    }

    override fun onRefreshReleased(layout: IRefreshLayout, headerHeight: Int, extendHeight: Int) {

    }

    override fun onStateChanged(IRefreshLayout: IRefreshLayout, oldState: RefreshState, newState: RefreshState) {}

    override fun onFinish(layout: IRefreshLayout, success: Boolean): Int {
        isRefreshing = false
        mEndOfRefreshing = true
        clearAnimation()
        return 0
    }

    @Deprecated("")
    override fun setPrimaryColors(@ColorInt colors: IntArray) {
        setBackgroundColor(colors[0])
    }

    override fun getView(): View {
        return this
    }

    override fun getSpinnerStyle(): SpinnerStyle {
        return SpinnerStyle.Scale
    }
    //</editor-fold>

    //<editor-fold desc="draw">
    public override fun onDraw(canvas: Canvas) {
        val width = width
        val height = height
        if (isRefreshing) {
            // Set up new set of wind
            while (mWinds!!.size < WIND_SET_AMOUNT) {
                var y = (mHeaderHeight / (Math.random() * RANDOM_Y_COEFFICIENT)).toFloat()
                val x = random(MIN_WIND_X_OFFSET, MAX_WIND_X_OFFSET)

                // Magic with checking interval between winds
                if (mWinds!!.size > 1) {
                    y = 0f
                    while (y == 0f) {
                        val tmp = (mHeaderHeight / (Math.random() * RANDOM_Y_COEFFICIENT)).toFloat()

                        for ((key) in mWinds!!) {
                            // We want that interval will be greater than fifth part of draggable distance
                            if (Math.abs(key - tmp) > mHeaderHeight / RANDOM_Y_COEFFICIENT) {
                                y = tmp
                            } else {
                                y = 0f
                                break
                            }
                        }
                    }
                }

                mWinds!!.put(y, x)
                drawWind(canvas, y, x, width)
            }

            // Draw current set of wind
            if (mWinds!!.size >= WIND_SET_AMOUNT) {
                for ((key, value) in mWinds!!) {
                    drawWind(canvas, key, value, width)
                }
            }

            // We should to create new set of winds
            if (mInverseDirection && mNewWindSet) {
                mWinds!!.clear()
                mNewWindSet = false
                mWindLineWidth = random(MIN_WIND_LINE_WIDTH, MAX_WIND_LINE_WIDTH)
            }

            // needed for checking direction
            mLastAnimationTime = mLoadingAnimationTime
        }
        drawAirplane(canvas, width, height)
        drawSideClouds(canvas, width, height)
        drawCenterClouds(canvas, width, height)
    }

    /**
     * Draw wind on loading animation
     *
     * @param canvas  - area where we will draw
     * @param y       - y position fot one of lines
     * @param xOffset - x offset for on of lines
     */
    private fun drawWind(canvas: Canvas, y: Float, xOffset: Float, width: Int) {
        /* We should multiply current animation time with this coefficient for taking all screen width in time
        Removing slowing of animation with dividing on {@LINK #SLOW_DOWN_ANIMATION_COEFFICIENT}
        And we should don't forget about distance that should "fly" line that depend on screen of device and x offset
        */
        val cof = (width + xOffset) / (LOADING_ANIMATION_COEFFICIENT / SLOW_DOWN_ANIMATION_COEFFICIENT)
        var time = mLoadingAnimationTime

        // HORRIBLE HACK FOR REVERS ANIMATION THAT SHOULD WORK LIKE RESTART ANIMATION
        if (mLastAnimationTime - mLoadingAnimationTime > 0) {
            mInverseDirection = true
            // take time from 0 to end of animation time
            time = LOADING_ANIMATION_COEFFICIENT / SLOW_DOWN_ANIMATION_COEFFICIENT - mLoadingAnimationTime
        } else {
            mNewWindSet = true
            mInverseDirection = false
        }

        // Taking current x position of drawing wind
        // For fully disappearing of line we should subtract wind line width
        val x = width - time * cof + xOffset - mWindLineWidth
        val xEnd = x + mWindLineWidth

        canvas.drawLine(x, y, xEnd, y, mWindPaint)
    }

    private fun drawSideClouds(canvas: Canvas, width: Int, height: Int) {
        val matrix = mMatrix
        matrix!!.reset()

        val mCloudLeft = mCloudCenter
        val mCloudRight = mCloudCenter

        // Drag percent will newer get more then 1 here
        var dragPercent = Math.min(1f, Math.abs(mPercent))

        if (isInEditMode) {
            dragPercent = 1f
            mHeaderHeight = height
        }

        val scale: Float
        val scalePercentDelta = dragPercent - SCALE_START_PERCENT
        if (scalePercentDelta > 0) {
            val scalePercent = scalePercentDelta / (1.0f - SCALE_START_PERCENT)
            scale = SIDE_CLOUDS_INITIAL_SCALE + (SIDE_CLOUDS_FINAL_SCALE - SIDE_CLOUDS_INITIAL_SCALE) * scalePercent
        } else {
            scale = SIDE_CLOUDS_INITIAL_SCALE
        }

        // Current y position of clouds
        val dragYOffset = mHeaderHeight * (1.0f - dragPercent)

        // Position where clouds fully visible on screen and we should drag them with content of listView
        //        int cloudsVisiblePosition = mHeaderHeight / 2 - mCloudCenter.height() / 2;

        //        boolean needMoveCloudsWithContent = false;
        //        if (dragYOffset < cloudsVisiblePosition) {
        //            needMoveCloudsWithContent = true;
        //        }

        var offsetLeftX = (0 - mCloudLeft!!.width() / 2).toFloat()
        var offsetLeftY = dragYOffset

        var offsetRightX = (width - mCloudRight!!.width() / 2).toFloat()
        var offsetRightY = dragYOffset

        // Magic with animation on loading process
        if (isRefreshing) {
            when {
                checkCurrentAnimationPart(AnimationPart.FIRST) -> {
                    offsetLeftX -= 2 * getAnimationPartValue(AnimationPart.FIRST) / Y_SIDE_CLOUDS_SLOW_DOWN_COF
                    offsetRightX += getAnimationPartValue(AnimationPart.FIRST) / X_SIDE_CLOUDS_SLOW_DOWN_COF
                }
                checkCurrentAnimationPart(AnimationPart.SECOND) -> {
                    offsetLeftX -= 2 * getAnimationPartValue(AnimationPart.SECOND) / Y_SIDE_CLOUDS_SLOW_DOWN_COF
                    offsetRightX += getAnimationPartValue(AnimationPart.SECOND) / X_SIDE_CLOUDS_SLOW_DOWN_COF
                }
                checkCurrentAnimationPart(AnimationPart.THIRD) -> {
                    offsetLeftX -= getAnimationPartValue(AnimationPart.THIRD) / Y_SIDE_CLOUDS_SLOW_DOWN_COF
                    offsetRightX += 2 * getAnimationPartValue(AnimationPart.THIRD) / X_SIDE_CLOUDS_SLOW_DOWN_COF
                }
                checkCurrentAnimationPart(AnimationPart.FOURTH) -> {
                    offsetLeftX -= getAnimationPartValue(AnimationPart.FOURTH) / X_SIDE_CLOUDS_SLOW_DOWN_COF
                    offsetRightX += 2 * getAnimationPartValue(AnimationPart.FOURTH) / Y_SIDE_CLOUDS_SLOW_DOWN_COF
                }
            }
        }

        if (offsetLeftY + scale * mCloudLeft!!.height() < height + 2) {
            offsetLeftY = height + 2 - scale * mCloudLeft!!.height()
        }
        if (offsetRightY + scale * mCloudRight!!.height() < height + 2) {
            offsetRightY = height + 2 - scale * mCloudRight!!.height()
        }

        val saveCount = canvas.saveCount
        canvas.save()
        canvas.translate(offsetLeftX, offsetLeftY)
        matrix.postScale(scale, scale, (mCloudLeft.width() * 3 / 4).toFloat(), mCloudLeft.height().toFloat())
        canvas.concat(matrix)
        mCloudLeft.alpha = 100
        mCloudLeft.draw(canvas)
        mCloudLeft.alpha = 255
        canvas.restoreToCount(saveCount)
        canvas.save()
        canvas.translate(offsetRightX, offsetRightY)
        matrix.postScale(scale, scale, 0f, mCloudRight.height().toFloat())
        canvas.concat(matrix)
        mCloudRight.alpha = 100
        mCloudRight.draw(canvas)
        mCloudRight.alpha = 255
        canvas.restoreToCount(saveCount)
    }

    private fun drawCenterClouds(canvas: Canvas, width: Int, height: Int) {
        val matrix = mMatrix
        matrix!!.reset()
        var dragPercent = Math.min(1f, Math.abs(mPercent))

        if (isInEditMode) {
            dragPercent = 1f
            mHeaderHeight = height
        }

        val scale: Float
        var overdragPercent = 0f
        var overdrag = false

        if (mPercent > 1.0f) {
            overdrag = true
            // Here we want know about how mach percent of over drag we done
            overdragPercent = Math.abs(1.0f - mPercent)
        }

        val scalePercentDelta = dragPercent - SCALE_START_PERCENT
        scale = if (scalePercentDelta > 0) {
            val scalePercent = scalePercentDelta / (1.0f - SCALE_START_PERCENT)
            CENTER_CLOUDS_INITIAL_SCALE + (CENTER_CLOUDS_FINAL_SCALE -
                    CENTER_CLOUDS_INITIAL_SCALE) * scalePercent
        } else {
            CENTER_CLOUDS_INITIAL_SCALE
        }

        var parallaxPercent = 0f
        var parallax = false
        // Current y position of clouds
        val dragYOffset = mHeaderHeight * dragPercent
        // Position when should start parallax scrolling
        val startParallaxHeight = mHeaderHeight - mCloudCenter!!.height() / 2

        if (dragYOffset > startParallaxHeight) {
            parallax = true
            parallaxPercent = dragYOffset - startParallaxHeight
        }

        val offsetX = (width / 2 - mCloudCenter!!.width() / 2).toFloat()
        val dy :Float = if (parallax) mCloudCenter!!.height() / 2 + parallaxPercent else
            (mCloudCenter!!.height() / 2).toFloat()
        var offsetY = dragYOffset - dy

        var sx = if (overdrag) scale + overdragPercent / 4 else scale
        var sy = if (overdrag) scale + overdragPercent / 2 else scale

        if (isRefreshing && !overdrag) {
            when {
                checkCurrentAnimationPart(AnimationPart.FIRST) -> sx = scale - getAnimationPartValue(AnimationPart.FIRST) / LOADING_ANIMATION_COEFFICIENT / 8
                checkCurrentAnimationPart(AnimationPart.SECOND) -> sx = scale - getAnimationPartValue(AnimationPart.SECOND) / LOADING_ANIMATION_COEFFICIENT / 8
                checkCurrentAnimationPart(AnimationPart.THIRD) -> sx = scale + getAnimationPartValue(AnimationPart.THIRD) / LOADING_ANIMATION_COEFFICIENT / 6
                checkCurrentAnimationPart(AnimationPart.FOURTH) -> sx = scale + getAnimationPartValue(AnimationPart.FOURTH) / LOADING_ANIMATION_COEFFICIENT / 6
            }
            sy = sx
        }


        matrix.postScale(sx, sy, (mCloudCenter!!.width() / 2).toFloat(), 0f)

        if (offsetY + sy * mCloudCenter!!.height() < height + 2) {
            offsetY = height + 2 - sy * mCloudCenter!!.height()
        }

        val saveCount = canvas.saveCount
        canvas.save()
        canvas.translate(offsetX, offsetY)
        canvas.concat(matrix)
        mCloudCenter!!.draw(canvas)
        canvas.restoreToCount(saveCount)
    }

    private fun drawAirplane(canvas: Canvas, width: Int, height: Int) {
        val matrix = mMatrix
        matrix!!.reset()

        var dragPercent = mPercent
        var rotateAngle = 0f

        if (isInEditMode) {
            dragPercent = 1f
            mHeaderHeight = height
        }

        // Check overdrag
        if (dragPercent > 1.0f) {
            rotateAngle = 20 * (1 - Math.pow(100.0, (-(dragPercent - 1) / 2).toDouble())).toFloat()
            dragPercent = 1.0f
        }/*&& !mEndOfRefreshing*/

        var offsetX = width * dragPercent / 2 - mAirplane!!.width() / 2
        var offsetY = mHeaderHeight * (1 - dragPercent / 2) - mAirplane!!.height() / 2

        if (mEndOfRefreshing) {
            offsetX = width / 2 + width * (1 - dragPercent) / 2 - mAirplane!!.width() / 2
            offsetY = dragPercent * (mHeaderHeight / 2 + mAirplane!!.height() * 3 / 2) - 2 * mAirplane!!.height()
        }

        if (isRefreshing) {
            when {
                checkCurrentAnimationPart(AnimationPart.FIRST) -> offsetY -= getAnimationPartValue(AnimationPart.FIRST)
                checkCurrentAnimationPart(AnimationPart.SECOND) -> offsetY -= getAnimationPartValue(AnimationPart.SECOND)
                checkCurrentAnimationPart(AnimationPart.THIRD) -> offsetY += getAnimationPartValue(AnimationPart.THIRD)
                checkCurrentAnimationPart(AnimationPart.FOURTH) -> offsetY += getAnimationPartValue(AnimationPart.FOURTH)
            }
        }

        if (rotateAngle > 0) {
            matrix.postRotate(rotateAngle,
                    (mAirplane!!.width() / 2).toFloat(),
                    (mAirplane!!.height() / 2).toFloat())
        }

        val saveCount = canvas.saveCount
        canvas.save()
        canvas.translate(offsetX, offsetY)
        canvas.concat(matrix)
        mAirplane!!.draw(canvas)
        canvas.restoreToCount(saveCount)
    }
    //</editor-fold>

    //    @Override
//    public int defineHeight() {
//        return (int)(Resources.getSystem().getDisplayMetrics().widthPixels * 0.3);
//    }
//
//    @Override
//    public int defineExtendHeight() {
//        return (int) (defineHeight() * 0.3f);
//    }

    //<editor-fold desc="private">
    private fun setupPathDrawable() {
        mAirplane = PathsDrawable()
        //mAirplane.parserPaths("M60.68,16.15l-0.13,-0.11c-1.09,-0.76 -2.63,-1.16 -4.47,-1.16c-2.92,0 -5.95,0.99 -7.32,1.92l-10.76,7.35l-20.97,4.45c-0.18,0.04 -0.35,0.11 -0.51,0.22c-0.41,0.28 -0.64,0.76 -0.62,1.25c0.04,0.71 0.58,1.27 1.28,1.34l8.87,0.89l-8.65,5.9c-2.57,-1.18 -5.02,-2.33 -7.27,-3.4c-3.48,-1.67 -5.76,-1.96 -6.83,-0.89c-1.11,1.11 -0.39,3.02 0.01,3.6l8.33,10.8c0.28,0.41 0.6,0.64 0.99,0.71c0.64,0.11 1.2,-0.27 1.78,-0.68l2.11,-1.45l11.72,-5.69l-1.71,6.12c-0.19,0.68 0.14,1.38 0.78,1.68c0.18,0.08 0.39,0.13 0.59,0.13c0.29,0 0.57,-0.09 0.81,-0.25c0.16,-0.1 0.28,-0.23 0.38,-0.39l6.7,-10.19l4.1,-4.8L58.08,21.08c0.28,-0.19 0.55,-0.36 0.82,-0.54c0.63,-0.4 1.22,-0.78 1.65,-1.21C61.47,18.41 61.52,17.39 60.68,16.15z");
        mAirplane!!.parserPaths("m23.01,81.48c-0.21,-0.3 -0.38,-0.83 -0.38,-1.19 0,-0.55 0.24,-0.78 1.5,-1.48 1.78,-0.97 2.62,-1.94 2.24,-2.57 -0.57,-0.93 -1.97,-1.24 -11.64,-2.59 -5.35,-0.74 -10.21,-1.44 -10.82,-1.54l-1.09,-0.18 1.19,-0.91c0.99,-0.76 1.38,-0.91 2.35,-0.91 0.64,0 6.39,0.33 12.79,0.74 6.39,0.41 12.09,0.71 12.65,0.67l1.03,-0.07 -1.24,-2.19C30.18,66.77 15.91,42 15.13,40.68l-0.51,-0.87 4.19,-1.26c2.3,-0.69 4.27,-1.26 4.37,-1.26 0.1,0 5.95,3.85 13,8.55 14.69,9.81 17.1,11.31 19.7,12.31 4.63,1.78 6.45,1.69 12.94,-0.64 13.18,-4.73 25.22,-9.13 25.75,-9.4 0.69,-0.36 3.6,1.33 -24.38,-14.22L50.73,23.07 46.74,16.42 42.75,9.77 43.63,8.89c0.83,-0.83 0.91,-0.86 1.46,-0.52 0.32,0.2 3.72,3.09 7.55,6.44 3.83,3.34 7.21,6.16 7.5,6.27 0.29,0.11 13.6,2.82 29.58,6.03 15.98,3.21 31.86,6.4 35.3,7.1l6.26,1.26 3.22,-1.13c41.63,-14.63 67.88,-23.23 85.38,-28 14.83,-4.04 23.75,-4.75 32.07,-2.57 7.04,1.84 9.87,4.88 7.71,8.27 -1.6,2.5 -4.6,4.63 -10.61,7.54 -5.94,2.88 -10.22,4.46 -25.4,9.41 -8.15,2.66 -16.66,5.72 -39.01,14.02 -66.79,24.82 -88.49,31.25 -121.66,36.07 -14.56,2.11 -24.17,2.95 -34.08,2.95 -5.43,0 -5.52,-0.01 -5.89,-0.54z")
        mAirplane!!.setBounds(0, 0, DeviceUtil.dip2Px(context, 65f),
                DeviceUtil.dip2Px(context, 20f))
        mAirplane!!.parserColors(-0x1)

        mCloudCenter = PathsDrawable()
        mCloudCenter!!.parserPaths(
                "M551.81,1.01A65.42,65.42 0,0 0,504.38 21.5A50.65,50.65 0,0 0,492.4 20A50.65,50.65 0,0 0,441.75 70.65A50.65,50.65 0,0 0,492.4 121.3A50.65,50.65 0,0 0,511.22 117.64A65.42,65.42 0,0 0,517.45 122L586.25,122A65.42,65.42 0,0 0,599.79 110.78A59.79,59.79 0,0 0,607.81 122L696.34,122A59.79,59.79 0,0 0,711.87 81.9A59.79,59.79 0,0 0,652.07 22.11A59.79,59.79 0,0 0,610.93 38.57A65.42,65.42 0,0 0,551.81 1.01zM246.2,1.71A54.87,54.87 0,0 0,195.14 36.64A46.78,46.78 0,0 0,167.77 27.74A46.78,46.78 0,0 0,120.99 74.52A46.78,46.78 0,0 0,167.77 121.3A46.78,46.78 0,0 0,208.92 96.74A54.87,54.87 0,0 0,246.2 111.45A54.87,54.87 0,0 0,268.71 106.54A39.04,39.04 0,0 0,281.09 122L327.6,122A39.04,39.04 0,0 0,343.38 90.7A39.04,39.04 0,0 0,304.34 51.66A39.04,39.04 0,0 0,300.82 51.85A54.87,54.87 0,0 0,246.2 1.71z",
                "m506.71,31.37a53.11,53.11 0,0 0,-53.11 53.11,53.11 53.11,0 0,0 15.55,37.5h75.12a53.11,53.11 0,0 0,1.88 -2.01,28.49 28.49,0 0,0 0.81,2.01h212.96a96.72,96.72 0,0 0,-87.09 -54.85,96.72 96.72,0 0,0 -73.14,33.52 28.49,28.49 0,0 0,-26.74 -18.74,28.49 28.49,0 0,0 -13.16,3.23 53.11,53.11 0,0 0,0.03 -0.66,53.11 53.11,0 0,0 -53.11,-53.11zM206.23,31.81a53.81,53.81 0,0 0,-49.99 34.03,74.91 74.91,0 0,0 -47.45,-17 74.91,74.91 0,0 0,-73.54 60.82,31.3 31.3,0 0,0 -10.17,-1.73 31.3,31.3 0,0 0,-26.09 14.05L300.86,121.98a37.63,37.63 0,0 0,0.2 -3.85,37.63 37.63,0 0,0 -37.63,-37.63 37.63,37.63 0,0 0,-3.65 0.21,53.81 53.81,0 0,0 -53.54,-48.9z",
                "m424.05,36.88a53.46,53.46 0,0 0,-40.89 19.02,53.46 53.46,0 0,0 -1.34,1.76 62.6,62.6 0,0 0,-5.39 -0.27,62.6 62.6,0 0,0 -61.36,50.17 62.6,62.6 0,0 0,-0.53 3.51,15.83 15.83,0 0,0 -10.33,-3.84 15.83,15.83 0,0 0,-8.06 2.23,21.1 21.1,0 0,0 -18.31,-10.67 21.1,21.1 0,0 0,-19.47 12.97,21.81 21.81,0 0,0 -6.56,-1.01 21.81,21.81 0,0 0,-19.09 11.32L522.84,122.07a43.61,43.61 0,0 0,-43.11 -37.35,43.61 43.61,0 0,0 -2.57,0.09 53.46,53.46 0,0 0,-53.11 -47.93zM129.08,38.4a50.29,50.29 0,0 0,-50.29 50.29,50.29 50.29,0 0,0 2.37,15.06 15.48,15.83 0,0 0,-5.87 1.68,15.48 15.83,0 0,0 -0.98,0.58 16.53,16.18 0,0 0,-0.19 -0.21,16.53 16.18,0 0,0 -11.86,-4.91 16.53,16.18 0,0 0,-16.38 14.13,20.05 16.18,0 0,0 -14.97,7.04L223.95,122.07a42.56,42.56 0,0 0,1.14 -9.56,42.56 42.56,0 0,0 -42.56,-42.56 42.56,42.56 0,0 0,-6.58 0.54,50.29 50.29,0 0,0 -0,-0.01 50.29,50.29 0,0 0,-46.88 -32.07zM631.67,82.61a64.01,64.01 0,0 0,-44.9 18.42,26.73 26.73,0 0,0 -10.67,-2.24 26.73,26.73 0,0 0,-22.72 12.71,16.88 16.88,0 0,0 -0.25,-0.12 16.88,16.88 0,0 0,-6.57 -1.33,16.88 16.88,0 0,0 -16.15,12.03h160.36a64.01,64.01 0,0 0,-59.1 -39.46z"
        )
        //        mCloudCenter.parserColors(0xfffdfdfd,0xffe8f3fd,0xffc7dcf1);
        mCloudCenter!!.parserColors(-0x5538230f, -0x22170c03, -0x20203)
        mCloudCenter!!.setBounds(0, 0, DeviceUtil.dip2Px(context, 260f),
                DeviceUtil.dip2Px(context, 45f))

    }

    private fun random(min: Int, max: Int): Float {

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        return (mRandom!!.nextInt(max - min + 1) + min).toFloat()
    }

    /**
     * We need a special value for different part of animation
     *
     * @param part - needed part
     * @return - value for needed part
     */
    private fun getAnimationPartValue(part: AnimationPart): Float {
        return when (part) {
            TaurusHeader.AnimationPart.FIRST -> {
                mLoadingAnimationTime
            }
            TaurusHeader.AnimationPart.SECOND -> {
                getAnimationTimePart(AnimationPart.FOURTH) - (mLoadingAnimationTime - getAnimationTimePart(AnimationPart.FOURTH))
            }
            TaurusHeader.AnimationPart.THIRD -> {
                mLoadingAnimationTime - getAnimationTimePart(AnimationPart.SECOND)
            }
            TaurusHeader.AnimationPart.FOURTH -> {
                getAnimationTimePart(AnimationPart.THIRD) - (mLoadingAnimationTime - getAnimationTimePart(AnimationPart.FOURTH))
            }
            else -> 0f
        }
    }

    /**
     * On drawing we should check current part of animation
     *
     * @param part - needed part of animation
     * @return - return true if current part
     */
    private fun checkCurrentAnimationPart(part: AnimationPart): Boolean {
        return when (part) {
            TaurusHeader.AnimationPart.FIRST -> {
                mLoadingAnimationTime < getAnimationTimePart(AnimationPart.FOURTH)
            }
            TaurusHeader.AnimationPart.SECOND, TaurusHeader.AnimationPart.THIRD -> {
                mLoadingAnimationTime < getAnimationTimePart(part)
            }
            TaurusHeader.AnimationPart.FOURTH -> {
                mLoadingAnimationTime > getAnimationTimePart(AnimationPart.THIRD)
            }
            else -> false
        }
    }

    /**
     * Get part of animation duration
     *
     * @param part - needed part of time
     * @return - interval of time
     */
    private fun getAnimationTimePart(part: AnimationPart): Int {
        return when (part) {
            TaurusHeader.AnimationPart.SECOND -> {
                LOADING_ANIMATION_COEFFICIENT / 2
            }
            TaurusHeader.AnimationPart.THIRD -> {
                getAnimationTimePart(AnimationPart.FOURTH) * 3
            }
            TaurusHeader.AnimationPart.FOURTH -> {
                LOADING_ANIMATION_COEFFICIENT / 4
            }
            else -> 0
        }
    }

    private fun setupAnimations() {
        mAnimation = object : Animation() {
            public override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                setLoadingAnimationTime(interpolatedTime)
            }
        }
        mAnimation!!.repeatCount = Animation.INFINITE
        mAnimation!!.repeatMode = Animation.REVERSE
        mAnimation!!.interpolator = ACCELERATE_DECELERATE_INTERPOLATOR
        mAnimation!!.duration = ANIMATION_DURATION.toLong()
    }

    private fun setLoadingAnimationTime(loadingAnimationTime: Float) {
        /**SLOW DOWN ANIMATION IN [.SLOW_DOWN_ANIMATION_COEFFICIENT] time  */
        mLoadingAnimationTime = LOADING_ANIMATION_COEFFICIENT * (loadingAnimationTime / SLOW_DOWN_ANIMATION_COEFFICIENT)
        invalidate()
    }
    //</editor-fold>
}