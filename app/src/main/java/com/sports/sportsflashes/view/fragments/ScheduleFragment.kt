package com.sports.sportsflashes.view.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
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
import com.sports.sportsflashes.view.activites.MainActivity
import com.sports.sportsflashes.view.adapters.CircularShowAdapter
import com.sports.sportsflashes.view.adapters.ScheduleViewPagerAdapter
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
    private lateinit var viewPageAdapter: ScheduleViewPagerAdapter
    private lateinit var activity:MainActivity


    @Inject
    lateinit var gson: Gson

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as MainActivity
    }

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
        activity.appLogo.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,0,0)
        activity.toolbar.setBackgroundColor(resources.getColor(R.color.black,null))
        activity.appLogo.text= "Schedule"
        activity.appLogo.setTextColor(resources.getColor(R.color.red,null))
        return inflater.inflate(R.layout.schedule_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initScheduleShowRecycler()
        getWeekList()
        refreshViewInit()
        getShowsFromNetwork()
    }

    private fun getShowsFromNetwork() {
        activity?.let {
            scheduleFragmentViewModel.getScheduleShows()
                .observe(it, androidx.lifecycle.Observer {
                    if (swipeRefresh != null)
                        swipeRefresh.isRefreshing = false
                    if (it.status == STATUS.SUCCESS) {
                        scheduleModel = it.data as ScheduleModel
                        viewPageAdapter = ScheduleViewPagerAdapter(
                            childFragmentManager,
                            scheduleModel,
                            weekTabs,
                            weekdayList
                        )
                        scheduleViewPager.adapter = viewPageAdapter
                        scheduleViewPager.setCurrentItem(3, true)
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

        scheduleViewPager.setOnTouchListener(OnTouchListener { v, event ->
            swipeRefresh.isEnabled = false
            when (event.action) {
                MotionEvent.ACTION_UP -> swipeRefresh.isEnabled = true
            }
            false
        })
    }


    private fun initScheduleShowRecycler() {
        schedule_shows_recycler.setHasFixedSize(true)
        schedule_shows_recycler.layoutManager = LinearLayoutManager(activity).apply {
            this.orientation = LinearLayoutManager.HORIZONTAL
            this.reverseLayout = false
        }
        if (featuredShows.isNotEmpty())
            schedule_shows_recycler.adapter =
                activity?.let {
                    CircularShowAdapter(featuredShows, {
                        var smallItemWidth = it
                    }, it, true)
                }
    }

    private fun getWeekList() {
        weekdayList.clear()
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
        weekTabs.setupWithViewPager(scheduleViewPager)
        weekTabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                scheduleViewPager.setCurrentItem(weekTabs.selectedTabPosition, true)
            }

        })
    }


}