package com.example.example

import com.google.gson.annotations.SerializedName

   
data class EmojiData (

   @SerializedName("reactions") var reactions : List<Reactions>,
   @SerializedName("episodes") var episodes : List<Episodes>

)