package com.sports.sportsflashes.repository

import com.google.gson.Gson
import com.sports.sportsflashes.common.applicationlevel.SFApplication
import javax.inject.Inject

class RepositoryFactory {
    init {
        SFApplication.getAppComponent().inject(this)
    }

    @Inject
    lateinit var gson: Gson
    val homeScreenRepo: HomeScreenRepo by lazy {
        HomeScreenRepo(ApiFactory().HOME_API, gson)
    }
}