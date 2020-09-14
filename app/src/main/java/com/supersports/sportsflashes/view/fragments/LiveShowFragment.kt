package com.supersports.sportsflashes.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.supersports.sportsflashes.R
import com.supersports.sportsflashes.common.utils.AppConstant
import com.supersports.sportsflashes.model.LiveSeasonModel
import com.supersports.sportsflashes.repository.api.STATUS
import com.supersports.sportsflashes.view.adapters.LiveShowAdapter
import com.supersports.sportsflashes.viewmodel.LiveShowFragmentViewModel
import kotlinx.android.synthetic.main.live_show_fragment.*

/**
 *Created by Bhanu on 20-07-2020
 */
class LiveShowFragment : Fragment() {
    private lateinit var sessionId: String
    private lateinit var radioId: String
    private val liveShowViewModel by lazy {
        ViewModelProvider(this).get(LiveShowFragmentViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        sessionId = arguments?.getString(AppConstant.BundleExtras.LIVE_SHOW_ID).toString()
        radioId = arguments?.getString(AppConstant.BundleExtras.LIVE_RADIO_ID).toString()

        val rootView = inflater.inflate(R.layout.live_show_fragment, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getLiveSeasonById()
    }

    private fun getLiveSeasonById() {
        if (sessionId!="null")
            liveShowViewModel.getLiveSeasonById(sessionId).observe(requireActivity(), Observer {
                if (it.status == STATUS.SUCCESS) {
                    liveShowViewPager.adapter =
                        LiveShowAdapter(childFragmentManager, it.data as LiveSeasonModel)
                    liveShowTabs.setupWithViewPager(liveShowViewPager)
                }
            })
        if (radioId!="null") {
            liveShowViewModel.getRadioById(radioId).observe(requireActivity(), Observer {
                if (it.status == STATUS.SUCCESS) {
                    liveShowViewPager.adapter =
                        LiveShowAdapter(childFragmentManager, it.data as LiveSeasonModel)
                    liveShowTabs.setupWithViewPager(liveShowViewPager)
                }
            })
        }
    }
}