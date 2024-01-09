package com.pp.coworkingapp.app.retrofit.domain.response

import com.google.gson.annotations.SerializedName

data class Answer(
    @SerializedName("user_name")
    val userName: String,
    val id: Int,
    @SerializedName("user_photo")
    val userPhoto: String,
    val body: String,
    @SerializedName("user_surname")
    val userSurname: String,
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("review_id")
    val reviewId: String,
    @SerializedName("place_id")
    val placeId: String,
    @SerializedName("created_at")
    val createdAt: String,
)
