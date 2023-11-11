package com.pp.coworkingapp.app.retrofit.domain.response

import com.google.gson.annotations.SerializedName

data class User(
    val phone: String,
    val name: String,
    val surname: String,
    @SerializedName("role_id")
    val roleId: String,
    @SerializedName("photo_user")
    val photoUser: String,
    val id: Int,
    @SerializedName("is_active")
    val isActive: Boolean
)
