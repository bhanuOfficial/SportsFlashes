package com.sports.sportsflashes.repository.api

import com.sports.sportsflashes.model.*
import io.reactivex.Single
import retrofit2.http.*

interface DashboardApi {
    @GET("/api/shows")
    fun getFeaturedShows(): Single<BaseResponse<List<FeaturedShows>>>

    @GET("/api/categories")
    fun getCategories(): Single<BaseResponse<List<SportCategories>>>

    @GET("/api/shows/category/{id}")
    fun getShowsByCategories(@Path("id") categoryId: String): Single<BaseResponse<List<FeaturedShows>>>

    @POST("api/users/register/")
//    @FormUrlEncoded
    fun registerFirebase(@Body request: FirebaseRequest): Single<BaseResponse<FirebaseSubscribeModel>>
}