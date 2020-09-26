package com.supersports.sportsflashes.repository.api

import com.supersports.sportsflashes.model.*
import io.reactivex.Single
import retrofit2.http.*

interface DashboardApi {
    @GET("/api/shows")
    fun getFeaturedShows(): Single<BaseResponse<List<FeaturedShows>>>

    @GET("/api/categories")
    fun getCategories(): Single<BaseResponse<SportCategories>>

    @GET("/api/shows/category/{id}")
    fun getShowsByCategories(@Path("id") categoryId: String): Single<BaseResponse<List<FeaturedShows>>>

    @GET("/api/radio")
    fun getLiveRadio(): Single<BaseResponse<LiveRadioModel>>

    @POST("api/users/register/")
//    @FormUrlEncoded
    fun registerFirebase(@Body request: FirebaseRequest): Single<BaseResponse<FirebaseSubscribeModel>>

    //shows/search?text=Chetan Sharma
    @GET("api/shows/search")
    fun getSearchShows(@Query("text") name: String): Single<BaseResponse<List<FeaturedShows>>>
}