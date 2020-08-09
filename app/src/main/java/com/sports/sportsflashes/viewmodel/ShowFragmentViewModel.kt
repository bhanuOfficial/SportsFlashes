package com.sports.sportsflashes.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sports.sportsflashes.model.ReminderReqModel
import com.sports.sportsflashes.repository.api.NetworkResponse
import com.sports.sportsflashes.repository.api.STATUS
import com.sports.sportsflashes.repository.factory.RepositoryFactory

/**
 *Created by Bhanu on 09-08-2020
 */
class ShowFragmentViewModel : ViewModel() {
    private val scheduleRepoFactory = RepositoryFactory().scheduleRepo
    private val reminderResponseObserver = MutableLiveData(
        NetworkResponse(
            STATUS.NOT_REQUESTED
        )
    )

    fun setReminder(requestBody: ReminderReqModel): MutableLiveData<NetworkResponse> {
        scheduleRepoFactory.setReminder(reminderResponseObserver,requestBody)
        return reminderResponseObserver
    }

    fun removeReminder(requestBody: ReminderReqModel): MutableLiveData<NetworkResponse> {
        scheduleRepoFactory.removeReminder(reminderResponseObserver, requestBody)
        return reminderResponseObserver
    }
}