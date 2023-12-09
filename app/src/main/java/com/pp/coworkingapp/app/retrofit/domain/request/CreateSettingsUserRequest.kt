package com.pp.coworkingapp.app.retrofit.domain.request

data class CreateSettingsUserRequest(
    val phone: String,
    val name: String,
    val surname: String,
    val city: String,
    val id: Int
)
