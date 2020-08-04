package com.sports.sportsflashes.view.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
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
import com.bekawestberg.loopinglayout.library.LoopingLayoutManager
import com.google.android.exoplayer2.ExoPlayer
import com.google.gson.Gson
import com.sports.sportsflashes.R
import com.sports.sportsflashes.common.application.SFApplication
import com.sports.sportsflashes.common.helper.FeaturedShowsImpl
import com.sports.sportsflashes.common.utils.AppConstant
import com.sports.sportsflashes.common.utils.DateTimeUtils
import com.sports.sportsflashes.model.FeaturedShows
import com.sports.sportsflashes.model.FirebaseRequest
import com.sports.sportsflashes.model.FirebaseSubscribeModel
import com.sports.sportsflashes.model.MessageEvent
import com.sports.sportsflashes.repository.api.NetworkResponse
import com.sports.sportsflashes.repository.api.STATUS
import com.sports.sportsflashes.view.activites.MainActivity
import com.sports.sportsflashes.view.adapters.CircularShowAdapter
import com.sports.sportsflashes.view.adapters.ImageShowAdapter
import com.sports.sportsflashes.view.customviewimpl.CircularHorizontalMode
import com.sports.sportsflashes.viewmodel.HomeFragmentViewModel
import kotlinx.android.synthetic.main.home_fragment.*
import kotlinx.android.synthetic.main.playable_item_layout.*
import org.greenrobot.eventbus.EventBus
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

    @Inject
    lateinit var mediaPlayer: ExoPlayer

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
        activity.appLogo.setCompoundDrawablesRelativeWithIntrinsicBounds(
            R.drawable.in_app_logo,
            0,
            0,
            0
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

        /* imageCategory.setOnTouchListener(View.OnTouchListener { v, event ->
             swipeRefresh.isEnabled = false
             when (event.action) {
                 MotionEvent.ACTION_UP -> swipeRefresh.isEnabled = true
                 MotionEvent.ACTION_DOWN->swipeRefresh.isEnabled=true
             }
             true
         })
         circularRecycler.setOnTouchListener(View.OnTouchListener { v, event ->
             swipeRefresh.isEnabled = false
             when (event.action) {
                 MotionEvent.ACTION_UP -> swipeRefresh.isEnabled = true
             }
             false
         })*/
    }

    private fun initDashboard() {
        circularRecycler.setHasFixedSize(true)
        circularRecycler.layoutManager= activity?.let {
            LinearLayoutManager(it).also {
                it.reverseLayout = false
                it.orientation = LinearLayoutManager.HORIZONTAL
            }
        }
        imageCategory.setHasFixedSize(true)
        imageCategory.layoutManager = activity?.let {
            LoopingLayoutManager(it).also {
                it.reverseLayout = false
                it.orientation = LinearLayoutManager.HORIZONTAL
            }
        }
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(imageCategory)
        scrollListener =
            object : RecyclerView.OnScrollListener() {
                var state: Int = -1
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (draggingView == 1 && recyclerView == imageCategory) {
                        circularRecycler.scrollBy(dx / (mainItemWidth / smallItemWidth), 0)
                        if (state == RecyclerView.SCROLL_STATE_SETTLING) {
//                            circularRecycler.smoothScrollToView(imageCategory.getChildAt(imageCategory.getChildLayoutPosition(circularRecycler.findViewAtCenter()!!)))

                            circularRecycler.smoothScrollToView(circularRecycler.findViewAtCenter()!!)
                            circularRecycler.smoothScrollToPosition(
                                (imageCategory.adapter as ImageShowAdapter).getItemPosition()
                            )
                            setFeaturedDetail(featuredShowslist[imageCategory.layoutManager.let { t ->
                                t!!.getPosition(circularRecycler.findViewAtCenter()!!)
                            }])
                        }
                    } else if (draggingView == 2 && recyclerView == circularRecycler) {
                        imageCategory.scrollBy(dx * (mainItemWidth / smallItemWidth), 0)
                        if (state == RecyclerView.SCROLL_STATE_SETTLING) {
                            recyclerView.getChildAdapterPosition(circularRecycler.findViewAtCenter()!!)
                                .let {
                                    imageCategory.smoothScrollToPosition(it)
                                }
                            setFeaturedDetail(featuredShowslist[imageCategory.layoutManager.let { t ->
                                t!!.getPosition(circularRecycler.findViewAtCenter()!!)
                            }])
                        }
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

        Handler().postDelayed({
            circularRecycler.layoutManager = activity?.let {
                LoopingLayoutManager(it).also {
                    it.reverseLayout = false
                    it.orientation = LinearLayoutManager.HORIZONTAL
                }
            }
            circularRecycler.mViewMode = CircularHorizontalMode()
            circularRecycler.mNeedCenterForce = true
            imageCategory.addOnScrollListener(scrollListener)
            circularRecycler.addOnScrollListener(scrollListener)
        }, 1000)

        setAlphaForFeaturedChanged()

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

    override fun setFeaturedDetail(featuredShow: FeaturedShows) {
        animation1.startNow()
        showTittle.text = featuredShow.title
        creatorName.text = featuredShow.creator
        if (featuredShow.releaseTime.isNotEmpty()) {
            val time = DateTimeUtils.convertServerISOTime(
                AppConstant.DateTime.TIME_FORMAT_HOURS,
                featuredShow.releaseTime
            )
            showTime.text = "$time - ${DateTimeUtils.getAdditionalTimeWithDuration(
                time!!,
                AppConstant.DateTime.TIME_FORMAT_HOURS,
                featuredShow.duration
            )}"
        } else {
            showTime.text = formatHoursAndMinutes(featuredShow.duration)
        }
        if (featuredShow.seasonsEpisodes.isNotEmpty() && featuredShow.seasonsEpisodes[0].live && featuredShow.type.equals(
                "podcast",
                true
            )
        ) {
            showLiveStatus.text = "Listen Live"
        } else {
            showLiveStatus.text = "Watch Live"
        }
    }

    private fun formatHoursAndMinutes(totalMinutes: Int): String? {
        var minutes = (totalMinutes % 60).toString()
        minutes = if (minutes.length == 1) "0$minutes" else minutes
        return if ((totalMinutes / 60).toString() == "0") {
            if (minutes.length == 1) "$minutes min" else "$minutes mins"
        } else
            ((totalMinutes / 60).toString()) + "h " + minutes + "m"
    }


    interface ItemPosition {
        fun getItemPosition(): Int
    }

    private fun setFeaturedShows() {
        activity?.let {
            viewModel.getFeaturedShows().observe(
                it,
                Observer<NetworkResponse> { t ->
                    if (t!!.status == STATUS.SUCCESS) {
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
                            it.postDelayed({
                                if (featuredShowslist.isNotEmpty()) {
                                    it.adapter =
                                        CircularShowAdapter(featuredShowslist, {
                                            smallItemWidth = it
                                        }, requireActivity(), false)

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
                                                    )]
                                                )
                                            } catch (e: Exception) {
                                                e.printStackTrace()
                                            }

                                        }, 50)
                                }
                            }, 1000)
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