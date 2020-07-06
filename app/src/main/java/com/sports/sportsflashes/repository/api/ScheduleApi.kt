package com.sports.sportsflashes.repository.api

import com.sports.sportsflashes.model.BaseResponse
import com.sports.sportsflashes.model.ScheduleModel
import io.reactivex.Single
import retrofit2.http.GET

/**
 *Created by Bhanu on 06-07-2020
 */
interface ScheduleApi {
    @GET("/api/schedule/weekly")
    fun getScheduleShows(): Single<BaseResponse<ScheduleModel>>
}