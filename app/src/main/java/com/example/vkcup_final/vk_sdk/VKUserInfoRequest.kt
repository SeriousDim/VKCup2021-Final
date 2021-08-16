package com.example.vkcup_final.vk_sdk

import com.example.vkcup_final.vk_sdk.pojos.Response
import com.example.vkcup_final.vk_sdk.pojos.UserInfo
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.vk.api.sdk.requests.VKRequest
import org.json.JSONObject
import java.lang.reflect.Type


class VKUserInfoRequest: VKRequest<Response> {
    constructor(uids: IntArray = intArrayOf()): super("users.get") {
        if (uids.isNotEmpty()) {
            addParam("user_ids", uids.joinToString(","))
        }
        addParam("fields", "sex,bdate,city")
    }

    override fun parse(json: JSONObject): Response {
        val gson = Gson()
        return gson.fromJson(json.toString(), Response::class.java)
    }
}