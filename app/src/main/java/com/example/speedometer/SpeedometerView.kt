package com.example.speedometer

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import java.lang.StrictMath.min
import kotlin.math.cos
import kotlin.math.sin


class SpeedometerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): View(context, attrs, defStyleAttr) {

    init {
        isClickable = true
    }
    private var speedBackgroundColor: Int = ContextCompat.getColor(context, R.color.dark_blue)
    private var speedArcColor: Int = ContextCompat.getColor(context, R.color.light_blue)

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = speedBackgroundColor
    }

    private val paintText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textSize = 55.0f
        textAlign = Paint.Align.CENTER
    }

    private enum class FanSpeed(val label: Int) {
        OFF(R.string.speed_zero),
        LOW(R.string.speed_ten),
        MEDIUM(R.string.speed_twenty),
        HIGH(R.string.speed_thirty);

        fun next() = when(this) {
            OFF -> LOW
            LOW -> MEDIUM
            MEDIUM -> HIGH
            HIGH -> OFF
        }
    }

    private var radius = 0.0f                   // Radius of the circle.
    private var fanSpeed = FanSpeed.OFF         // The active selection.
    // position variable which will be used to draw label and indicator circle position
    private val pointPosition: PointF = PointF(0.0f, 0.0f)
    

    private fun PointF.computeXYForSpeed(pos: FanSpeed, radius: Float) {
        // Angles are in radians.
        val startAngle = Math.PI * (9 / 8.0)
        val angle = startAngle + pos.ordinal * (Math.PI / 4)
        x = (radius * cos(angle)).toFloat() + width / 2
        y = (radius * sin(angle)).toFloat() + height / 2
    }


    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        radius = (min(width, height) * 0.4).toFloat()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        paint.color = if (fanSpeed == FanSpeed.OFF) Color.GRAY else Color.GREEN

//        canvas.drawCircle((width / 2).toFloat(), (height / 2).toFloat(), (width / 2).toFloat(), paint)

        // Draw the dial.
        canvas.drawCircle((width / 2).toFloat(), (height / 2).toFloat(), radius, paint)

        // Draw the indicator circle.
        val markerRadius = radius + RADIUS_OFFSET_INDICATOR
        pointPosition.computeXYForSpeed(fanSpeed, markerRadius)
        paint.color = Color.BLACK
        canvas.drawCircle(pointPosition.x, pointPosition.y, radius/12, paint)

        // Draw the text labels.
        val labelRadius = radius + RADIUS_OFFSET_LABEL
        for (i in FanSpeed.values()) {
            pointPosition.computeXYForSpeed(i, labelRadius)
//            val label = resources.getString(i.label)  // TODO from res
            val label = resources.getString(i.label)
            canvas.drawText(label, pointPosition.x, pointPosition.y, paintText)
        }
        // Draw arc
        val centerArcOffset: Float = 0.2F * radius
        val rectF = RectF(
            (width / 2).toFloat() - radius + centerArcOffset,
            (height / 2).toFloat() - radius + centerArcOffset,
            (width / 2).toFloat() + radius - centerArcOffset,
            (height / 2).toFloat() + radius - centerArcOffset)
//        paint.color = Color.BLACK
        paint.shader =
            object : LinearGradient((width / 2).toFloat() - radius, (height / 2).toFloat() - radius, height.toFloat(), height.toFloat(), Color.BLUE, Color.RED, Shader.TileMode.MIRROR){}

        canvas.drawArc(rectF, 0F, -180F, true, paint)

    }

    override fun performClick(): Boolean {
        if (super.performClick()) return true

        fanSpeed = fanSpeed.next()
        contentDescription = resources.getString(fanSpeed.label)

        invalidate()
        return true
    }

    companion object {  // Offsets on circle in degrees
        const val RADIUS_OFFSET_LABEL = 30
        const val RADIUS_OFFSET_INDICATOR = -35
    }
}