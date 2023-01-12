package com.example.speedometer

import android.content.Context
import android.graphics.*
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import kotlinx.parcelize.Parcelize
import java.lang.StrictMath.max
import java.lang.StrictMath.min
import kotlin.math.cos
import kotlin.math.sin


class SpeedometerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.SpeedometerStyle,
    defStyleRes: Int = R.style.BasicSpeedometerStyle
) : View(context, attrs, defStyleAttr, defStyleRes) {

    val max: Int  // Must be public!
    var speedProgress: Int   // Must be public!

    private var radius = 0.0f                   // Radius of the circle.
    private var handX: Float = 0f
    private var handY: Float = 0f
    private val speedBackgroundColor: Int = ContextCompat.getColor(context, R.color.dark_blue)
    private val speedArcColor: Int = ContextCompat.getColor(context, R.color.light_blue)
    private val lowColor: Int
    private val mediumColor: Int
    private val highColor: Int

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = speedBackgroundColor
    }

    private val paintArc = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = speedArcColor
        style = Paint.Style.STROKE
        strokeWidth = 40f
    }

    private val paintHand = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 10f
    }

    private val paintText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textSize = 60.0f
        textAlign = Paint.Align.CENTER
        typeface = Typeface.DEFAULT_BOLD
    }

    private val mainRect = RectF()

    private val offset = 50f
    private var arcRect = RectF()

    private val maxText: String  // Will be set later
    private var speedProgressText: String
    private val maxTextBounds = Rect()
    private val progressTextBounds = Rect()
    private val handOffset = 50f
    private var angle = 0.0

    private val minWidth: Int
    private val minHeight: Int

    init {
        isClickable = true
        val typedArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.SpeedometerView,
            defStyleAttr,
            defStyleRes
        )

        max = typedArray.getInt(R.styleable.SpeedometerView_max_speed, 100)
        maxText = "max $max км/ч"
        speedProgress = typedArray.getInt(R.styleable.SpeedometerView_speed_value, max / 3)
        speedProgressText = "$speedProgress км/ч"

        paintText.getTextBounds(maxText, 0, maxText.length, maxTextBounds)
        paintText.getTextBounds(speedProgressText, 0, speedProgressText.length, progressTextBounds)

        minWidth = max(maxTextBounds.width(), progressTextBounds.width()) * 6
        minHeight = (maxTextBounds.height() + progressTextBounds.height()) * 6

        Log.v("Speed", " min w $minWidth")
        Log.v("Speed", " min h $minHeight")

        lowColor = typedArray.getColor(R.styleable.SpeedometerView_low_speed_color, Color.GREEN)
        mediumColor =
            typedArray.getColor(R.styleable.SpeedometerView_medium_speed_color, Color.YELLOW)
        highColor = typedArray.getColor(R.styleable.SpeedometerView_high_speed_color, Color.RED)

        typedArray.recycle()
    }

    fun setProgress(speedProgress: Int) {
        this.speedProgress = speedProgress
        invalidate()
    }

    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        return SavedState(superState, speedProgress)
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        state as SavedState
        super.onRestoreInstanceState(state.superSavedState)
        speedProgress = state.progress
        invalidate()   // TODO why?
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        Log.v("Speed", "onMeasure w " + MeasureSpec.toString(widthMeasureSpec))
        Log.v("Speed", "onMeasure h " + MeasureSpec.toString(heightMeasureSpec))

//        val desiredWidth =
//            suggestedMinimumWidth + paddingLeft + paddingRight  // FIXME suggested too small
//        val desiredHeight = suggestedMinimumHeight + paddingTop + paddingBottom

        val desiredWidth =
            max(minWidth, suggestedMinimumWidth) // suggested contains paddings
        val desiredHeight =
            max(minHeight, suggestedMinimumHeight)

        Log.v("Speed", " suggested w $minimumWidth")
        Log.v("Speed", " suggested h $minimumHeight")

        Log.v("Speed", " our w $minWidth")
        Log.v("Speed", " our h $minHeight")

        setMeasuredDimension(  // works same as resolveSize!
            measureDimension(desiredWidth, widthMeasureSpec),
            measureDimension(desiredHeight, heightMeasureSpec)
        )
        Log.d("Speed", "Applied: $measuredWidth $measuredHeight")
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        radius = (min(width, height) * 0.4).toFloat()

        with(mainRect) {  // Initialize mainRect
            left = (width / 2).toFloat() - radius + paddingLeft
            top = (height / 2).toFloat() - radius + paddingTop
            right = (width / 2).toFloat() + radius - paddingRight
            bottom = (height / 2).toFloat() + radius - paddingBottom
        }
        arcRect = RectF(
            mainRect.left + offset,
            mainRect.top + offset,
            mainRect.right - offset,
            mainRect.bottom - offset
        )
        angle = Math.PI * speedProgress / max - Math.PI  // - Pi because start angle is 180
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Draw paddings
        canvas.drawLine(paddingLeft.toFloat(), 0f, paddingLeft.toFloat(), height.toFloat(), paintHand)
        canvas.drawLine(0f, paddingTop.toFloat(),width.toFloat(),  paddingTop.toFloat(), paintHand)
        canvas.drawLine(width - paddingRight.toFloat(), 0f, width - paddingRight.toFloat(), height.toFloat(), paintHand)
        canvas.drawLine(0f, height - paddingBottom.toFloat(), width.toFloat(),  height - paddingBottom.toFloat(), paintHand)


        // Draw the dial.
        canvas.drawCircle(mainRect.centerX(), mainRect.centerY(), radius, paint)

        //  Draw arc
        canvas.drawArc(arcRect, -180f, 180f / max * speedProgress, false, paintArc)

        // Draw the text label
        speedProgressText = "$speedProgress км/ч"
        canvas.drawText(
            speedProgressText,
            mainRect.centerX(),
            mainRect.centerY() + 100f,
            paintText
        )
        paintText.getTextBounds(maxText, 0, maxText.length, maxTextBounds)
        canvas.drawText(
            maxText,
            mainRect.centerX(),
            mainRect.centerY() + 100f + maxTextBounds.height(),
            paintText
        )

        // Draw hand
        Log.d("SpeedView", "speedProgress = $speedProgress, max = $max")
        paintHand.color = when (speedProgress) {
            in 0..max / 3 -> lowColor
            in max / 3..max * 2 / 3 -> mediumColor
            in max * 2 / 3..max -> highColor
            else -> {
                throw RuntimeException("Error hand color")
            }
        }
        angle =
            Math.PI * speedProgress / max - Math.PI  // - Pi because start angle is 180  // Must be calculated every time
        handX = width / 2 + ((radius - handOffset) * cos(angle)).toFloat()
        handY = height / 2 + ((radius - handOffset) * sin(angle)).toFloat()
        canvas.drawLine(
            (width / 2).toFloat(),
            (height / 2).toFloat(),
            handX,
            handY,
            paintHand
        )
    }

    private fun measureDimension(desiredSize: Int, measureSpec: Int): Int {
        val mode = MeasureSpec.getMode(measureSpec)  // get values from encoded spec
        val size = MeasureSpec.getSize(measureSpec)
        var result = when (mode) {
            MeasureSpec.EXACTLY -> {
                size
            }
            MeasureSpec.AT_MOST -> {
                min(size, desiredSize)
            }
            MeasureSpec.UNSPECIFIED -> {
                desiredSize
            }
            else -> {
                desiredSize
            }
        }
        Log.v("Speed", " result $result")
        return result
    }

    @Parcelize   // TODO import
    class SavedState(val superSavedState: Parcelable?, val progress: Int) :
            View.BaseSavedState(superSavedState), Parcelable
}





