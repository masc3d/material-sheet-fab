/*
Copyright 2015 Sunghoon Kang (devholic@plusquare.com)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package sx.android.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

import sx.android.R
import kotlin.properties.Delegates

/**
 * Clock view
 */
class ClockView : View {
    private val p = Paint(Paint.ANTI_ALIAS_FLAG)

    /** Clock hour */
    var hour: Int by Delegates.observable(0, { _, o, v ->
        if (o != v) this.invalidate()
    })

    /** Clock minutes */
    var minute: Int by Delegates.observable(0, { _, o, v ->
        if (o != v) this.invalidate()
    })

    /** Clock color */
    var color: Int by Delegates.observable(0, { _, o, v ->
        if (o != v) this.invalidate()
    })

    constructor(context: Context) : super(context) {
        color = Color.parseColor("#F44336")
        hour = 17
        minute = 0
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        val arr = context.obtainStyledAttributes(attrs, R.styleable.ClockView)
        color = arr.getColor(R.styleable.ClockView_color, Color.parseColor("#F44336"))
        hour = arr.getInteger(R.styleable.ClockView_hour, 17)
        minute = arr.getInteger(R.styleable.ClockView_minute, 0)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(measuredWidth, measuredHeight)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val center_w = measuredWidth / 2
        val center_h = measuredHeight / 2
        val len = measuredWidth / 2 - 3
        p.style = Paint.Style.FILL
        p.color = color
        canvas.drawCircle(center_w.toFloat(), center_h.toFloat(), len.toFloat(), p)
        p.color = Color.WHITE
        canvas.drawCircle(center_w.toFloat(), center_h.toFloat(), (len * 0.8).toInt().toFloat(), p)
        p.style = Paint.Style.STROKE
        p.strokeWidth = (len * 0.2).toInt().toFloat()
        p.strokeJoin = Paint.Join.ROUND
        p.strokeCap = Paint.Cap.ROUND
        p.color = color
        //p.setColor(getResources().getColor(R.color.textColorSecondary));
        canvas.drawLine(center_w.toFloat(), center_h.toFloat(), center_w + getMinuteX(len), center_h + getMinuteY(len), p)
        //p.setColor(getResources().getColor(R.color.textColorPrimary));
        canvas.drawLine(center_w.toFloat(), center_h.toFloat(), center_w + getHourX(len), center_h + getHourY(len), p)
    }

    private fun getMinuteX(l: Int): Float {
        val c: Int = if (minute < 15) {
            minute + 45
        } else {
            minute - 15
        }

        val angle = Math.toRadians((c * 6).toDouble())
        return (0.6 * l.toDouble() * Math.cos(angle)).toFloat()
    }

    private fun getMinuteY(l: Int): Float {
        val c = if (minute < 15) {
            minute + 45
        } else {
            minute - 15
        }
        
        val angle = Math.toRadians((c * 6).toDouble())
        return (0.6 * l.toDouble() * Math.sin(angle)).toFloat()
    }

    private fun getHourX(l: Int): Float {
        val angle = Math.toRadians(((hour * 60 + minute) / 2 - 90).toDouble())
        return (0.4 * l.toDouble() * Math.cos(angle)).toFloat()
    }

    private fun getHourY(l: Int): Float {
        val angle = Math.toRadians(((hour * 60 + minute) / 2 - 90).toDouble())
        return (0.4 * l.toDouble() * Math.sin(angle)).toFloat()
    }
}
