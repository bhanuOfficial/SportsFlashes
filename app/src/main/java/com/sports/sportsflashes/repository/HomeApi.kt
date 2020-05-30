package com.sports.sportsflashes.repository

import com.sports.sportsflashes.model.BaseResponse
import com.sports.sportsflashes.model.Language
import io.reactivex.Single
import retrofit2.http.GET

interface HomeApi {

    @GET("/v5/findlang")
    fun getHomeApi(): Single<BaseResponse<Language>>
}