package com.sports.sportsflashes.repository

import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.sports.sportsflashes.model.BaseResponse
import io.reactivex.Single

class HomeScreenRepo(private val apiService: HomeApi, gson: Gson) : BaseNetworkRepo(gson) {
    fun getHomeScreenData(resposeObserver: MutableLiveData<NetworkResponse>) {
        startNetworkService(
            resposeObserver,
            null,
            null,
            apiService.getHomeApi() as Single<BaseResponse<Any>>)
    }
}
