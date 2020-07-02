package com.sports.sportsflashes.repository

import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.sports.sportsflashes.model.BaseResponse
import io.reactivex.Single

class HomeScreenRepo(private val apiService: DashboardApi, gson: Gson) : BaseNetworkRepo(gson) {
    fun getFeaturedShowsData(responseObserver: MutableLiveData<NetworkResponse>) {
        startNetworkService(
            responseObserver,
            null,
            null,
            apiService.getFeaturedShows() as Single<BaseResponse<Any>>
//            apiService.getFeaturedShows() as Single<List<Any>>
        )
    }

    fun getCategoriesData(responseObserver: MutableLiveData<NetworkResponse>) {
        startNetworkService(
            responseObserver,
            null,
            null,
            apiService.getCategories() as Single<BaseResponse<Any>>
//            apiService.getCategories() as Single<List<Any>>
        )
    }


}
