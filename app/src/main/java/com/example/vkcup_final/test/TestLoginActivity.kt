package com.example.vkcup_final.test

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.vkcup_final.R
import com.example.vkcup_final.vk_sdk.VKUserInfoRequest
import com.example.vkcup_final.vk_sdk.pojos.Response
import com.example.vkcup_final.vk_sdk.pojos.UserInfo
import com.vk.api.sdk.VK
import com.vk.api.sdk.VKApiCallback
import com.vk.api.sdk.auth.VKAccessToken
import com.vk.api.sdk.auth.VKAuthCallback
import com.vk.api.sdk.auth.VKScope

class TestLoginActivity : AppCompatActivity() {

    private val scopes = arrayListOf(VKScope.OFFLINE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_login)

        VK.login(this, scopes)
    }

    fun getUserInfo(v: View){
        VK.execute(VKUserInfoRequest(intArrayOf(210700286)), object: VKApiCallback<Response> {
            override fun success(result: Response) {
                val obj = result.response[0]
                Log.d("vk_get", "Success: $obj")
            }

            override fun fail(error: Exception) {
                Log.d("vk_get", "Error: ${error.message}")
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val callback = object: VKAuthCallback {
            override fun onLogin(token: VKAccessToken) {
                // User passed authorization
                Log.d("vk_login", "Success: ${token.accessToken}")
            }

            override fun onLoginFailed(errorCode: Int) {
                // User didn't pass authorization
                Log.d("vk_login", "Error: $errorCode")
            }
        }
        if (data == null || !VK.onActivityResult(requestCode, resultCode, data, callback)) {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

}