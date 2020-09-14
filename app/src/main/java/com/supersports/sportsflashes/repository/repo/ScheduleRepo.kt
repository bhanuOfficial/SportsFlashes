package com.supersports.sportsflashes.repository.repo

import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.supersports.sportsflashes.model.BaseResponse
import com.supersports.sportsflashes.model.ReminderReqModel
import com.supersports.sportsflashes.repository.api.NetworkResponse
import com.supersports.sportsflashes.repository.api.ScheduleApi
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

    fun getLiveSeason(responseObserver: MutableLiveData<NetworkResponse>, seasonId: String) {
        startNetworkService(
            responseObserver,
            null,
            null,
            apiService.getSeasonById(seasonId) as Single<BaseResponse<Any>>
        )
    }

    fun getRadioById(responseObserver: MutableLiveData<NetworkResponse>, radioId: String) {
        startNetworkService(
            responseObserver,
            null,
            null,
            apiService.getRadioDetails(radioId) as Single<BaseResponse<Any>>
        )
    }

    fun setReminder(responseObserver: MutableLiveData<NetworkResponse>, request: ReminderReqModel) {
        startNetworkService(
            responseObserver,
            null,
            null,
            apiService.setReminderForShow(request) as Single<BaseResponse<Any>>
        )
    }

    fun removeReminder(responseObserver: MutableLiveData<NetworkResponse>, request: ReminderReqModel) {
        startNetworkService(
            responseObserver,
            null,
            null,
            apiService.removeReminderForShow(request) as Single<BaseResponse<Any>>
        )
    }
}