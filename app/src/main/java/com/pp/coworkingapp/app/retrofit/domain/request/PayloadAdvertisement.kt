package com.pp.coworkingapp.app.retrofit.domain.request

import com.google.gson.annotations.SerializedName

data class PayloadAdvertisement(
    @SerializedName("user_id")
    val userId: Int,
    val name: String,
    val city: String,
    val address: String,
    val tariff: String,
    val email: String,
    val status: String,
    @SerializedName("id_place")
    val idPlace: Int,
    @SerializedName("date_to")
    val dateTo: String,
    @SerializedName("date_from")
    val dateFrom: String,
    val photo: String,
)
