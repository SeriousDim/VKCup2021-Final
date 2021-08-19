package com.example.vkcup_final.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import com.example.vkcup_final.R
import com.example.vkcup_final.emoji_pojos.Episodes
import com.example.vkcup_final.emoji_pojos.Reactions
import com.example.vkcup_final.modules.EpisodeStat
import com.example.vkcup_final.modules.ReactionManager
import com.example.vkcup_final.modules.StatsManager
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

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

    var emojiSizeDp = 14f
    var startY = 15f
    var margin = 16f
    var maxProgress = 50 // кол-во полос
    var lineWidth = 7f // ширина полосы
    var lineBorder = 0.65f // макс. высота полос (% от height)
    var emojiBorder = 0.7f // высота отметки реакции (% от height)

    var currentPosition: Int = 2300 // сек.
    var maxPosition: Int = 10000 // сек.
    var max = 0f

    lateinit var linePaintTrans: Paint
    lateinit var linePaint: Paint
    lateinit var emojiPaint: Paint
    lateinit var textPaint: Paint

    private lateinit var lineData: List<Int>
    private var stats: EpisodeStat? = null
    private var reactManager: ReactionManager? = null
    private var popReactions: List<Pair<Int, Map.Entry<Int, Int>?>>? = null

    fun getDrawableEmoji(ctx: Context, s: String): Drawable {
        return when (s){
            "\uD83D\uDE02" -> AppCompatResources.getDrawable(ctx, R.drawable.lol)!!
            "\uD83D\uDC4D" -> AppCompatResources.getDrawable(ctx, R.drawable.like)!!
            "\uD83D\uDC4E" -> AppCompatResources.getDrawable(ctx, R.drawable.dislike)!!
            "\uD83D\uDE21" -> AppCompatResources.getDrawable(ctx, R.drawable.angry)!!
            "\uD83D\uDE14" -> AppCompatResources.getDrawable(ctx, R.drawable.sad)!!
            "\uD83D\uDE0A" -> AppCompatResources.getDrawable(ctx, R.drawable.happy)!!
            "\uD83D\uDCB8" -> AppCompatResources.getDrawable(ctx, R.drawable.advert)!!
            "\uD83D\uDCA9" -> AppCompatResources.getDrawable(ctx, R.drawable.boo)!!
            else -> AppCompatResources.getDrawable(ctx, R.drawable.ic_baseline_tag_faces_24)!!
        }
    }

    fun updateLines(episodes: EpisodeStat, r: ReactionManager){
        lineData = episodes.reactionProgress
        max = lineData.maxByOrNull { it }?.toFloat()!!

        reactManager = r
        stats = episodes

        processReactions()

        invalidate()
    }

    fun updatePosition(cur: Int){
        currentPosition = cur
        invalidate()
    }

    fun processReactions(){
        if (stats != null){
            val withInd = stats!!.reactionPerLine.mapIndexed() {
                i, h -> Pair(i, h)
            }
            val maxes = withInd.map {
                val localMax = it.second.maxByOrNull {
                    it.value
                }
                Pair(it.first, localMax)
            }.filter { it.second != null }

            val newMax = maxes.maxByOrNull {
                it.second?.value!!
            }
            popReactions = maxes.filter {
                it.second != null && it.second?.value!! >= newMax!!.second?.value!! * 0.35f
            }

        }
    }

    fun init() {
        emojiSizeDp = pxFromDp(emojiSizeDp, ctx)

        lineData = IntArray(maxProgress) { Random().nextInt(10000) }.asList()

        linePaintTrans = Paint(Paint.ANTI_ALIAS_FLAG)
        linePaintTrans.color = ctx.getColor(R.color.vk_transparent)
        linePaintTrans.strokeWidth = lineWidth
        linePaintTrans.setStrokeCap(Paint.Cap.ROUND)

        linePaint = Paint(linePaintTrans)
        linePaint.color = ctx.getColor(R.color.vk)

        emojiPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        emojiPaint.color = ctx.getColor(R.color.white_transparent)

        textPaint = Paint(linePaintTrans)
        textPaint.setColor(Color.WHITE)
        textPaint.setTextSize(emojiSizeDp.toFloat())
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
            val maxHeight = height * lineBorder
            val distance = (width - 2 * margin) / maxProgress
            var x = margin + lineWidth

            var amount: Int = 0
            if (currentPosition > 0){
                amount = ((currentPosition.toFloat() / maxPosition.toFloat()) * maxProgress).toInt()
                for (i in 0..amount-1){
                    var r = lineData[i]
                    cnv.drawLine(x, height - startY, x, height - startY - (maxHeight * r / max!!), linePaint)
                    x += distance
                }
            }

            for (i in amount..maxProgress-1){
                var r = lineData[i]
                cnv.drawLine(x, height - startY, x, height - startY - (maxHeight * r / max!!), linePaintTrans)
                x += distance
            }

            if (popReactions != null){
                for (e in popReactions!!){
                    val reaction = reactManager!!.getReaction(e.second!!.key)
                    val index = e.first
                    val posSec = ((index.toFloat() / maxProgress.toFloat()) * maxPosition).toInt()
                    drawEmoji(cnv, posSec, reaction!!.emoji)
                }
            }
        }
    }

    fun drawEmoji(cnv: Canvas?, sec: Int, emoji: String){
        if (cnv != null){
            val s = sec.toFloat() / maxPosition.toFloat() * width + margin
            val h = height * emojiBorder + margin
            cnv.drawRoundRect(s - lineWidth, height + startY, s + lineWidth, height - h, 3f, 3f, emojiPaint)

            val x1 = (s - emojiSizeDp * 0.5)
            val y1 = (height - h - 2 * margin)
            cnv.drawText(emoji, x1.toFloat(), y1, textPaint)
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

            val x1: Int = ((xf + xt) * 0.5 - emojiSizeDp * 0.5).toInt()
            val x2: Int = x1 + emojiSizeDp.toInt()
            val y1: Int = (height - h - margin).toInt()
            val y2: Int = y1 - emojiSizeDp.toInt()
            val rect2 = Rect(x1, y2, x2, y1)
            cnv.drawBitmap(bitmap!!, rect1, rect2, null)
        }
    }

    fun drawEmoji(cnv: Canvas?, fromSec: Int, toSec: Int, emoji: String){
        if (cnv != null){
            val xf = fromSec.toFloat() / maxPosition.toFloat() * width - margin
            val xt = toSec.toFloat() / maxPosition.toFloat() * width + margin
            val h = height * emojiBorder + margin
            cnv.drawRoundRect(xf, height + startY, xt, height - h, 3f, 3f, emojiPaint)

            val x1 = ((xf + xt) * 0.5 - emojiSizeDp * 0.5)
            val y1 = (height - h - 2 * margin)
            cnv.drawText(emoji, x1.toFloat(), y1, textPaint)
        }
    }

    private fun pxFromDp(dp: Float, ctx: Context): Float {
        return dp * ctx.resources.displayMetrics.density
    }

}