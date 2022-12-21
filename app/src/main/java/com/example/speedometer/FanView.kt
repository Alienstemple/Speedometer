package com.example.speedometer

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import java.lang.StrictMath.min
import kotlin.math.cos
import kotlin.math.sin

class FanView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.GREEN
    }

    private enum class FanSpeed(val label: Int) {
        OFF(0),
        LOW(1),
        MEDIUM(2),
        HIGH(3);
    }

    private var radius = 0.0f                   // Radius of the circle.
    private var fanSpeed = FanSpeed.OFF         // The active selection.
    // position variable which will be used to draw label and indicator circle position
    private val pointPosition: PointF = PointF(0.0f, 0.0f)

    private val paintFan = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
//        typeface = Typeface.create( "", Typeface.BOLD)   // FIXME crushes all!
    }

    private fun PointF.computeXYForSpeed(pos: FanSpeed, radius: Float) {
        // Angles are in radians.
        val startAngle = Math.PI * (9 / 8.0)
        val angle = startAngle + pos.ordinal * (Math.PI / 4)
        x = (radius * cos(angle)).toFloat() + width / 2
        y = (radius * sin(angle)).toFloat() + height / 2
    }


    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        radius = (min(width, height) / 2.0 * 0.8).toFloat()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        paintFan.color = if (fanSpeed == FanSpeed.OFF) Color.GRAY else Color.GREEN

//        canvas.drawCircle((width / 2).toFloat(), (height / 2).toFloat(), (width / 2).toFloat(), paint)

        // Draw the dial.
        canvas.drawCircle((width / 2).toFloat(), (height / 2).toFloat(), radius, paintFan)

        // Draw the indicator circle.
        val markerRadius = radius + RADIUS_OFFSET_INDICATOR
        pointPosition.computeXYForSpeed(fanSpeed, markerRadius)
        paintFan.color = Color.BLACK
        canvas.drawCircle(pointPosition.x, pointPosition.y, radius/12, paintFan)

        // Draw the text labels.
        val labelRadius = radius + RADIUS_OFFSET_LABEL
        for (i in FanSpeed.values()) {
            pointPosition.computeXYForSpeed(i, labelRadius)
//            val label = resources.getString(i.label)  // TODO from res
            val label = i.ordinal.toString()
            canvas.drawText(label, pointPosition.x, pointPosition.y, paintFan)
        }

    }

    companion object {  // Offsets on circle in degrees
        const val RADIUS_OFFSET_LABEL = 30
        const val RADIUS_OFFSET_INDICATOR = -35
    }
}