package com.example.vkcup_final.modules

import com.example.vkcup_final.emoji_pojos.EmojiData
import com.example.vkcup_final.emoji_pojos.Episodes
import kotlin.math.max

class Ages{
    companion object {

        val UNTIL_18 = Pair(0, 17)
        val AGE_18_21 = Pair(18, 20)
        val AGE_21_24 = Pair(21, 23)
        val AGE_24_27 = Pair(24, 26)
        val AGE_27_30 = Pair(27, 29)
        val UPPER_30 = Pair(30, 150)

    }
}

data class EpisodeStat(
    var e: Episodes,
    var maxProgress: Int,
    var durationSec: Int
){

    var reactionPerLine: ArrayList<HashMap<Int, Int>>
    var reactionProgress: ArrayList<Int>
    var reactionAmount: HashMap<Int, Int>
    var cityAmount: HashMap<Int, Int>
    var ageAmount: HashMap<Pair<Int, Int>, AgeInfo>
    var womenAmount: Int
    var menAmount: Int

    init {
        reactionPerLine = ArrayList(maxProgress)
        reactionProgress = ArrayList(maxProgress)

        for (i in 0..maxProgress-1){
            reactionPerLine.add(HashMap())
            reactionProgress.add(0)
        }

        reactionAmount = HashMap() // <reactionId, amount>
        cityAmount = HashMap() // <cityId, reactionAmount>

        ageAmount = HashMap() // <Pair<from ,to>, AgeInfo>
        ageAmount.put(Ages.UNTIL_18, AgeInfo(
                HashMap(), 0, 0
        ))
        ageAmount.put(Ages.AGE_18_21, AgeInfo(
                HashMap(), 0, 0
        ))
        ageAmount.put(Ages.AGE_21_24, AgeInfo(
                HashMap(), 0, 0
        ))
        ageAmount.put(Ages.AGE_24_27, AgeInfo(
                HashMap(), 0, 0
        ))
        ageAmount.put(Ages.AGE_27_30, AgeInfo(
                HashMap(), 0, 0
        ))
        ageAmount.put(Ages.UPPER_30, AgeInfo(
                HashMap(), 0, 0
        ))

        womenAmount = 0
        menAmount = 0
    }
}

data class AgeInfo(
        var reactions: HashMap<Int, Int>,
        var men: Int,
        var women: Int
)

class StatsManager(
    val maxProgress: Int
) {

    var episodeStats = HashMap<String, EpisodeStat>() // <guid, EpisodeStat>

    fun getAgePair(age: Int): Pair<Int, Int>{
        if ((0..17).contains(age))
            return Ages.UNTIL_18
        if ((18..20).contains(age))
            return Ages.AGE_18_21
        if ((21..23).contains(age))
            return Ages.AGE_21_24
        if ((24..26).contains(age))
            return Ages.AGE_24_27
        if ((27..29).contains(age))
            return Ages.AGE_27_30
        return Ages.UPPER_30
    }

    fun process(e: Episodes, durSec: Int): EpisodeStat?{
        if (episodeStats.containsKey(e.guid))
            return null
        val data = EpisodeStat(e, maxProgress, durSec)

        val stats = e.statistics
        for (s in stats){
            // реакции посекундно
            val index = ((s.time.toFloat() / 1000f) / durSec.toFloat() * maxProgress).toInt()
            data.reactionProgress[index]++

            val oldMap = data.reactionPerLine.getOrElse(index) { HashMap() }
            val oldReact = oldMap.getOrElse(s.reactionId) { 0 }
            oldMap.put(s.reactionId, oldReact + 1)
            data.reactionPerLine[index] = oldMap

            // реакции по типам
            var old = data.reactionAmount.getOrElse(s.reactionId) { 0 }
            data.reactionAmount[s.reactionId] = old + 1

            // статистика по городам
            old = data.cityAmount.getOrElse(s.cityId) { 0 }
            data.cityAmount[s.cityId] = old + 1

            // по возрасту
            val agePair = getAgePair(s.age)
            val ageData = data.ageAmount[agePair]
            val oldNum = ageData?.reactions?.getOrElse(s.reactionId) { 0 } // реакции этого возраста
            ageData?.reactions?.put(s.reactionId, oldNum!! + 1)

            if (s.sex == "female")
            {
                ageData!!.women++
                data.womenAmount++
            } else if (s.sex == "male"){
                ageData!!.men++
                data.menAmount++
            }

            data.ageAmount.put(agePair, ageData!!)
        }

        episodeStats[e.guid] = data
        return data
    }

}