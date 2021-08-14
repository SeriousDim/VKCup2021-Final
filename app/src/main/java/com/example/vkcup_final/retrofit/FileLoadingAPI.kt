package com.example.vkcup_final.retrofit

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url

interface FileLoadingAPI {

    @GET
    @Streaming
    fun loadByUrl(@Url url: String): Call<ResponseBody>

    @GET
    @Streaming
    fun loadReactions(@Url url: String): Call<ResponseBody> // POJO instead ResponseBody

}