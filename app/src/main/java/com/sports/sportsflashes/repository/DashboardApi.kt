package com.sports.sportsflashes.repository

import com.sports.sportsflashes.model.FeaturedShows
import com.sports.sportsflashes.model.SportCategories
import io.reactivex.Single
import retrofit2.http.GET

interface DashboardApi {
    @GET("/api/shows")
    fun getFeaturedShows(): Single<List<FeaturedShows>>

    @GET("/api/categories")
    fun getCategories(): Single<List<SportCategories>>
}