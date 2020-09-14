package com.supersports.sportsflashes.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.supersports.sportsflashes.repository.api.NetworkResponse
import com.supersports.sportsflashes.repository.api.STATUS
import com.supersports.sportsflashes.repository.factory.RepositoryFactory

/**
 *Created by Bhanu on 06-07-2020
 */
class ScheduleFragmentViewModel : ViewModel() {
    private val scheduleRepoFactory = RepositoryFactory().scheduleRepo
    private val scheduleShowResponseObserver = MutableLiveData(
        NetworkResponse(
            STATUS.NOT_REQUESTED
        )
    )

    fun getScheduleShows(): MutableLiveData<NetworkResponse> {
        scheduleRepoFactory.getScheduleShowsData(scheduleShowResponseObserver)
        return scheduleShowResponseObserver
    }
}