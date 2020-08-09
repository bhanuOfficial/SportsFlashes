package com.sports.sportsflashes.view.adapters

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.google.gson.Gson
import com.sports.sportsflashes.common.application.SFApplication
import com.sports.sportsflashes.common.utils.AppConstant
import com.sports.sportsflashes.model.LiveSeasonModel
import com.sports.sportsflashes.view.fragments.LiveShowPagerFragment
import com.sports.sportsflashes.view.fragments.ShowViewFragment
import javax.inject.Inject

/**
 *Created by Bhanu on 20-07-2020
 */
class LiveShowAdapter(
    fm: FragmentManager,
    val liveSeasonModel: LiveSeasonModel
) : FragmentStatePagerAdapter(fm) {
    @Inject
    lateinit var gson: Gson

    init {
        SFApplication.getAppComponent().inject(this)
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                ShowViewFragment().apply {
                    if (liveSeasonModel.live.isNotEmpty()) {
                        this.arguments = Bundle().also {
                            if (liveSeasonModel.live[0].radio){
                                liveSeasonModel.live[0].link = liveSeasonModel.link
                            }
                            it.putString(
                                AppConstant.BundleExtras.FEATURED_SHOW,
                                gson.toJson(liveSeasonModel.live[0])
                            )
                            it.putBoolean(
                                AppConstant.BundleExtras.FROM_SCHEDULE_LIVE,
                                true
                            )
                        }
                    }
                }
            }
            1 -> {
                if (liveSeasonModel.live[0].radio) {
                    for (i in liveSeasonModel.scheduled) {
                        i.description = liveSeasonModel.description
                    }
                }

                LiveShowPagerFragment().apply {
                    this.arguments = Bundle().also {
                        it.putString(
                            AppConstant.BundleExtras.LIVE_SHOW_UPCOMING_LIST,
                            gson.toJson(liveSeasonModel.scheduled)
                        )
                    }
                }
            }
            else -> {
                if (liveSeasonModel.live[0].radio) {
                    for (i in liveSeasonModel.upcoming) {
                        i.description = liveSeasonModel.description
                    }
                }
                LiveShowPagerFragment().apply {
                    this.arguments = Bundle().also {
                        it.putString(
                            AppConstant.BundleExtras.LIVE_SHOW_UPCOMING_LIST,
                            gson.toJson(liveSeasonModel.upcoming)
                        )
                    }
                }
            }
        }

    }

    override fun getCount(): Int {
        return 3
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> {
                "Live"
            }
            1 -> {
                "Schedule"
            }
            else -> {
                "Upcoming"
            }
        }
    }

}