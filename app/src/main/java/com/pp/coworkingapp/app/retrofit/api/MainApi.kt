package com.pp.coworkingapp.app.retrofit.api

import com.pp.coworkingapp.app.retrofit.domain.request.AuthRequest
import com.pp.coworkingapp.app.retrofit.domain.response.Place
import com.pp.coworkingapp.app.retrofit.domain.response.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface MainApi {

    @GET("places/all")
    suspend fun getListPlaces() : List<Place>

    @POST("user/login")
    suspend fun auth(@Body authRequest: AuthRequest): Response<User>
}