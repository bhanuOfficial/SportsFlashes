package com.sports.sportsflashes.repository

import com.sports.sportsflashes.common.application.SFApplication
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
}