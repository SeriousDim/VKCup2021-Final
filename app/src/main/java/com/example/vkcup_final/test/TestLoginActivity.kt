package com.example.vkcup_final.test

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.vk_cup_2021.modules.Notifier
import com.example.vkcup_final.R
import com.vk.api.sdk.VK
import com.vk.api.sdk.auth.VKAccessToken
import com.vk.api.sdk.auth.VKAuthCallback
import com.vk.api.sdk.auth.VKScope
import com.vk.api.sdk.internal.VKErrorUtils

class TestLoginActivity : AppCompatActivity() {

    private val scopes = arrayListOf(VKScope.OFFLINE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_login)

        VK.login(this, scopes)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val callback = object: VKAuthCallback {
            override fun onLogin(token: VKAccessToken) {
                // User passed authorization
                Log.d("vk_login", "Success: ${token.accessToken}")
            }

            override fun onLoginFailed(errorCode: Int) {
                // User didn't pass authorization
                Log.d("vk_login", "Error: ${errorCode}")
            }
        }
        if (data == null || !VK.onActivityResult(requestCode, resultCode, data, callback)) {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

}