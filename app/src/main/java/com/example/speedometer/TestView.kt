package com.example.speedometer

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import java.lang.StrictMath.min
import kotlin.math.cos
import kotlin.math.sin

/**
 * Аннотация @JvmOverloads указывает компилятору Kotlin генерировать перегрузки для этой функции,
 * которые заменяют значения параметров по умолчанию.
 */
class TestView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var radius = 0.0f                   // Radius of the circle.
    private var fanSpeed = FanSpeed.OFF        // The active selection.
    // position variable which will be used to draw label and indicator circle position
    private val pointPosition: PointF = PointF(0.0f, 0.0f)

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
        typeface = Typeface.create( "", Typeface.BOLD)
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        radius = (min(width, height) / 2.0 * 0.8).toFloat()
    }

    private fun PointF.computeXYForSpeed(pos: FanSpeed, radius: Float) {
        // Angles are in radians.
        val startAngle = Math.PI * (9 / 8.0)
        val angle = startAngle + pos.ordinal * (Math.PI / 4)
        x = (radius * cos(angle)).toFloat() + width / 2
        y = (radius * sin(angle)).toFloat() + height / 2
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        paint.color = if (fanSpeed == FanSpeed.OFF) Color.GRAY else Color.GREEN

        // Draw the dial.
        canvas.drawCircle((width / 2).toFloat(), (height / 2).toFloat(), radius, paint)

        // Draw the indicator circle.
//        val markerRadius = radius + RADIUS_OFFSET_INDICATOR
//        pointPosition.computeXYForSpeed(fanSpeed, markerRadius)
//        paint.color = Color.BLACK
//
//        // Draw the text labels.
//        val labelRadius = radius + RADIUS_OFFSET_LABEL
//        for (i in FanSpeed.values()) {
//            pointPosition.computeXYForSpeed(i, labelRadius)
//            val label = resources.getString(i.label)
//            canvas.drawText(label, pointPosition.x, pointPosition.y, paint)
//        }

    }


    companion object {
        const val RADIUS_OFFSET_LABEL = 30
        const val RADIUS_OFFSET_INDICATOR = -35
    }
}

private enum class FanSpeed(val label: Int) {
    OFF(0),
    LOW(1),
    MEDIUM(2),
    HIGH(3);
}


private enum class SpeedValue(val label: Int) {
    ZERO(R.string.speed_zero),
    TEN(R.string.speed_ten),
    TWENTY(R.string.speed_twenty)/*,
    THIRTY(R.string.fan_high),
    FORTY(R.string.fan_high),
    FITHTY(R.string.fan_high),
    SIXTY(R.string.fan_high),
    SEVENTY(R.string.fan_high),
    EIGHTY(R.string.fan_high),
    NINETY(R.string.fan_high),
    HUNDREED(R.string.fan_high)*/
}
