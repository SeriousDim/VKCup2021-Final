package com.example.vkcup_final.vk_sdk

import com.example.vkcup_final.vk_sdk.pojos.City
import com.example.vkcup_final.vk_sdk.pojos.CityInfo
import com.example.vkcup_final.vk_sdk.pojos.CityResponse
import com.example.vkcup_final.vk_sdk.pojos.Response
import com.google.gson.Gson
import com.vk.api.sdk.requests.VKRequest
import org.json.JSONObject

class VKCityRequest: VKRequest<CityInfo> {
    constructor(uids: IntArray = intArrayOf()): super("database.getCitiesById") {
        if (uids.isNotEmpty()) {
            addParam("city_ids", uids.joinToString(","))
        }
    }

    override fun parse(json: JSONObject): CityInfo {
        val gson = Gson()
        return gson.fromJson(json.toString(), CityInfo::class.java)
    }
}