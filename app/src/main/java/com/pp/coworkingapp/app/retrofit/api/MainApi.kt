package com.pp.coworkingapp.app.retrofit.api

import com.pp.coworkingapp.app.retrofit.domain.request.RegisterRequest
import com.pp.coworkingapp.app.retrofit.domain.response.CurrentUser
import com.pp.coworkingapp.app.retrofit.domain.response.Place
import com.pp.coworkingapp.app.retrofit.domain.response.Token_Access
import com.pp.coworkingapp.app.retrofit.domain.response.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface MainApi {

    @GET("places/all")
    suspend fun getListPlaces() : List<Place>

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST("user/login")
    suspend fun auth(@Field("username") username: String, @Field("password") password: String): Response<Token_Access> //токен

    @POST("user/signup")
    suspend fun register(@Body registerRequest: RegisterRequest): Response<User>

    @Headers("Content-Type: application/json")
    @GET("user/current")
    suspend fun checkUser(@Header("Authorization") token: String): CurrentUser

//    @GET("products/search")
//    suspend fun getProductsByName(@Query("q") name: String): Products
}