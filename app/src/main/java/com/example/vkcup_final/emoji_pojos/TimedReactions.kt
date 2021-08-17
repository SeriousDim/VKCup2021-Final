package com.example.vkcup_final.emoji_pojos

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class TimedReactions (

   @SerializedName("from") var from : String,
   @SerializedName("to") var to : String,
   @SerializedName("available_reactions") var availableReactions : List<Int>

) : Serializable