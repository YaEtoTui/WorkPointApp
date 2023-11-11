package com.pp.coworkingapp.app.retrofit.domain.request

data class RegisterRequest(
    val name: String,
    val surname: String,
    val phone: String,
    val password: String,
    val role_id: String
)
