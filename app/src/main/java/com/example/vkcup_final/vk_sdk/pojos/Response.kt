package com.example.vkcup_final.vk_sdk.pojos

import com.google.gson.annotations.SerializedName

data class Response (
    @SerializedName("response") var response : List<UserInfo>,
)