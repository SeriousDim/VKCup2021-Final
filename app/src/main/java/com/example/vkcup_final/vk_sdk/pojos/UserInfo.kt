package com.example.vkcup_final.vk_sdk.pojos

import com.google.gson.annotations.SerializedName

data class UserInfo (

   @SerializedName("first_name") var firstName : String,
   @SerializedName("id") var id : Int,
   @SerializedName("last_name") var lastName : String,
   @SerializedName("can_access_closed") var canAccessClosed : Boolean,
   @SerializedName("is_closed") var isClosed : Boolean,
   @SerializedName("sex") var sex : Int,
   @SerializedName("bdate") var bdate : String,
   @SerializedName("city") var city : City

) {

    override fun toString(): String {
        return "$firstName $lastName: id = $id, sex = $sex, bdate = $bdate, city = $city"
    }

}