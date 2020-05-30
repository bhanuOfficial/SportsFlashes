package com.sports.sportsflashes.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sports.sportsflashes.repository.NetworkResponse
import com.sports.sportsflashes.repository.RepositoryFactory
import com.sports.sportsflashes.repository.STATUS

class MainActivityViewModel : ViewModel() {
    val repoFactory = RepositoryFactory().homeScreenRepo
    val responseObserver = MutableLiveData(NetworkResponse(STATUS.NOT_REQUESTED))

    fun check() {
        repoFactory.getHomeScreenData(responseObserver)
    }
}