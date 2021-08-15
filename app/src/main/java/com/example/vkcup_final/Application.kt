package com.example.vkcup_final

import android.app.Application
import com.vk.api.sdk.VK
import com.vk.api.sdk.VKTokenExpiredHandler

class Application : Application() {

    private val tokenTracker = object: VKTokenExpiredHandler {
        override fun onTokenExpired() {
            // token expired
        }
    }

    override fun onCreate() {
        super.onCreate()
        VK.initialize(this)
        VK.addTokenExpiredHandler(tokenTracker)
    }

}