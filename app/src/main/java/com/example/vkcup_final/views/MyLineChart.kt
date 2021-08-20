package com.example.vkcup_final.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.example.vkcup_final.R
import com.example.vkcup_final.modules.EpisodeStat

class MyLineChart : View {

    private lateinit var ctx: Context
    private var stats: EpisodeStat? = null

    private lateinit var paint: Paint

    private var yLabels = 7
    private var xLabels = 8
    private var fontSize = 20f

    constructor(ctx: Context?) : super(ctx) {
        this.ctx = ctx!!
        init()
    }

    constructor(ctx: Context?, attrs: AttributeSet?) : super(ctx!!, attrs!!) {
        this.ctx = ctx
        init()
    }

    fun update(stats: EpisodeStat, mode: String){
        this.stats = stats

        invalidate()
    }

    fun init(){
        paint = Paint(Paint.ANTI_ALIAS_FLAG)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = 250
        val desiredHeight: Int = 100
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val width: Int
        val height: Int
        width = if (widthMode == MeasureSpec.EXACTLY) {
            widthSize
        } else if (widthMode == MeasureSpec.AT_MOST) {
            Math.min(desiredWidth, widthSize)
        } else {
            desiredWidth
        }
        height = if (heightMode == MeasureSpec.EXACTLY) {
            heightSize
        } else if (heightMode == MeasureSpec.AT_MOST) {
            Math.min(desiredHeight, heightSize)
        } else {
            desiredHeight
        }
        setMeasuredDimension(width, height)
    }

    override fun onDraw(cnv: Canvas?) {
        if (cnv != null){
            if (stats != null){
//                val grey = ctx.getColor(R.color.grey_text)
//
//                paint.color = grey
//                paint.strokeWidth = 1f
//                cnv.drawLine(fontSize + 25f, 0f, fontSize + 25f, height.toFloat(), paint)
//                cnv.drawLine(0f, height - fontSize - 5f, width.toFloat(),
//                    height - fontSize - 5f, paint)
//
//                val reactions = stats?.reactionAmount
//                val max = reactions!!.maxByOrNull {
//                    it.value
//                }
//
//                var y = height - fontSize - 5f
//                val distance = y.toFloat() / yLabels.toFloat()
//                val d2 = max!!.value / yLabels.toFloat()
//                var v = 0f
//
//                paint.textSize = fontSize
//                for (i in 0..yLabels){
//                    v += (d2/ 1000f)
//                    y += distance
//                }

            }
        }
    }

    private fun pxFromDp(dp: Float, ctx: Context): Float {
        return dp * ctx.resources.displayMetrics.density
    }

}