package com.sports.sportsflashes.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.sports.sportsflashes.R
import com.sports.sportsflashes.common.utils.AppConstant
import com.sports.sportsflashes.model.LiveSeasonModel
import com.sports.sportsflashes.repository.api.STATUS
import com.sports.sportsflashes.view.adapters.LiveShowAdapter
import com.sports.sportsflashes.viewmodel.LiveShowFragmentViewModel
import kotlinx.android.synthetic.main.live_show_fragment.*

/**
 *Created by Bhanu on 20-07-2020
 */
class LiveShowFragment : Fragment() {
    private lateinit var sessionId: String
    private val liveShowViewModel by lazy {
        ViewModelProvider(this).get(LiveShowFragmentViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        sessionId = arguments?.getString(AppConstant.BundleExtras.LIVE_SHOW_ID).toString()

        val rootView = inflater.inflate(R.layout.live_show_fragment, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getLiveSeasonById()
    }

    private fun getLiveSeasonById() {
        liveShowViewModel.getLiveSeasonById(sessionId!!).observe(requireActivity(), Observer {
            if (it.status == STATUS.SUCCESS) {
                liveShowViewPager.adapter =
                    LiveShowAdapter(childFragmentManager, it.data as LiveSeasonModel)
                liveShowTabs.setupWithViewPager(liveShowViewPager)
            }
        })
    }
}