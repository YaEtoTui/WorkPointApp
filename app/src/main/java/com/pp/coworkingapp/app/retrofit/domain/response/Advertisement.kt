package com.pp.coworkingapp.app.retrofit.domain.response

import com.google.gson.annotations.SerializedName

data class Advertisement(
    val city: String,
    val id: Int,
    val tariff: String,
    val status: String,
    @SerializedName("date_to")
    val dateTo: String,
    val photo: String,
    val name: String,
    val address: String,
    @SerializedName("user_id")
    val userId: Int,
    val email: String,
    @SerializedName("id_place")
    val idPlace: Int,
    @SerializedName("date_from")
    val dateFrom: String
)
