package com.example.speedometer

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import java.lang.StrictMath.min
import kotlin.math.cos
import kotlin.math.sin


class SpeedometerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : View(context, attrs, defStyleAttr, defStyleRes) {

    val max: Int
    var speedProgress: Int

    private var radius = 0.0f                   // Radius of the circle.
    private var handX: Float = 0f
    private var handY: Float = 0f
    private val speedBackgroundColor: Int = ContextCompat.getColor(context, R.color.dark_blue)
    private val speedArcColor: Int = ContextCompat.getColor(context, R.color.light_blue)
    private val lowColor: Int
    private val mediumColor: Int
    private val highColor: Int

    init {
        isClickable = true
        val typedArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.SpeedometerView,
            defStyleAttr,
            defStyleRes
        )

        max = typedArray.getInt(R.styleable.SpeedometerView_max_speed, 100)
        speedProgress = typedArray.getInt(R.styleable.SpeedometerView_speed_value, max/3)

        lowColor = typedArray.getColor(R.styleable.SpeedometerView_low_speed_color, Color.GREEN)
        mediumColor =
            typedArray.getColor(R.styleable.SpeedometerView_medium_speed_color, Color.YELLOW)
        highColor = typedArray.getColor(R.styleable.SpeedometerView_high_speed_color, Color.RED)

        typedArray.recycle()
    }

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

    fun setProgress(speedProgress: Int) {
        this.speedProgress = speedProgress
        invalidate()
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        radius = (min(width, height) * 0.4).toFloat()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val mainRect = RectF(
            (width / 2).toFloat() - radius,
            (height / 2).toFloat() - radius,
            (width / 2).toFloat() + radius,
            (height / 2).toFloat() + radius
        )
        // Draw the dial.
        canvas.drawCircle(mainRect.centerX(), mainRect.centerY(), radius, paint)

        //  Draw arc
        val offset = 50f
        val arcRect = RectF(
            mainRect.left + offset,
            mainRect.top + offset,
            mainRect.right - offset,
            mainRect.bottom - offset
        )
        canvas.drawArc(arcRect, -180f, 180f / max * speedProgress, false, paintArc)

        // Draw the text label
        canvas.drawText("$speedProgress км/ч", mainRect.centerX(), mainRect.centerY() + 100f, paintText)

        // Draw hand
        Log.d("SpeedView", "speedProgress = $speedProgress, max = $max")
        paintHand.color = when(speedProgress) {
            in 0 .. max/3 -> lowColor
            in max/3 .. max*2/3 -> mediumColor
            in max*2/3 .. max -> highColor
            else -> {throw RuntimeException("Error hand color")}
        }
        val handOffset = 50f
        val angle = Math.PI * speedProgress / max - Math.PI  // - Pi because start angle is 180
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
}