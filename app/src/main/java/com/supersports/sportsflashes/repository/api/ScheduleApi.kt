package com.supersports.sportsflashes.repository.api

import com.supersports.sportsflashes.model.*
import io.reactivex.Single
import retrofit2.http.*

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

    @PUT("api/users/subscribe")
    fun setReminderForShow(@Body requestBody: ReminderReqModel): Single<BaseResponse<ReminderRespose>>

    @PUT("/api/users/unsubscribe")
    fun removeReminderForShow(@Body requestBody: ReminderReqModel): Single<BaseResponse<ReminderRespose>>

    @GET("/api/radio/{id}/details")
    fun getRadioDetails(@Path("id") radioId: String): Single<BaseResponse<LiveSeasonModel>>


}