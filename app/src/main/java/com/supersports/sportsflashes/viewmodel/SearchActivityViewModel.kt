package com.supersports.sportsflashes.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.supersports.sportsflashes.repository.api.NetworkResponse
import com.supersports.sportsflashes.repository.api.STATUS
import com.supersports.sportsflashes.repository.factory.RepositoryFactory

class SearchActivityViewModel : ViewModel() {
    private val repoFactory = RepositoryFactory().homeScreenRepo
    private val searchResponseObserver = MutableLiveData(
        NetworkResponse(
            STATUS.NOT_REQUESTED
        )
    )

    fun getSearchResult(search: String): MutableLiveData<NetworkResponse> {
        repoFactory.getSearchResult(searchResponseObserver,search)
        return searchResponseObserver
    }
}