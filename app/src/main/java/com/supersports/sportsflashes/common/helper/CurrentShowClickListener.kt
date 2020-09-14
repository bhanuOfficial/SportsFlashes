package com.supersports.sportsflashes.common.helper

import com.supersports.sportsflashes.model.FeaturedShows

/**
 *Created by Bhanu on 02-07-2020
 */
interface CurrentShowClickListener {
    fun onCurrentShowClicked(featuredShows: FeaturedShows)
}