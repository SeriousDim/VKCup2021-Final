package com.example.vkcup_final.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.example.vkcup_final.R
import com.example.vkcup_final.modules.EpisodeStat

class MyBarChart : View {

    private lateinit var ctx: Context
    private var stats: EpisodeStat? = null

    private var color: Int = 0
    private lateinit var paint: Paint
    private lateinit var textPaint: Paint

    private var margin = 10f
    private var fontSize = 14f
    private var labels = 8
    private var barWidth = 26

    constructor(ctx: Context?) : super(ctx) {
        this.ctx = ctx!!
        init()
    }

    constructor(ctx: Context?, attrs: AttributeSet?) : super(ctx!!, attrs!!) {
        this.ctx = ctx
        init()
    }

    fun update(stats: EpisodeStat){
        this.stats = stats

        invalidate()
    }

    fun init(){
        color = ctx.getColor(R.color.light_blue)
        paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = color

        fontSize = pxFromDp(fontSize, ctx)
        textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        textPaint.color = ctx.getColor(R.color.grey_text)
        textPaint.textSize = fontSize
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
                val reactions = stats?.reactionPer20
                val amount = stats?.lowCapacity!!

                if (reactions != null){
                    val max = reactions.maxByOrNull { it }

                    // draw bars
                    var x = 0
                    var distance = (width - amount * barWidth).toFloat() / (amount - 1)
                    var usedH = height.toFloat() - margin - fontSize
                    for (i in 0..amount-2){
                        cnv.drawRoundRect(x.toFloat(), usedH - (reactions[i].toFloat() / max!!.toFloat()) * usedH,
                            x + barWidth.toFloat(), usedH, 10f, 10f, paint)
                        x = (x + barWidth + distance).toInt()
                    }
                    cnv.drawRoundRect(x.toFloat(), usedH - (reactions[amount - 1].toFloat() / max!!.toFloat()) * usedH,
                        x + barWidth.toFloat(), usedH, 10f, 10f, paint)

                    // draw labels
                    x = 0
                    distance = width.toFloat() / (labels - 1).toFloat()
                    var durMins = stats?.durationSec?.div(60)
                    var minsDist = durMins?.div(labels)
                    var mins = 0
                    for (i in 0..labels-2){
                        cnv.drawText("$mins", x.toFloat(), height - 3f, textPaint)
                        mins += minsDist!!
                        x += distance.toInt()
                    }

                    cnv.drawText("$durMins", width - fontSize, height - 3f, textPaint)
                }
            }
        }
    }

    private fun pxFromDp(dp: Float, ctx: Context): Float {
        return dp * ctx.resources.displayMetrics.density
    }

}