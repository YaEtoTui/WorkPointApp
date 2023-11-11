package com.pp.coworkingapp.app.retrofit.domain.response

import com.google.gson.annotations.SerializedName

data class Token_Access(
    @SerializedName("access_token")
    val token: String
)
