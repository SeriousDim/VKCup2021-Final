package com.example.vkcup_final.vk_sdk

import android.util.Log
import com.example.vkcup_final.vk_sdk.pojos.CityInfo
import com.example.vkcup_final.vk_sdk.pojos.CityResponse
import com.example.vkcup_final.vk_sdk.pojos.Response
import com.example.vkcup_final.vk_sdk.pojos.UserInfo
import com.vk.api.sdk.VK
import com.vk.api.sdk.VKApiCallback
import com.vk.api.sdk.auth.VKAccessToken
import com.vk.api.sdk.auth.VKScope

class VKWorker {

    companion object {

        val scopes = arrayListOf(VKScope.OFFLINE)
        var token: VKAccessToken? = null
        var userInfo: UserInfo? = null
        var city: CityInfo? = null

        fun requestUserInfo(){
            VK.execute(VKUserInfoRequest(intArrayOf(token?.userId!!)), object: VKApiCallback<Response> {
                override fun success(result: Response) {
                    userInfo = result.response[0]
                    Log.d("vk_get", "Success: $userInfo")
                }

                override fun fail(error: Exception) {
                    Log.d("vk_get", "Error: ${error.message}")
                }
            })
        }

        fun getCity(id: Int, index: Int, action: (CityInfo, Int) -> Unit){
            VK.execute(VKCityRequest(intArrayOf(id)), object : VKApiCallback<CityInfo> {
                override fun success(result: CityInfo) {
                    city = result
                    action.invoke(result, index)
                    Log.d("vk_get", "Success: $city")
                }

                override fun fail(error: Exception) {
                    Log.d("vk_get", "Error: ${error.message}")
                }
            })
        }

    }

}