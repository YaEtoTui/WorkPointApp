package com.pp.coworkingapp.app.retrofit.api

import com.pp.coworkingapp.app.retrofit.domain.request.CreateReviewRequest
import com.pp.coworkingapp.app.retrofit.domain.request.CreateSettingsUserRequest
import com.pp.coworkingapp.app.retrofit.domain.request.RegisterRequest
import com.pp.coworkingapp.app.retrofit.domain.response.CurrentUser
import com.pp.coworkingapp.app.retrofit.domain.response.Place
import com.pp.coworkingapp.app.retrofit.domain.response.PlaceWithTags
import com.pp.coworkingapp.app.retrofit.domain.response.Review
import com.pp.coworkingapp.app.retrofit.domain.response.Token_Access
import com.pp.coworkingapp.app.retrofit.domain.response.User
import okhttp3.Call
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query
import java.io.File

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

    @POST("places/get_place")
    suspend fun findPlaceCard(@Query("id_place") placeId: Int): PlaceWithTags

    @POST("places/get_reviews")
    suspend fun findReviews(@Query("id_place") placeId: Int): List<Review>

    @POST("review/add_review")
    suspend fun addReview(@Header("Authorization") token: String, @Body createReviewRequest: CreateReviewRequest): Review

    @POST("user/photo")
    @Multipart
    suspend fun loadNewPhotoUser(@Header("Authorization") token: String, @Part file: MultipartBody.Part): String

    @POST("user/settings")
    suspend fun changeSettingsUser(@Header("Authorization") token: String, @Body createSettingsUserRequest: CreateSettingsUserRequest): Array<String>
}