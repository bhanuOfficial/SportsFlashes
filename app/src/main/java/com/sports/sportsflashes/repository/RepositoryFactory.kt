package com.sports.sportsflashes.repository

import com.google.gson.Gson
import com.sports.sportsflashes.common.application.SFApplication
import javax.inject.Inject

class RepositoryFactory {
    init {
        SFApplication.getAppComponent().inject(this)
    }

    @Inject
    lateinit var gson: Gson
    val homeScreenRepo: HomeScreenRepo by lazy {
        HomeScreenRepo(ApiFactory().Dashboard_API, gson)
    }
}