package com.pp.coworkingapp.app.retrofit.domain.request

import com.google.gson.annotations.SerializedName

data class AuthRequest(
    val username: String,
    val password: String
)
