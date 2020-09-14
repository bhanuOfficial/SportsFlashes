package com.supersports.sportsflashes.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.supersports.sportsflashes.repository.api.NetworkResponse
import com.supersports.sportsflashes.repository.api.STATUS
import com.supersports.sportsflashes.repository.factory.RepositoryFactory

class MainActivityViewModel : ViewModel() {
    private val repoFactory = RepositoryFactory().homeScreenRepo
    private val categoriesResponseObserver = MutableLiveData(
        NetworkResponse(
            STATUS.NOT_REQUESTED
        )
    )

    fun getCategories(): MutableLiveData<NetworkResponse> {
        repoFactory.getCategoriesData(categoriesResponseObserver)
        return categoriesResponseObserver
    }

    private val showResponseObserver = MutableLiveData(
        NetworkResponse(
            STATUS.NOT_REQUESTED
        )
    )

    fun getFeaturedShows(): MutableLiveData<NetworkResponse> {
        repoFactory.getFeaturedShowsData(showResponseObserver)
        return showResponseObserver
    }

    fun getLiveRadio(): MutableLiveData<NetworkResponse> {
        repoFactory.getLiveRadio(showResponseObserver)
        return showResponseObserver
    }
}