package com.supersports.sportsflashes.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.supersports.sportsflashes.R
import com.supersports.sportsflashes.common.application.SFApplication
import com.supersports.sportsflashes.common.utils.AppConstant
import com.supersports.sportsflashes.model.LiveSeasonModel
import com.supersports.sportsflashes.view.adapters.ScheduleUpcomingShowAdapter
import kotlinx.android.synthetic.main.live_show_pager_fragment.*
import java.lang.reflect.Type
import javax.inject.Inject

/**
 *Created by Bhanu on 19-07-2020
 */
class LiveShowPagerFragment : Fragment() {

    @Inject
    lateinit var gson: Gson

    init {
        SFApplication.getAppComponent().inject(this)
    }

    private lateinit var genericShowList: ArrayList<LiveSeasonModel.GenericUpcomingShows>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        arguments?.let {
            val featuredListType: Type =
                object : TypeToken<ArrayList<LiveSeasonModel.GenericUpcomingShows?>?>() {}.type
            genericShowList =
                gson.fromJson(
                    it.getString(AppConstant.BundleExtras.LIVE_SHOW_UPCOMING_LIST),
                    featuredListType
                )
        }
        val rootView = inflater.inflate(R.layout.live_show_pager_fragment, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {
        liveShowsRecycler.setHasFixedSize(true)
        liveShowsRecycler.layoutManager = LinearLayoutManager(activity).also {
            it.reverseLayout = false
            it.orientation = RecyclerView.VERTICAL
        }
        liveShowsRecycler.adapter = ScheduleUpcomingShowAdapter(genericShowList)
    }

}