package com.supersports.sportsflashes.common.helper

import com.supersports.sportsflashes.model.MonthEventModel

/**
 *Created by Bhanu on 07-07-2020
 */
interface EventItemSelection {
    fun onEventSelected(position: Int, eventModel: MonthEventModel,listOfSelectedEvent: ArrayList<String>)
    fun setSelectionVisible(select: Boolean)
}