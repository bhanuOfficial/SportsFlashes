package com.sports.sportsflashes.view.fragments

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sports.sportsflashes.R
import com.sports.sportsflashes.common.application.SFApplication
import com.sports.sportsflashes.common.utils.AppConstant
import com.sports.sportsflashes.model.FeaturedShows
import com.sports.sportsflashes.model.ScheduleModel
import com.sports.sportsflashes.repository.api.STATUS
import com.sports.sportsflashes.view.adapters.CircularShowAdapter
import com.sports.sportsflashes.view.adapters.ScheduleShowsAdapter
import com.sports.sportsflashes.viewmodel.ScheduleFragmentViewModel
import kotlinx.android.synthetic.main.schedule_fragment.*
import java.lang.reflect.Type
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ScheduleFragment : Fragment() {
    private var featuredShows = listOf<FeaturedShows>()
    private var weekdayList = arrayListOf<String>()
    private lateinit var scheduleFragmentViewModel: ScheduleFragmentViewModel
    private lateinit var scheduleModel: ScheduleModel

    @Inject
    lateinit var gson: Gson

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SFApplication.getAppComponent().inject(this)
        scheduleFragmentViewModel =
            ViewModelProvider(this).get(ScheduleFragmentViewModel::class.java)
        arguments?.let {
            val featuredListType: Type = object : TypeToken<ArrayList<FeaturedShows?>?>() {}.type
            featuredShows =
                gson.fromJson(
                    it.getString(AppConstant.BundleExtras.FEATURED_SHOW_LIST),
                    featuredListType
                )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.schedule_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initScheduleShowRecycler()
        initSchedulerRecycler()
        getWeekList()
        refreshViewInit()
        getShowsFromNetwork()
    }

    private fun getShowsFromNetwork() {
        activity?.let {
            scheduleFragmentViewModel.getScheduleShows().observe(it, androidx.lifecycle.Observer {
                swipeRefresh.isRefreshing = false
                if (it.status == STATUS.SUCCESS) {
                    scheduleModel = it.data as ScheduleModel
                }
            })
        }
    }

    private fun refreshViewInit() {
        swipeRefresh.isRefreshing = true
        swipeRefresh.setOnRefreshListener {
            swipeRefresh.isRefreshing = true
            getShowsFromNetwork()
        }
    }

    fun initScheduleShowRecycler() {
        schedule_shows_recycler.setHasFixedSize(true)
        schedule_shows_recycler.layoutManager = LinearLayoutManager(activity).apply {
            this.orientation = LinearLayoutManager.HORIZONTAL
            this.reverseLayout = false
        }
        schedule_shows_recycler.adapter =
            activity?.let {
                CircularShowAdapter(featuredShows, {
                    var smallItemWidth = it
                }, it, true)
            }
    }

    fun initSchedulerRecycler() {
        scheduleRecycler.setHasFixedSize(true)
        scheduleRecycler.layoutManager = LinearLayoutManager(activity).apply {
            this.orientation = LinearLayoutManager.VERTICAL
            this.reverseLayout = false
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ScheduleFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

        @JvmStatic
        fun newInstance(): ScheduleFragment {
            return ScheduleFragment()
        }
    }

    private fun getWeekList() {
        val dateFormat = SimpleDateFormat("dd MMM")
        val dateFormatWithDay = SimpleDateFormat("EEE, dd MMM")
        var cal: Calendar = Calendar.getInstance()
        cal.time = cal.time
        val df: DateFormat = SimpleDateFormat("dd/MM/yyyy")
        for (i in 1..3) {
            cal.add(Calendar.DATE, -1)
            weekdayList.add(df.format(cal.time))
        }
        cal = Calendar.getInstance()
        cal.time = (cal.time)
        weekdayList.add(df.format(cal.time))
        for (i in 1..3) {
            cal.add(Calendar.DATE, 1)
            weekdayList.add(df.format(cal.time))
        }

        val byDate: Comparator<String?> =
            object : Comparator<String?> {
                var sdf =
                    SimpleDateFormat("dd/MM/yyyy")

                override fun compare(p0: String?, p1: String?): Int {
                    var d1: Date? = null
                    var d2: Date? = null
                    try {
                        d1 = sdf.parse(p0)
                        d2 = sdf.parse(p1)
                    } catch (e: ParseException) {
                        e.printStackTrace()
                    }
//                    return if (d1!!.time > d2!!.time) -1 else 1 //descending
                    return if (d1!!.time > d2!!.time) 1 else -1    //ascending
                }
            }

        Collections.sort(weekdayList, byDate)
        for (i in weekdayList) {
            val date1 = SimpleDateFormat("dd/MM/yyyy").parse(i)
            when {
                isYesterday(date1!!) -> {
                    weekTabs.addTab(
                        weekTabs.newTab()
                            .setText("Yesterday, ${dateFormat.format(df.parse(i)!!)}")
                            .also { tab -> tab.tag = getDayNameByDate(i) }
                    )
                }
                isTomorrow(date1) -> {
                    weekTabs.addTab(
                        weekTabs.newTab()
                            .setText("Tomorrow, ${dateFormat.format(df.parse(i)!!)}")
                            .also { tab -> tab.tag = getDayNameByDate(i) }
                    )
                }
                isToday(date1) -> {
                    weekTabs.addTab(
                        weekTabs.newTab()
                            .setText("Today, ${dateFormat.format(df.parse(i)!!)}")
                            .also { tab -> tab.tag = getDayNameByDate(i) }
                    )
                }
                else -> {
                    weekTabs.addTab(
                        weekTabs.newTab().setText(dateFormatWithDay.format(df.parse(i)!!))
                            .also { tab -> tab.tag = getDayNameByDate(i) }
                    )
                }
            }

        }
        weekTabs.postDelayed({
            weekTabs.setScrollPosition(3, 0f, true)
        }, 100)
        weekTabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (this@ScheduleFragment::scheduleModel.isInitialized) {
                    when (tab?.tag) {
                        WeekDays.Sunday.name -> {
                            if (scheduleModel.`0`.isEmpty()) {
                                visible()
                            } else {
                                if (this@ScheduleFragment::scheduleModel.isInitialized)
                                    hide(scheduleModel.`0`)
                            }
                        }
                        WeekDays.Monday.name -> {
                            if (scheduleModel.`1`.isEmpty()) {
                                visible()
                            } else {
                                if (this@ScheduleFragment::scheduleModel.isInitialized)
                                    hide(scheduleModel.`1`)
                            }
                        }
                        WeekDays.Tuesday.name -> {
                            if (scheduleModel.`2`.isEmpty()) {
                                visible()
                            } else {
                                if (this@ScheduleFragment::scheduleModel.isInitialized)
                                    hide(scheduleModel.`2`)
                            }
                        }
                        WeekDays.Wednesday.name -> {
                            if (scheduleModel.`3`.isEmpty()) {
                                visible()
                            } else {
                                if (this@ScheduleFragment::scheduleModel.isInitialized)
                                    hide(scheduleModel.`3`)
                            }
                        }
                        WeekDays.Thursday.name -> {
                            if (scheduleModel.`4`.isEmpty()) {
                                visible()
                            } else {
                                if (this@ScheduleFragment::scheduleModel.isInitialized)
                                    hide(scheduleModel.`4`)
                            }
                        }
                        WeekDays.Friday.name -> {
                            if (scheduleModel.`5`.isEmpty()) {
                                visible()
                            } else {
                                if (this@ScheduleFragment::scheduleModel.isInitialized)
                                    hide(scheduleModel.`5`)
                            }
                        }
                        WeekDays.Saturday.name -> {
                            if (scheduleModel.`6`.isEmpty()) {
                                visible()
                            } else {
                                if (this@ScheduleFragment::scheduleModel.isInitialized)
                                    hide(scheduleModel.`6`)
                            }
                        }
                    }

                }
            }

        })
    }

    private fun visible() {
        noData.visibility = View.VISIBLE
        scheduleRecycler.visibility = View.GONE
    }

    private fun hide(list: List<ScheduleModel.WeekScheduleData>) {
        noData.visibility = View.GONE
        scheduleRecycler.visibility = View.VISIBLE
        scheduleRecycler.adapter =
            ScheduleShowsAdapter(scheduleShowsList = list)
    }

    fun isYesterday(d: Date): Boolean {
        return DateUtils.isToday(d.time + DateUtils.DAY_IN_MILLIS)
    }

    fun isTomorrow(d: Date): Boolean {
        return DateUtils.isToday(d.time - DateUtils.DAY_IN_MILLIS)
    }

    fun isToday(d: Date): Boolean {
        return DateUtils.isToday(d.time)
    }

    private fun getDayNameByDate(dateString: String): String {
        val inFormat = SimpleDateFormat("dd/MM/yyyy")
        val date = inFormat.parse(dateString)
        val outFormat = SimpleDateFormat("EEEE")
        return outFormat.format(date)
    }

    private enum class WeekDays {
        Sunday,
        Monday,
        Tuesday,
        Wednesday,
        Thursday,
        Friday,
        Saturday
    }


}