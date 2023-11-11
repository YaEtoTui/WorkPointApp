package com.pp.coworkingapp.app.retrofit.domain.request

import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    val phone: String,
    val name: String,
    val surname: String,
    @SerializedName("role_id")
    val roleId: String,
    @SerializedName("photo_user")
    val photoUser: String,
    val password: String
)
