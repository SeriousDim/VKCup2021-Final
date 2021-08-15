package com.example.vkcup_final.vk_sdk

import com.example.vkcup_final.vk_sdk.pojos.Response
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface VKUserApi {

    @GET("users.get")
    fun getUserInfo(@Query("v") v: String = "5.52",
                           @Query("access_token") accessToken: String): Call<Response>

}