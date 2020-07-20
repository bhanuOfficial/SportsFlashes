package com.sports.sportsflashes.repository.api

import com.sports.sportsflashes.model.BaseResponse
import com.sports.sportsflashes.model.LiveSeasonModel
import com.sports.sportsflashes.model.MonthEventModel
import com.sports.sportsflashes.model.ScheduleModel
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 *Created by Bhanu on 06-07-2020
 */
interface ScheduleApi {
    @GET("/api/schedule/weekly")
    fun getScheduleShows(): Single<BaseResponse<ScheduleModel>>

    @GET("/api/schedule/monthly")
    fun getEvents(@Query("month") month: Int): Single<BaseResponse<List<MonthEventModel>>>

    @GET("/api/seasons/{id}")
    fun getSeasonById(@Path("id") seasonId: String): Single<BaseResponse<LiveSeasonModel>>
}