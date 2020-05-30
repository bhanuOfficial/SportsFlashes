package com.sports.sportsflashes.repository

import com.sports.sportsflashes.common.applicationlevel.SFApplication
import retrofit2.Retrofit
import javax.inject.Inject

class ApiFactory {
    init {
        SFApplication.getAppComponent().inject(this)
    }

    @Inject
    lateinit var retrofit: Retrofit

    val HOME_API: HomeApi by lazy {
        retrofit.create(HomeApi::class.java)
    }
}