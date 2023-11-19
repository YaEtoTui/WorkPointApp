package com.pp.coworkingapp.app.retrofit.domain.request

import com.google.gson.annotations.SerializedName

data class CreateReviewRequest(
    @SerializedName("user_id")
    val userId: Int,
//    @SerializedName("user_name")
//    val name: String,
//    @SerializedName("user_surname")
//    val surname: String,
//    @SerializedName("user_photo")
//    val photo: String,
    @SerializedName("place_id")
    val placeId: Int,
    val body: String,
    val rank: Int
)
