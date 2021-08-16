package com.example.vkcup_final.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import com.example.vkcup_final.R
import java.util.*

class AudiowaveProgressbar : View {

    private lateinit var ctx: Context

    constructor(ctx: Context?) : super(ctx) {
        this.ctx = ctx!!
        init()
    }

    constructor(ctx: Context?, attrs: AttributeSet?) : super(ctx!!, attrs!!) {
        this.ctx = ctx
        init()
    }

    private val STANDART_HEIGHT = 100

    var emojiSize = 50
    var startY = 15f
    var margin = 16f
    var maxProgress = 50 // кол-во полос
    var lineWidth = 7f // ширина полосы
    var lineBorder = 0.65f // макс. высота полос (% от height)
    var emojiBorder = 0.7f // высота отметки реакции (% от height)

    var currentPosition: Int = 2300 // сек.
    var maxPosition: Int = 10000 // сек.

    lateinit var linePaintTrans: Paint
    lateinit var linePaint: Paint
    lateinit var emojiPaint: Paint

    private lateinit var rndData: List<Int>

    fun updatePosition(cur: Int){
        currentPosition = cur
        invalidate()
    }

    fun init() {
        rndData = IntArray(maxProgress) { Random().nextInt(10000) }.asList()

        linePaintTrans = Paint(Paint.ANTI_ALIAS_FLAG)
        linePaintTrans.color = ctx.getColor(R.color.vk_transparent)
        linePaintTrans.strokeWidth = lineWidth
        linePaintTrans.setStrokeCap(Paint.Cap.ROUND)

        linePaint = Paint(linePaintTrans)
        linePaint.color = ctx.getColor(R.color.vk)

        emojiPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        emojiPaint.color = ctx.getColor(R.color.white_transparent)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = 250
        val desiredHeight: Int = STANDART_HEIGHT
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
            val max = rndData.maxByOrNull { it }?.toFloat()
            val maxHeight = height * lineBorder
            val distance = (width - 2 * margin) / maxProgress
            var x = margin + lineWidth

            var amount: Int = 0
            if (currentPosition > 0){
                amount = ((currentPosition.toFloat() / maxPosition.toFloat()) * maxProgress).toInt()
                for (i in 0..amount-1){
                    var r = rndData[i]
                    cnv.drawLine(x, height - startY, x, height - startY - (maxHeight * r / max!!), linePaint)
                    x += distance
                }
            }

            for (i in amount..maxProgress-1){
                var r = rndData[i]
                cnv.drawLine(x, height - startY, x, height - startY - (maxHeight * r / max!!), linePaintTrans)
                x += distance
            }

            drawEmoji(cnv, 1000, 1200, R.drawable.happy)
        }
    }

    fun drawEmoji(cnv: Canvas?, fromSec: Int, toSec: Int, drawableId: Int){
        if (cnv != null){
            val xf = fromSec.toFloat() / maxPosition.toFloat() * width - margin
            val xt = toSec.toFloat() / maxPosition.toFloat() * width + margin
            val h = height * emojiBorder + margin
            cnv.drawRoundRect(xf, height + startY, xt, height - h, 3f, 3f, emojiPaint)

            val bitmap = AppCompatResources.getDrawable(ctx, drawableId)?.toBitmap()
            val rect1 = Rect(0, 0, bitmap?.width!!, bitmap.height)

            val x1: Int = ((xf + xt) * 0.5 - emojiSize * 0.5).toInt()
            val x2: Int = x1 + emojiSize
            val y1: Int = (height - h - margin).toInt()
            val y2: Int = y1 - emojiSize
            val rect2 = Rect(x1, y2, x2, y1)
            cnv.drawBitmap(bitmap!!, rect1, rect2, null)
        }
    }

    private fun pxFromDp(dp: Float, ctx: Context): Float {
        return dp * ctx.resources.displayMetrics.density
    }

}