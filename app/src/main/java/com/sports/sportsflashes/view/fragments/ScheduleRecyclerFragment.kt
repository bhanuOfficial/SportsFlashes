package com.sports.sportsflashes.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import com.sports.sportsflashes.R
import com.sports.sportsflashes.common.application.SFApplication
import com.sports.sportsflashes.common.helper.WeekDaysTags
import com.sports.sportsflashes.common.utils.AppConstant
import com.sports.sportsflashes.model.ScheduleModel
import com.sports.sportsflashes.view.adapters.ScheduleShowsAdapter
import java.text.SimpleDateFormat
import javax.inject.Inject

/**
 *Created by Bhanu on 15-07-2020
 */
class ScheduleRecyclerFragment : Fragment() {
    private lateinit var scheduleModel: ScheduleModel
    private var schedulePosition = -1
    private var weeklist = ArrayList<String>()
    private lateinit var tabLayout: TabLayout
    private lateinit var recyclerViewSchedule: RecyclerView

    fun weekTabs(tabLayout: TabLayout) {
        this.tabLayout = tabLayout
    }

    @Inject
    lateinit var gson: Gson
    private lateinit var nodata: TextView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        SFApplication.getAppComponent().inject(this)
        scheduleModel = gson.fromJson(
            arguments?.getString(AppConstant.BundleExtras.SCHEDULE_MODEL),
            ScheduleModel::class.java
        )!!
        schedulePosition = arguments?.getInt(AppConstant.BundleExtras.SCHEDULE_POSITION)!!
        weeklist = arguments?.getStringArrayList(AppConstant.BundleExtras.WEEKDAY_LIST)!!
        val view = inflater.inflate(R.layout.schedule_recycler_fragment, container, false)
        nodata = view.findViewById(R.id.noData)
        recyclerViewSchedule = view.findViewById(R.id.recyclerViewSchedule)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initSchedulerRecycler()
        tabLayout.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.tag = getDayNameByDate(weeklist[tabLayout.selectedTabPosition])
                when (tab?.tag) {
                    WeekDaysTags.Sunday.name -> {
                        if (scheduleModel.`0`.isEmpty()) {
                            visible()
                        } else {
                            if (this@ScheduleRecyclerFragment::scheduleModel.isInitialized)
                                hide(scheduleModel.`0`)
                        }
                    }
                    WeekDaysTags.Monday.name -> {
                        if (scheduleModel.`1`.isEmpty()) {
                            visible()
                        } else {
                            if (this@ScheduleRecyclerFragment::scheduleModel.isInitialized)
                                hide(scheduleModel.`1`)
                        }
                    }
                    WeekDaysTags.Tuesday.name -> {
                        if (scheduleModel.`2`.isEmpty()) {
                            visible()
                        } else {
                            if (this@ScheduleRecyclerFragment::scheduleModel.isInitialized)
                                hide(scheduleModel.`2`)
                        }
                    }
                    WeekDaysTags.Wednesday.name -> {
                        if (scheduleModel.`3`.isEmpty()) {
                            visible()
                        } else {
                            if (this@ScheduleRecyclerFragment::scheduleModel.isInitialized)
                                hide(scheduleModel.`3`)
                        }
                    }
                    WeekDaysTags.Thursday.name -> {
                        if (scheduleModel.`4`.isEmpty()) {
                            visible()
                        } else {
                            if (this@ScheduleRecyclerFragment::scheduleModel.isInitialized)
                                hide(scheduleModel.`4`)
                        }
                    }
                    WeekDaysTags.Friday.name -> {
                        if (scheduleModel.`5`.isEmpty()) {
                            visible()
                        } else {
                            if (this@ScheduleRecyclerFragment::scheduleModel.isInitialized)
                                hide(scheduleModel.`5`)
                        }
                    }
                    WeekDaysTags.Saturday.name -> {
                        if (scheduleModel.`6`.isEmpty()) {
                            visible()
                        } else {
                            if (this@ScheduleRecyclerFragment::scheduleModel.isInitialized)
                                hide(scheduleModel.`6`)
                        }
                    }
                }
            }

        })
    }

    private fun visible() {
        nodata.visibility = View.VISIBLE
        recyclerViewSchedule.visibility = View.GONE
    }

    private fun hide(list: List<ScheduleModel.WeekScheduleData>) {
        nodata.visibility = View.GONE
        recyclerViewSchedule.visibility = View.VISIBLE
        recyclerViewSchedule.adapter =
            context?.let { ScheduleShowsAdapter(scheduleShowsList = list, context = it) }
    }

    private fun initSchedulerRecycler() {
        recyclerViewSchedule.setHasFixedSize(true)
        recyclerViewSchedule.layoutManager = LinearLayoutManager(activity).apply {
            this.orientation = LinearLayoutManager.VERTICAL
            this.reverseLayout = false
        }
    }

    private fun getDayNameByDate(dateString: String): String {
        val inFormat = SimpleDateFormat("dd/MM/yyyy")
        val date = inFormat.parse(dateString)
        val outFormat = SimpleDateFormat("EEEE")
        return outFormat.format(date)
    }

}