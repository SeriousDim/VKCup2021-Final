package com.example.vkcup_final.emoji_pojos

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class Statistics (

   @SerializedName("time") var time : Int,
   @SerializedName("reaction_id") var reactionId : Int,
   @SerializedName("sex") var sex : String,
   @SerializedName("age") var age : Int,
   @SerializedName("city_id") var cityId : Int

) : Serializable