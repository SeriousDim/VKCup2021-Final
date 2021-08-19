package com.example.vkcup_final.modules

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.appcompat.content.res.AppCompatResources
import com.example.vkcup_final.R
import com.example.vkcup_final.emoji_pojos.EmojiData
import com.example.vkcup_final.emoji_pojos.Episodes
import com.example.vkcup_final.emoji_pojos.Reactions
import com.example.vkcup_final.emoji_pojos.TimedReactions

class ReactionManager(
    var emojiData: EmojiData
) {

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

    fun getEpisodeAmount(): Int {
        return emojiData.episodes.size
    }

    fun getEpisode(index: Int): Episodes{
        return emojiData.episodes[index]
    }

    fun getEpisode(guid: String): Episodes?{
        return emojiData.episodes.find {
            it.guid == guid
        }
    }

    fun getReaction(id: Int): Reactions?{
        return emojiData.reactions.find {
            it.reactionId == id
        }
    }

    fun isAvaliable(t: TimedReactions, posSec: Int): Boolean{
        return posSec >= t.from.toInt() && posSec <= t.to.toInt()
    }

    fun getDefaultReaction(e: Episodes): List<Reactions>{
        return e.defaultReactions.map {
            getReaction(it)!!
        }
    }

    fun getAvaliableReactions(e: Episodes, posSec: Int): List<Reactions>{
        val ids = mutableListOf<Int>()
        e.timedReactions.forEach {
            if (isAvaliable(it, posSec)){
                it.availableReactions.forEach {
                    if (!ids.contains(it)) {
                        ids.add(it)
                    }
                }
            }
        }

        return ids.map {
            getReaction(it)!!
        }.toList()
    }

}