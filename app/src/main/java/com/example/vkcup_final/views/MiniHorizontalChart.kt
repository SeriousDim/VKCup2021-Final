package com.example.vkcup_final.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.example.vkcup_final.R
import com.example.vkcup_final.modules.EpisodeStat

class MiniHorizontalChart : View {

    private lateinit var ctx: Context
    private var stats: EpisodeStat? = null


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


            }
        }
    }

    private fun pxFromDp(dp: Float, ctx: Context): Float {
        return dp * ctx.resources.displayMetrics.density
    }

}