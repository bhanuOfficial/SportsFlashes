package com.sports.sportsflashes.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sports.sportsflashes.repository.NetworkResponse
import com.sports.sportsflashes.repository.RepositoryFactory
import com.sports.sportsflashes.repository.STATUS

/**
 *Created by Bhanu on 02-07-2020
 */
class HomeFragmentViewModel : ViewModel() {
    private val repoFactory = RepositoryFactory().homeScreenRepo
    private val showResponseObserver = MutableLiveData(NetworkResponse(STATUS.NOT_REQUESTED))

    fun getFeaturedShows(): MutableLiveData<NetworkResponse> {
        repoFactory.getFeaturedShowsData(showResponseObserver)
        return showResponseObserver
    }
}