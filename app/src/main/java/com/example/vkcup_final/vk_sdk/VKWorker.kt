package com.example.vkcup_final.vk_sdk

import android.util.Log
import android.view.View
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

        fun requestUserInfo(){
            VK.execute(VKUserInfoRequest(intArrayOf(210700286)), object: VKApiCallback<Response> {
                override fun success(result: Response) {
                    userInfo = result.response[0]
                    Log.d("vk_get", "Success: $userInfo")
                }

                override fun fail(error: Exception) {
                    Log.d("vk_get", "Error: ${error.message}")
                }
            })
        }

    }

}