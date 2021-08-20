package com.example.vkcup_final.vk_sdk.pojos


import com.google.gson.annotations.SerializedName


data class CityInfo (

        @SerializedName("response") var response : List<CityResponse>

)