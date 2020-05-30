package com.sports.sportsflashes.view.activites

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.bekawestberg.loopinglayout.library.LoopingLayoutManager
import com.sports.sportsflashes.R
import com.sports.sportsflashes.common.applicationlevel.SFApplication
import com.sports.sportsflashes.view.adapters.CircularCategoryAdapter
import com.sports.sportsflashes.view.adapters.ImageCategoryAdapter
import com.sports.sportsflashes.view.customviewimpl.CircularHorizontalMode
import com.sports.sportsflashes.viewmodel.MainActivityViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    var smallItemWidth: Int = 0
    var mainItemWidth: Int = 0
    private var draggingView = -1
    private lateinit var viewModel: MainActivityViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SFApplication.getAppComponent().inject(this)
        viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
        viewModel.check()
        setContentView(R.layout.activity_main)
        val list = ArrayList<String>()
        list.add("asdad")
        list.add("asdad")
        list.add("asdad")
        list.add("asdad")
        list.add("asdad")
        list.add("asdad")
        list.add("asdad")
        list.add("asdad")
        list.add("asdad")
        list.add("asdad")
        list.add("asdad")
        list.add("asdad")
        list.add("asdad")
        list.add("asdad")
        list.add("asdad")
        list.add("asdad")
        list.add("asdad")
        list.add("asdad")
        list.add("asdad")

        circularRecycler.setHasFixedSize(true)
        circularRecycler.layoutManager = LoopingLayoutManager(this).also {
            it.reverseLayout = false
            it.orientation = LinearLayoutManager.HORIZONTAL
        }
        /*    val linearSnapHelper = LinearSnapHelper()
            linearSnapHelper.attachToRecyclerView(circularRecycler)*/
        circularRecycler.mViewMode = CircularHorizontalMode()
        circularRecycler.mNeedCenterForce = true
        circularRecycler.adapter =
            CircularCategoryAdapter(list) {
                smallItemWidth = it
            }
        imageCategory.setHasFixedSize(true)
        imageCategory.layoutManager = LoopingLayoutManager(this).also {
            it.reverseLayout = false
            it.orientation = LinearLayoutManager.HORIZONTAL
        }
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(imageCategory)
        imageCategory.adapter =
            ImageCategoryAdapter(list) {
                mainItemWidth = it
            }
        imageCategory.postDelayed(Runnable {
            imageCategory.scrollToPosition(
                circularRecycler.getChildAdapterPosition(
                    circularRecycler.findViewAtCenter()!!
                )
            )
        }, 700)
        val scrollListner: RecyclerView.OnScrollListener =
            object : RecyclerView.OnScrollListener() {
                var state: Int = -1
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (draggingView == 1 && recyclerView == imageCategory) {
                        circularRecycler.scrollBy(dx / (mainItemWidth / smallItemWidth), 0)
                        if (state == RecyclerView.SCROLL_STATE_SETTLING) {
                            circularRecycler.smoothScrollToView(circularRecycler.findViewAtCenter()!!)
                            circularRecycler.smoothScrollToPosition(
                                (imageCategory.adapter as ImageCategoryAdapter).getItemPosition()
                            )
                        }
                    } else if (draggingView == 2 && recyclerView == circularRecycler) {
                        imageCategory.scrollBy(dx * (mainItemWidth / smallItemWidth), 0)
                        if (state == RecyclerView.SCROLL_STATE_SETTLING) {
                            circularRecycler.post {
                                imageCategory.smoothScrollToPosition(
                                    circularRecycler.getChildAdapterPosition(circularRecycler.findViewAtCenter()!!)
                                )
                            }
                        }
                    }
                }

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (imageCategory == recyclerView && newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                        draggingView = 1;
                    } else if (circularRecycler == recyclerView && newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                        draggingView = 2;
                    }
                    this.state = newState
                }
            }
        imageCategory.addOnScrollListener(scrollListner)
        circularRecycler.addOnScrollListener(scrollListner)
    }

    interface ItemPosition {
        fun getItemPosition(): Int
    }

}
