package com.example.example

import com.google.gson.annotations.SerializedName

   
data class TimedReactions (

   @SerializedName("from") var from : String,
   @SerializedName("to") var to : String,
   @SerializedName("available_reactions") var availableReactions : String

)