package com.supersports.sportsflashes.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.supersports.sportsflashes.model.FirebaseRequest
import com.supersports.sportsflashes.repository.api.NetworkResponse
import com.supersports.sportsflashes.repository.api.STATUS
import com.supersports.sportsflashes.repository.factory.RepositoryFactory

/**
 *Created by Bhanu on 02-07-2020
 */
class HomeFragmentViewModel : ViewModel() {
    private val repoFactory = RepositoryFactory().homeScreenRepo
    private val showResponseObserver = MutableLiveData(
        NetworkResponse(
            STATUS.NOT_REQUESTED
        )
    )
    private val firebaseResponseObserver = MutableLiveData(
                NetworkResponse(
                    STATUS.NOT_REQUESTED
                )
            )

    fun getFeaturedShows(): MutableLiveData<NetworkResponse> {
        repoFactory.getFeaturedShowsData(showResponseObserver)
        return showResponseObserver
    }

    fun subscribeFirebase(request: FirebaseRequest): MutableLiveData<NetworkResponse> {
        repoFactory.registerFirebase(firebaseResponseObserver,request)
        return firebaseResponseObserver
    }
}