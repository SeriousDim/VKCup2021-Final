package com.example.vkcup_final.vk_sdk.pojos

import com.google.gson.annotations.SerializedName


data class CityResponse (

        @SerializedName("id") var id : Int,
        @SerializedName("title") var title : String

)