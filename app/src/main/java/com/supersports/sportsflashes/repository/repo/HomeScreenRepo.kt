package com.supersports.sportsflashes.repository.repo

import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.supersports.sportsflashes.model.BaseResponse
import com.supersports.sportsflashes.model.FirebaseRequest
import com.supersports.sportsflashes.repository.api.DashboardApi
import com.supersports.sportsflashes.repository.api.NetworkResponse
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

    fun getCategoriesById(responseObserver: MutableLiveData<NetworkResponse>,categoryId:String) {
        startNetworkService(
            responseObserver,
            null,
            null,
            apiService.getShowsByCategories(categoryId) as Single<BaseResponse<Any>>
        )
    }

    fun registerFirebase(responseObserver: MutableLiveData<NetworkResponse>, request: FirebaseRequest) {
        startNetworkService(
            responseObserver,
            null,
            null,
            apiService.registerFirebase(request) as Single<BaseResponse<Any>>
        )
    }

    fun getLiveRadio(
        responseObserver: MutableLiveData<NetworkResponse>
    ) {
        startNetworkService(
            responseObserver,
            null,
            null,
            apiService.getLiveRadio() as Single<BaseResponse<Any>>
        )
    }

    fun getSearchResult(
        responseObserver: MutableLiveData<NetworkResponse>,
        search: String
    ) {
        startNetworkService(
            responseObserver,
            null,
            null,
            apiService.getSearchShows(search) as Single<BaseResponse<Any>>
        )
    }


}
