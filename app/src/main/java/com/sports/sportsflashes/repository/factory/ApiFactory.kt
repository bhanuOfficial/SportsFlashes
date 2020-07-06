package com.sports.sportsflashes.repository.factory

import com.sports.sportsflashes.common.application.SFApplication
import com.sports.sportsflashes.repository.api.DashboardApi
import com.sports.sportsflashes.repository.api.ScheduleApi
import retrofit2.Retrofit
import javax.inject.Inject

class ApiFactory {
    init {
        SFApplication.getAppComponent().inject(this)
    }

    @Inject
    lateinit var retrofit: Retrofit

    val Dashboard_API: DashboardApi by lazy {
        retrofit.create(DashboardApi::class.java)
    }

    val scheduleApi : ScheduleApi by lazy {
        retrofit.create(ScheduleApi::class.java)
    }
}