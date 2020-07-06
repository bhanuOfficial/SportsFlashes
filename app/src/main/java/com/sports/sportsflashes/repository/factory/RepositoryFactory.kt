package com.sports.sportsflashes.repository.factory

import com.google.gson.Gson
import com.sports.sportsflashes.common.application.SFApplication
import com.sports.sportsflashes.repository.factory.ApiFactory
import com.sports.sportsflashes.repository.repo.HomeScreenRepo
import com.sports.sportsflashes.repository.repo.ScheduleRepo
import javax.inject.Inject

class RepositoryFactory {
    init {
        SFApplication.getAppComponent().inject(this)
    }

    @Inject
    lateinit var gson: Gson

    val homeScreenRepo: HomeScreenRepo by lazy {
        HomeScreenRepo(
            ApiFactory().Dashboard_API,
            gson
        )
    }

    val scheduleRepo: ScheduleRepo by lazy {
        ScheduleRepo(
            ApiFactory().scheduleApi,
            gson
        )
    }
}