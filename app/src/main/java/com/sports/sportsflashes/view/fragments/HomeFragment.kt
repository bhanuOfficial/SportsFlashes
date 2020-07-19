package com.sports.sportsflashes.view.fragments

import android.content.Context
import android.os.Bundle
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
import com.sports.sportsflashes.R
import com.sports.sportsflashes.common.application.SFApplication
import com.sports.sportsflashes.common.helper.FeaturedShowsImpl
import com.sports.sportsflashes.model.FeaturedShows
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
    var smallItemWidth: Int = 0
    var mainItemWidth: Int = 0
    private var draggingView = -1
    var featuredShowslist = listOf<FeaturedShows>()
    private lateinit var animation1: AlphaAnimation
    private lateinit var viewModel: HomeFragmentViewModel
    private lateinit var attachedActivity: Context
    private var created = false

    @Inject
    lateinit var mediaPlayer: ExoPlayer

    override fun onResume() {
        super.onResume()
        EventBus.getDefault().post(MessageEvent(MessageEvent.HOME_FRAGMENT))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(HomeFragmentViewModel::class.java)
        SFApplication.getAppComponent().inject(this)
        return inflater.inflate(R.layout.home_fragment, null, false)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        attachedActivity = this.requireContext()
        initDashboard()
        setFeaturedShows()
    }


    private fun initDashboard() {
        circularRecycler.setHasFixedSize(true)
        circularRecycler.layoutManager = activity?.let {
            LoopingLayoutManager(it).also {
                it.reverseLayout = false
                it.orientation = LinearLayoutManager.HORIZONTAL
            }
        }
        circularRecycler.mViewMode = CircularHorizontalMode()
        circularRecycler.mNeedCenterForce = true

        imageCategory.setHasFixedSize(true)
        imageCategory.layoutManager = activity?.let {
            LoopingLayoutManager(it).also {
                it.reverseLayout = false
                it.orientation = LinearLayoutManager.HORIZONTAL
            }
        }
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(imageCategory)
        val scrollListener: RecyclerView.OnScrollListener =
            object : RecyclerView.OnScrollListener() {
                var state: Int = -1
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (draggingView == 1 && recyclerView == imageCategory) {
                        circularRecycler.scrollBy(dx / (mainItemWidth / smallItemWidth), 0)
                        if (state == RecyclerView.SCROLL_STATE_SETTLING) {
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
        imageCategory.addOnScrollListener(scrollListener)
        circularRecycler.addOnScrollListener(scrollListener)

        setAlphaForFeaturedChanged()

        playCurrentShow.setOnClickListener {
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
        creatorName.text = featuredShow.description
        showTime.text = formatHoursAndMinutes(featuredShow.duration)
        if (featuredShow.seasonsEpisodes[0].live && featuredShow.type.equals("podcast", true)) {
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
                        created = true
                        featuredShowslist = t.data as List<FeaturedShows>
                        (attachedActivity as MainActivity).setShowsList(featuredShowslist)

                        imageCategory?.let {
                            it.adapter =
                                ImageShowAdapter(featuredShowslist, {
                                    mainItemWidth = it
                                }, requireContext())
                        }
                        circularRecycler?.let {
                            it.postDelayed({
                                it.adapter =
                                    CircularShowAdapter(featuredShowslist, {
                                        smallItemWidth = it
                                    }, requireActivity(), false)
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