package com.sports.sportsflashes.view.fragments

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
import com.sports.sportsflashes.R
import com.sports.sportsflashes.common.application.SFApplication
import com.sports.sportsflashes.common.helper.FeaturedShowsImpl
import com.sports.sportsflashes.model.FeaturedShows
import com.sports.sportsflashes.repository.NetworkResponse
import com.sports.sportsflashes.repository.STATUS
import com.sports.sportsflashes.view.activites.MainActivity
import com.sports.sportsflashes.view.activites.MainActivity.Companion.instance
import com.sports.sportsflashes.view.adapters.CircularShowAdapter
import com.sports.sportsflashes.view.adapters.ImageShowAdapter
import com.sports.sportsflashes.view.customviewimpl.CircularHorizontalMode
import com.sports.sportsflashes.viewmodel.HomeFragmentViewModel
import kotlinx.android.synthetic.main.home_fragment.*
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(HomeFragmentViewModel::class.java)
        SFApplication.getAppComponent().inject(this)
        return inflater.inflate(R.layout.home_fragment, null, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initDashboard()
        setFeaturedShows()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
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
                            circularRecycler.post {
                                imageCategory.smoothScrollToPosition(
                                    circularRecycler.getChildAdapterPosition(circularRecycler.findViewAtCenter()!!)
                                )
                            }
                            setFeaturedDetail(featuredShowslist[imageCategory.layoutManager.let { t ->
                                t!!.getPosition(circularRecycler.findViewAtCenter()!!)
                            }])
                        }
                    }
                }

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (imageCategory == recyclerView && newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                        draggingView = 1
                    } else if (circularRecycler == recyclerView && newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                        draggingView = 2
                    }
                    this.state = newState
                }
            }
        imageCategory.addOnScrollListener(scrollListener)
        circularRecycler.addOnScrollListener(scrollListener)


        setAlphaForFeaturedChanged()

        playCurrentShow.setOnClickListener {
            instance.onCurrentShowClicked(featuredShowslist[imageCategory.layoutManager.let { t ->
                t!!.getPosition(circularRecycler.findViewAtCenter()!!)
            }])

        }
    }

    private fun setAlphaForFeaturedChanged() {
        animation1 = AlphaAnimation(0.1f, 1.0f)
        animation1.duration = 400
        animation1.fillAfter = true
        showTittle.startAnimation(animation1)
        showDescription.startAnimation(animation1)
    }

    override fun setFeaturedDetail(featuredShow: FeaturedShows) {
        animation1.startNow()
        showTittle.text = featuredShow.title
        showDescription.text = featuredShow.description
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
                        featuredShowslist = t.data as List<FeaturedShows>
//                        instance.setShowsList(featuredShowslist)
                        circularRecycler.adapter =
                            CircularShowAdapter(featuredShowslist, {
                                smallItemWidth = it
                            }, requireActivity(), false)


                        imageCategory.adapter =
                            ImageShowAdapter(featuredShowslist, {
                                mainItemWidth = it
                            }, requireContext())

                        imageCategory.postDelayed(Runnable {
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
                        }, 200)
                    }
                })
        }
    }
}