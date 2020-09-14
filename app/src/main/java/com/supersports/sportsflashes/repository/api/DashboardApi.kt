package com.supersports.sportsflashes.repository.api

import com.supersports.sportsflashes.model.*
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface DashboardApi {
    @GET("/api/shows")
    fun getFeaturedShows(): Single<BaseResponse<List<FeaturedShows>>>

    @GET("/api/categories")
    fun getCategories(): Single<BaseResponse<List<SportCategories>>>

    @GET("/api/shows/category/{id}")
    fun getShowsByCategories(@Path("id") categoryId: String): Single<BaseResponse<List<FeaturedShows>>>

    @GET("/api/radio")
    fun getLiveRadio(): Single<BaseResponse<List<LiveRadioModel>>>

    @POST("api/users/register/")
//    @FormUrlEncoded
    fun registerFirebase(@Body request: FirebaseRequest): Single<BaseResponse<FirebaseSubscribeModel>>
}