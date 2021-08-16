package com.example.example

import com.google.gson.annotations.SerializedName

   
data class Episodes (

   @SerializedName("guid") var guid : String,
   @SerializedName("default_reactions") var defaultReactions : String,
   @SerializedName("timed_reactions") var timedReactions : List<TimedReactions>,
   @SerializedName("statistics") var statistics : List<Statistics>

)