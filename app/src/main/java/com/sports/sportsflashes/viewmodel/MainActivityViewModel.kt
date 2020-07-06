package com.sports.sportsflashes.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sports.sportsflashes.repository.api.NetworkResponse
import com.sports.sportsflashes.repository.factory.RepositoryFactory
import com.sports.sportsflashes.repository.api.STATUS

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
}