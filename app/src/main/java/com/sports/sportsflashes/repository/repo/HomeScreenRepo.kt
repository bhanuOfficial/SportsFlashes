package com.sports.sportsflashes.repository.repo

import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.sports.sportsflashes.model.BaseResponse
import com.sports.sportsflashes.repository.api.NetworkResponse
import com.sports.sportsflashes.repository.api.DashboardApi
import io.reactivex.Single

class HomeScreenRepo(private val apiService: DashboardApi, gson: Gson) : BaseNetworkRepo(gson) {
    fun getFeaturedShowsData(responseObserver: MutableLiveData<NetworkResponse>) {
        startNetworkService(
            responseObserver,
            null,
            null,
            apiService.getFeaturedShows() as Single<BaseResponse<Any>>
        )
    }

    fun getCategoriesData(responseObserver: MutableLiveData<NetworkResponse>) {
        startNetworkService(
            responseObserver,
            null,
            null,
            apiService.getCategories() as Single<BaseResponse<Any>>
        )
    }


}