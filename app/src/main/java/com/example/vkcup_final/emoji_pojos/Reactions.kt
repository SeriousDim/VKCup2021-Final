package com.example.vkcup_final.emoji_pojos

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class Reactions (

   @SerializedName("reaction_id") var reactionId : Int,
   @SerializedName("emoji") var emoji : String,
   @SerializedName("description") var description : String

) : Serializable