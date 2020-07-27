package com.sports.sportsflashes.repository.api

import com.sports.sportsflashes.model.BaseResponse
import com.sports.sportsflashes.model.FeaturedShows
import com.sports.sportsflashes.model.SportCategories
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path

interface DashboardApi {
    @GET("/api/shows")
    fun getFeaturedShows(): Single<BaseResponse<List<FeaturedShows>>>

    @GET("/api/categories")
    fun getCategories(): Single<BaseResponse<List<SportCategories>>>

    @GET("/api/shows/category/{id}")
    fun getShowsByCategories(@Path("id") categoryId: String): Single<BaseResponse<List<FeaturedShows>>>
}