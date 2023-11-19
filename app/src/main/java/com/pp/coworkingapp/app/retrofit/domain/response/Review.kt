package com.pp.coworkingapp.app.retrofit.domain.response

import com.google.gson.annotations.SerializedName

data class Review(
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("user_name")
    val username: String,
    @SerializedName("user_surname")
    val surname: String,
    @SerializedName("user_photo")
    val photo: String,
    @SerializedName("place_id")
    val placeId: Int,
    @SerializedName("body")
    val textReview: String,
    val rank: Int,
    val id: Int,
    @SerializedName("created_at")
    val createdAt: String
)
