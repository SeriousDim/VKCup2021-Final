package com.example.vkcup_final.emoji_pojos

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class Episodes (

        @SerializedName("guid") var guid : String,
        @SerializedName("default_reactions") var defaultReactions : List<Int>,
        @SerializedName("timed_reactions") var timedReactions : List<TimedReactions>,
        @SerializedName("statistics") var statistics : List<Statistics>

) : Serializable