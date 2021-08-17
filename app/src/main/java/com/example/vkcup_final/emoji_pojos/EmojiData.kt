package com.example.vkcup_final.emoji_pojos

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class EmojiData (

        @SerializedName("reactions") var reactions : List<Reactions>,
        @SerializedName("episodes") var episodes : List<Episodes>

) : Serializable