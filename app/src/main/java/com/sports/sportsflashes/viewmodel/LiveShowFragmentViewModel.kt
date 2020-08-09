package com.sports.sportsflashes.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sports.sportsflashes.repository.api.NetworkResponse
import com.sports.sportsflashes.repository.api.STATUS
import com.sports.sportsflashes.repository.factory.RepositoryFactory

/**
 *Created by Bhanu on 20-07-2020
 */
class LiveShowFragmentViewModel : ViewModel(){
    private val scheduleRepoFactory = RepositoryFactory().scheduleRepo
    private val liveShowResponseObserver = MutableLiveData(
        NetworkResponse(
            STATUS.NOT_REQUESTED
        )
    )

    fun getLiveSeasonById(seasonId:String): MutableLiveData<NetworkResponse> {
        scheduleRepoFactory.getLiveSeason(liveShowResponseObserver,seasonId)
        return liveShowResponseObserver
    }

    fun getRadioById(radioId: String): MutableLiveData<NetworkResponse> {
        scheduleRepoFactory.getRadioById(liveShowResponseObserver, radioId)
        return liveShowResponseObserver
    }
}