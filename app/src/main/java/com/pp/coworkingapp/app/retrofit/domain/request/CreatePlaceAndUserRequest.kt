package com.pp.coworkingapp.app.retrofit.domain.request

import com.google.gson.annotations.SerializedName

data class CreatePlaceAndUserRequest(
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("place_id")
    val placeId: Int
)
