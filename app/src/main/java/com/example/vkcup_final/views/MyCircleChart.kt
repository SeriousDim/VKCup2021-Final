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

class MyCircleChart : View {

    val SEX_MODE = "SEX_MODE"
    val AGE_MODE = "AGE_MODE"
    val CITY_MODE = "CITY_MODE"

    private lateinit var ctx: Context
    private var stats: EpisodeStat? = null
    private var mode: String = SEX_MODE

    private lateinit var paint: Paint

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
        this.mode = mode

        invalidate()
    }

    fun init(){
        paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.style = Paint.Style.FILL
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
                if (mode == SEX_MODE){
                    val menColor = ctx.getColor(R.color.blue2)
                    val womenColor = ctx.getColor(R.color.light_blue)

                    val total = stats?.menAmount!! + stats?.womenAmount!!
                    val menPercent = stats?.menAmount!!.toFloat() / total.toFloat()
                    val womenPercent = stats?.womenAmount!!.toFloat() / total.toFloat()

                    var r = height * 0.5f
                    var start = 0f
                    var sweep = menPercent * 360f

                    paint.color = menColor
                    cnv.drawArc(width * 0.5f - r, 0f, width * 0.5f + r, height.toFloat(), start, sweep, true, paint)
                    start += sweep

                    paint.color = womenColor
                    sweep = womenPercent * 360f
                    cnv.drawArc(width * 0.5f - r, 0f, width * 0.5f + r, height.toFloat(), start, sweep, true, paint)
                }

                if (mode == AGE_MODE && stats?.ageAmount != null){
                    val ages = stats?.ageAmount

                    val colors = arrayOf(
                            ctx.getColor(R.color.chart_color1),
                            ctx.getColor(R.color.chart_color2),
                            ctx.getColor(R.color.chart_color3),
                            ctx.getColor(R.color.chart_color4),
                            ctx.getColor(R.color.chart_color5),
                            ctx.getColor(R.color.chart_color6),
                    )
                    var total = 0
                    ages?.values?.forEach { total += it.men + it.women }
                    /*val newAges = ages?.entries?.sortedByDescending {
                        it.value.women + it.value.women
                    }?.associate { it.toPair() }*/
                    var ind = 0
                    var r = height * 0.5f
                    var start = 0f
                    for (k in ages?.keys!!){
                        paint.color = colors[ind]
                        val sweep = (ages[k]?.men!! + ages[k]?.women!!).toFloat() / total.toFloat() * 360f
                        cnv.drawArc(width * 0.5f - r, 0f, width * 0.5f + r, height.toFloat(), start, sweep, true, paint)
                        ind++
                        start += sweep
                        Log.d("colors", "$ind: ${k.first} - ${k.second}")

                        if (ind >= colors.size)
                            ind = 0
                    }
                }

                if (mode == CITY_MODE && stats?.cityAmount != null){
                    val cities = stats?.cityAmount

                    val colors = arrayOf(
                            ctx.getColor(R.color.chart_color1),
                            ctx.getColor(R.color.chart_color2),
                            ctx.getColor(R.color.chart_color3),
                            ctx.getColor(R.color.chart_color4)
                    )

                    val sorted = cities?.entries?.sortedByDescending { it.value }
                            ?.associate { it.toPair() }
                    var total = 0
                    cities?.values?.forEach { total += it }

                    var r = height * 0.5f
                    var start = 0f
                    var ind = 0

                    var other = 0
                    for (i in sorted!!.keys){
                        if (ind >= 3){
                            other += sorted[i]!!
                            continue
                        }

                        val sweep = (sorted[i])!!.toFloat() / total.toFloat() * 360f
                        paint.color = colors[ind]
                        cnv.drawArc(width * 0.5f - r, 0f, width * 0.5f + r, height.toFloat(), start, sweep, true, paint)
                        ind++
                        start += sweep
                    }

                    val sweep = other / total.toFloat() * 360f
                    paint.color = colors[3]
                    cnv.drawArc(width * 0.5f - r, 0f, width * 0.5f + r, height.toFloat(), start, sweep, true, paint)
                }
            }
        }
    }

    private fun pxFromDp(dp: Float, ctx: Context): Float {
        return dp * ctx.resources.displayMetrics.density
    }

}