package com.pp.coworkingapp.app.retrofit.domain.response

import com.google.gson.annotations.SerializedName

data class IdResponse(
    val id: Int,
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("place_id")
    val placeId: Int
)
