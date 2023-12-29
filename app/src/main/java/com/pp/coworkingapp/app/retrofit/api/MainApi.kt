package com.pp.coworkingapp.app.retrofit.api

import com.pp.coworkingapp.app.retrofit.domain.request.CreatePlaceAndUserRequest
import com.pp.coworkingapp.app.retrofit.domain.request.CreateReviewRequest
import com.pp.coworkingapp.app.retrofit.domain.request.CreateSettingsUserRequest
import com.pp.coworkingapp.app.retrofit.domain.request.Payload
import com.pp.coworkingapp.app.retrofit.domain.request.PayloadSansTags
import com.pp.coworkingapp.app.retrofit.domain.request.RegisterRequest
import com.pp.coworkingapp.app.retrofit.domain.response.CurrentUser
import com.pp.coworkingapp.app.retrofit.domain.response.Place
import com.pp.coworkingapp.app.retrofit.domain.response.PlaceWithTags
import com.pp.coworkingapp.app.retrofit.domain.response.Review
import com.pp.coworkingapp.app.retrofit.domain.response.Tag
import com.pp.coworkingapp.app.retrofit.domain.response.Token_Access
import com.pp.coworkingapp.app.retrofit.domain.response.User
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.Query

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
    suspend fun loadNewPhotoUser(@Header("Authorization") token: String, @Part file: MultipartBody.Part): Response<String>

    @POST("user/settings")
    suspend fun changeSettingsUser(@Header("Authorization") token: String, @Body createSettingsUserRequest: CreateSettingsUserRequest): Array<String>

    @Headers("Content-Type: application/json")
    @GET("user/place")
    suspend fun getPlaceCoffee(@Header("Authorization") token: String, @Query("user_id") userId: Int): List<Place>

    @POST("user/role")
    suspend fun changeRole(@Header("Authorization") token: String, @Query("role_id") role_id: Int)

    @GET("places/get_tags")
    suspend fun getTagsAll(): List<Tag>

    @POST("places/upload_place")
    @Multipart
    suspend fun loadNewPlaceInDB(@Header("Authorization") token: String, @Part("payload") payload: RequestBody, @Part files: List<MultipartBody.Part>): Response<Place>

    @POST("places/update")
    suspend fun loadRedactPayload(@Header("Authorization") token: String, @Body payload: PayloadSansTags): Response<Place>

    @POST("places/update_photo")
    @Multipart
    suspend fun loadRedactPhoto(@Header("Authorization") token: String, @Query("id_place") placeId: Int, @Part files: List<MultipartBody.Part>): Response<String>

    @POST("places/update_tags")
    suspend fun loadRedactTags(@Header("Authorization") token: String, @Query("id_place") placeId: Int, @Body tags: List<String>)

    @DELETE("places/delete")
    suspend fun deletePlaces(@Header("Authorization") token: String, @Query("id_place") placeId: Int)

    @POST("user/favorite_place")
    suspend fun addFavoritePlace(@Header("Authorization") token: String, @Body createPlaceAndUserRequest: CreatePlaceAndUserRequest): String

    @POST("user/my_favorite_places")
    suspend fun getFavoritePlaces(@Header("Authorization") token: String): List<Place>

    @POST("user/delete_place")
    suspend fun deleteFavoritePlace(@Header("Authorization") token: String, @Query("user_id") userId: Int): Response<String>
}