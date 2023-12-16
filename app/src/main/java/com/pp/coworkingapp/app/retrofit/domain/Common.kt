package com.pp.coworkingapp.app.retrofit.domain

import com.pp.coworkingapp.app.retrofit.api.MainApi

object Common {
    private val BASE_URL = "https://www.1506815-cq40245.tw1.ru"
    val retrofitService: MainApi
        get() = RetrofitClient.getClient(BASE_URL).create(MainApi::class.java)
}