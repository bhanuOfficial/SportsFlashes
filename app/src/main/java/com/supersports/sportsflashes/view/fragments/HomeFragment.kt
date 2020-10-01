package com.supersports.sportsflashes.view.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import co.sodalabs.pager.LoopingPagerLayoutManager
import com.google.android.exoplayer2.ExoPlayer
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.supersports.sportsflashes.R
import com.supersports.sportsflashes.common.application.SFApplication
import com.supersports.sportsflashes.common.helper.FeaturedShowsImpl
import com.supersports.sportsflashes.common.utils.AppConstant
import com.supersports.sportsflashes.common.utils.DateTimeUtils
import com.supersports.sportsflashes.common.utils.DateTimeUtils.calculateTimeBetweenTwoDates
import com.supersports.sportsflashes.common.utils.DateTimeUtils.formatHoursAndMinutes
import com.supersports.sportsflashes.model.*
import com.supersports.sportsflashes.repository.api.NetworkResponse
import com.supersports.sportsflashes.repository.api.STATUS
import com.supersports.sportsflashes.view.activites.MainActivity
import com.supersports.sportsflashes.view.adapters.CircularShowAdapter
import com.supersports.sportsflashes.view.adapters.ImageShowAdapter
import com.supersports.sportsflashes.view.customviewimpl.CircularHorizontalMode
import com.supersports.sportsflashes.viewmodel.HomeFragmentViewModel
import kotlinx.android.synthetic.main.home_fragment.*
import kotlinx.android.synthetic.main.playable_item_layout.*
import org.greenrobot.eventbus.EventBus
import java.lang.reflect.Type
import javax.inject.Inject

/**
 *Created by Bhanu on 02-07-2020
 */
class HomeFragment : Fragment(),
    FeaturedShowsImpl {
    private var token: String? = null
    var smallItemWidth: Int = 0
    var mainItemWidth: Int = 0
    private var draggingView = -1
    var featuredShowslist = listOf<FeaturedShows>()
    private lateinit var animation1: AlphaAnimation
    private lateinit var viewModel: HomeFragmentViewModel
    private lateinit var attachedActivity: Context
    private var created = false
    private var refreshed = false
    private lateinit var preferences: SharedPreferences
    private lateinit var activity: MainActivity
    private lateinit var scrollListener: RecyclerView.OnScrollListener
    private lateinit var snapHelper: PagerSnapHelper

    @Inject
    lateinit var mediaPlayer: ExoPlayer

    @Inject
    lateinit var gson: Gson

    override fun onResume() {
        super.onResume()
        EventBus.getDefault().post(MessageEvent(MessageEvent.HOME_FRAGMENT))
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity = context as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(HomeFragmentViewModel::class.java)
        SFApplication.getAppComponent().inject(this)
        activity.appLogo.setBackgroundResource(
            R.drawable.in_app_logo
        )
        activity.toolbar.setBackgroundColor(resources.getColor(android.R.color.transparent, null))
        activity.appLogo.text = ""
        return inflater.inflate(R.layout.home_fragment, null, false)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        attachedActivity = this.requireContext()
        subscribeFirebase()
        initDashboard()
        setFeaturedShows()
        initRefresh()
    }

    private fun subscribeFirebase() {
        preferences = requireActivity().getSharedPreferences(
            getString(R.string.pref_key),
            Context.MODE_PRIVATE
        )
        token = preferences.getString(AppConstant.FIREBASE_INSTANCE, "")!!
        viewModel.subscribeFirebase(FirebaseRequest(token!!))
            .observe(requireActivity(), Observer<NetworkResponse> {
                if (it.status == STATUS.SUCCESS) {
                    val firebaseSubscription = it.data as FirebaseSubscribeModel
                    Log.d("BHANU", "FIREBASE----> " + Gson().toJson(firebaseSubscription))
                }
            })
    }

    private fun initRefresh() {
        swipeRefresh.isRefreshing = true
        swipeRefresh.setOnRefreshListener {
//            refreshed = true
            swipeRefresh.isRefreshing = true
            setFeaturedShows()
        }
    }

    private fun initDashboard() {
        circularRecycler.setHasFixedSize(true)
        circularRecycler.layoutManager = activity?.let {
            LinearLayoutManager(it).also {
                it.reverseLayout = false
                it.orientation = LinearLayoutManager.HORIZONTAL
            }
        }
        imageCategory.setHasFixedSize(true)
        imageCategory.layoutManager = activity.let {
            LoopingPagerLayoutManager()
            /*LoopingLayoutManager(it).also {
                it.reverseLayout = false
                it.orientation = LinearLayoutManager.HORIZONTAL
            }*/
        }
        snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(imageCategory)



        scrollListener =
            object : RecyclerView.OnScrollListener() {
                var state: Int = -1
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    try {
                        if (draggingView == 1 && recyclerView == imageCategory) {
                            circularRecycler.scrollBy(dx / (mainItemWidth / smallItemWidth), 0)
                            circularRecycler.postDelayed({
                                circularRecycler.smoothScrollToView(
                                    circularRecycler.findViewAtCenter()!!
                                )
                            }, 200)
                            if (state == RecyclerView.SCROLL_STATE_SETTLING) {
                                setFeaturedDetail(featuredShowslist[imageCategory.layoutManager.let { t ->
                                    t!!.getPosition(circularRecycler.findViewAtCenter()!!)
                                }], draggingView)
                            }
                        } else if (draggingView == 2 && recyclerView == circularRecycler) {
                            imageCategory.scrollBy(dx * (mainItemWidth / smallItemWidth), 0)
                            if (state == RecyclerView.SCROLL_STATE_SETTLING) {
                                setFeaturedDetail(featuredShowslist[imageCategory.layoutManager.let { t ->
                                    t!!.getPosition(circularRecycler.findViewAtCenter()!!)
                                }], draggingView)
                            }
                        }

                    } catch (exception: Exception) {
                        exception.printStackTrace()
                    }

                }

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    if (imageCategory == recyclerView && newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                        draggingView = 1
                    } else if (circularRecycler == recyclerView && newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                        draggingView = 2
                    }
                    this.state = newState
                    super.onScrollStateChanged(recyclerView, newState)
                }
            }
        circularRecycler.layoutManager = activity.let { mainActivity ->
            LoopingPagerLayoutManager()
            /*LinearLayoutManager(mainActivity).also {
                it.reverseLayout = false
                it.orientation = LinearLayoutManager.HORIZONTAL
            }*/
        }
        circularRecycler.mViewMode = CircularHorizontalMode()
        circularRecycler.mNeedCenterForce = true
        imageCategory.addOnScrollListener(scrollListener)
        circularRecycler.addOnScrollListener(scrollListener)

        setAlphaForFeaturedChanged()
        if (playCurrentShow != null)
            playCurrentShow.setOnClickListener {
                if (featuredShowslist.isNotEmpty())
                    (attachedActivity as MainActivity).onCurrentShowClicked(featuredShowslist[imageCategory.layoutManager.let { t ->
                        t!!.getPosition(circularRecycler.findViewAtCenter()!!)
                    }])
            }
    }

    private fun setAlphaForFeaturedChanged() {
        animation1 = AlphaAnimation(0.1f, 1.0f)
        animation1.duration = 400
        animation1.fillAfter = true
        showTittle.startAnimation(animation1)
        creatorName.startAnimation(animation1)
    }

    companion object {
        var i = 2
    }

    override fun setFeaturedDetail(featuredShow: FeaturedShows, draggingView: Int) {
        if (draggingView == 1) {
            /*circularRecycler.smoothScrollToPosition(
                (imageCategory.adapter as ImageShowAdapter).getItemPosition()
            )*/
//            circularRecycler.smoothScrollToView(imageCategory.getChildAt((imageCategory.adapter as ImageShowAdapter).getItemPosition()))

        } else if (draggingView == 2) {
            try {
                imageCategory.smoothScrollToPosition(
                    circularRecycler.getChildAdapterPosition(circularRecycler.findViewAtCenter()!!)
                )
                imageCategory.postDelayed({
                    imageCategory.scrollToPosition(
                        circularRecycler.getChildAdapterPosition(
                            circularRecycler.findViewAtCenter()!!
                        )
                    )
                }, 500)
            } catch (exception: java.lang.Exception) {
                exception.printStackTrace()
            }
        }
//        animation1.startNow()
        showTittle.text = featuredShow.title
        creatorName.text = featuredShow.creator
        if (featuredShow.releaseTime.isNotEmpty()) {
            val time = DateTimeUtils.convertServerISOTime(
                AppConstant.DateTime.TIME_FORMAT_HOURS,
                featuredShow.releaseTime
            )
            showTime.text = "$time - ${
                DateTimeUtils.getAdditionalTimeWithDuration(
                    time!!,
                    AppConstant.DateTime.TIME_FORMAT_HOURS,
                    featuredShow.duration
                )
            }"
        } else {
            if (featuredShow.radio) {
                val thumbTokenType: Type = object : TypeToken<ThumbnailData?>() {}.type
                val thumbData: ThumbnailData =
                    gson.fromJson(
                        gson.toJson(featuredShow.thumbnailData),
                        thumbTokenType
                    )
                showTime.text = calculateTimeBetweenTwoDates(
                    AppConstant.DateTime.DATE_TIME_FORMAT_ISO,
                    thumbData.endTime,
                    thumbData.startTime
                )
            } else
                showTime.text = formatHoursAndMinutes(featuredShow.duration)
        }
        if (showLiveStatus != null)
            when {
                featuredShow.radio -> {
                    showLiveStatus.text = "Listen Live"
                }
                featuredShow.type.equals(
                    "podcast",
                    true
                ) -> {
                    showLiveStatus.text = "Listen"
                }
                featuredShow.type.equals(
                    "video",
                    true
                ) -> {
                    showLiveStatus.text = "Watch"
                }
            }
    }


    interface ItemPosition {
        fun getItemPosition(): Int
    }

    private fun setFeaturedShows() {
        activity.let {
            viewModel.getFeaturedShows().observe(
                it,
                Observer<NetworkResponse> { t ->
                    if (t!!.status == STATUS.SUCCESS) {
                        if (swipeRefresh != null)
                            swipeRefresh.isRefreshing = false
                        created = true
                        featuredShowslist = t.data as List<FeaturedShows>
                        (attachedActivity as MainActivity).setShowsList(featuredShowslist)
                        imageCategory?.let {
                            if (featuredShowslist.isNotEmpty())
                                it.adapter =
                                    ImageShowAdapter(featuredShowslist, {
                                        mainItemWidth = it
                                    }, requireContext())
                        }
                        circularRecycler?.let {
                            if (featuredShowslist.isNotEmpty()) {
                                it.adapter =
                                    CircularShowAdapter(featuredShowslist, {
                                        smallItemWidth = it
                                    }, activity, false)

                                if (imageCategory != null)
                                    imageCategory.postDelayed({
                                        try {
                                            imageCategory.scrollToPosition(
                                                circularRecycler.getChildAdapterPosition(
                                                    circularRecycler.findViewAtCenter()!!
                                                )
                                            )
                                            setFeaturedDetail(
                                                featuredShowslist[circularRecycler.getChildAdapterPosition(
                                                    circularRecycler.findViewAtCenter()!!
                                                )],
                                                draggingView
                                            )
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }

                                    }, 50)
                            }
                        }
                    }
                })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}