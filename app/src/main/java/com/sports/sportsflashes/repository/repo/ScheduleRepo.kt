package com.sports.sportsflashes.repository.repo

import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.sports.sportsflashes.model.BaseResponse
import com.sports.sportsflashes.repository.api.NetworkResponse
import com.sports.sportsflashes.repository.api.ScheduleApi
import io.reactivex.Single

/**
 *Created by Bhanu on 06-07-2020
 */
class ScheduleRepo(private val apiService: ScheduleApi, gson: Gson) : BaseNetworkRepo(gson) {
    fun getScheduleShowsData(responseObserver: MutableLiveData<NetworkResponse>) {
        startNetworkService(
            responseObserver,
            null,
            null,
            apiService.getScheduleShows() as Single<BaseResponse<Any>>
        )
    }

    fun getEvents(responseObserver: MutableLiveData<NetworkResponse>,month:Int) {
        startNetworkService(
            responseObserver,
            null,
            null,
            apiService.getEvents(month) as Single<BaseResponse<Any>>
        )
    }
}