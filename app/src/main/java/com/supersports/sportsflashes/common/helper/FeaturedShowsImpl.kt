package com.supersports.sportsflashes.common.helper

import com.supersports.sportsflashes.model.FeaturedShows

interface FeaturedShowsImpl {
    fun setFeaturedDetail(featuredShow: FeaturedShows, draggingView: Int)
}

interface FeaturedShowsListImpl{
    fun setShowsList(featuredShows: List<FeaturedShows>)
}