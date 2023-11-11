package com.pp.coworkingapp.app.retrofit.domain.response

import com.google.gson.annotations.SerializedName

data class CurrentUser(
    val id: Int,
    val phone: String,
    val name: String,
    val surname: String,
    @SerializedName("role_id")
    val roleId: Int,
    @SerializedName("photo_user")
    val photoUser: String,
    val city: String
)
