package com.example.example

import com.google.gson.annotations.SerializedName

   
data class Reactions (

   @SerializedName("reaction_id") var reactionId : Int,
   @SerializedName("emoji") var emoji : String,
   @SerializedName("description") var description : String

)