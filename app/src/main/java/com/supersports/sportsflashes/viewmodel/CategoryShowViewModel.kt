package com.supersports.sportsflashes.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.supersports.sportsflashes.repository.api.NetworkResponse
import com.supersports.sportsflashes.repository.factory.RepositoryFactory
import com.supersports.sportsflashes.repository.api.STATUS

/**
 *Created by Bhanu on 02-07-2020
 */
class CategoryShowViewModel : ViewModel() {
    private val repoFactory = RepositoryFactory().homeScreenRepo
    private val showResponseObserver = MutableLiveData(
        NetworkResponse(
            STATUS.NOT_REQUESTED
        )
    )

    fun getCategoryShow(categoryId:String): MutableLiveData<NetworkResponse> {
        repoFactory.getCategoriesById(showResponseObserver,categoryId)
        return showResponseObserver
    }
}