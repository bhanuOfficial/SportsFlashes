package com.sports.sportsflashes.common.helper

import com.sports.sportsflashes.model.FeaturedShows

interface FeaturedShowsImpl {
    fun setFeaturedDetail(featuredShow: FeaturedShows)
}

interface FeaturedShowsListImpl{
    fun setShowsList(featuredShows: List<FeaturedShows>)
}