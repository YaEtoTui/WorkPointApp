package com.pp.coworkingapp.app.retrofit.domain.response

import com.google.gson.annotations.SerializedName

data class PlaceWithTags(
    val id: Int,
    val description: String,
    val rating: String,
    @SerializedName("opening_hours")
    val openingHours: String,
    val parking: Boolean,
    @SerializedName("user_id")
    val userId: Int,
    val cost: String,
    @SerializedName("recreation_area")
    val recreationArea: Boolean,
    @SerializedName("type_cafe")
    val typeCafe: String,
    @SerializedName("conference_hall")
    val conferenceHall: Boolean,
    val name: String,
    @SerializedName("company_phone")
    val companyPhone: String,
    val status: String,
    val city: String,
    val email: String,
    val district: String,
    val site: String,
    val address: String,
    val photo: String,
    val tags : List<Tag>
)
