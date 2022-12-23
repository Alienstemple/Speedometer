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
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    init {
        isClickable = true
    }

    private val max: Int = 100
    private var progress: Int = 20
    private var radius = 0.0f                   // Radius of the circle.
    private var handX: Float = 0f
    private var handY: Float = 0f
    private var speedBackgroundColor: Int = ContextCompat.getColor(context, R.color.dark_blue)
    private var speedArcColor: Int = ContextCompat.getColor(context, R.color.light_blue)

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
        color = Color.RED
        style = Paint.Style.STROKE
        strokeWidth = 10f
    }

    private val paintText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textSize = 60.0f
        textAlign = Paint.Align.CENTER
        typeface = Typeface.DEFAULT_BOLD
    }


    private fun PointF.computeXYForSpeed(pos: Int, radius: Float) {
        // Angles are in radians.
        val startAngle = Math.PI * (9 / 8.0)
        val angle = startAngle + pos * (Math.PI / 4)
        x = (radius * cos(angle)).toFloat() + width / 2
        y = (radius * sin(angle)).toFloat() + height / 2
    }

    fun setProgress(progress: Int) {
        this.progress = progress
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
        canvas.drawArc(arcRect, -180f, 180f / max * progress, false, paintArc)

        // Draw the text label
        canvas.drawText("$progress км/ч", mainRect.centerX(), mainRect.centerY(), paintText)

        // Draw hand
//        handX = cos(Math.PI * progress / max).toFloat()
//        handY = sin(Math.PI * progress / max).toFloat()
        handX = cos(Math.PI).toFloat()
        handY = sin(Math.PI).toFloat()
        canvas.drawLine(
            mainRect.centerX(),
            mainRect.centerY(),
            mainRect.centerX() + handX,
            mainRect.centerY() - handY,
            paintHand
        )
        Log.d("SpeedLog", "Cos Pi = ${cos(Math.PI / 4).toFloat()}")
        Log.d("SpeedLog", "${mainRect.centerX()}, ${mainRect.centerY()}, ${mainRect.centerX() + handX}, ${mainRect.centerY() + handY}")
    }
}