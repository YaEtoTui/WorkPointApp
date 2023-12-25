package com.pp.coworkingapp.app.retrofit.domain.request

import com.google.gson.annotations.SerializedName

data class Payload(
    @SerializedName("user_id")
    val userId: Int,
    val name: String,
    val city: String,
    val district: String,
    val address: String,
    val description: String,
    @SerializedName("opening_hours")
    val openingHours: String,
    @SerializedName("type_cafe")
    val typeCafe: String,
    val cost: String,
    val tags: List<String>,
    val rating: String,
    val parking: Boolean,
    @SerializedName("recreation_area")
    val recreationArea: Boolean,
    @SerializedName("conference_hall")
    val conferenceHall: Boolean,
    @SerializedName("company_phone")
    val companyPhone: String,
    val email: String,
    val site: String,
    val photo: String,
    val status: String
)
