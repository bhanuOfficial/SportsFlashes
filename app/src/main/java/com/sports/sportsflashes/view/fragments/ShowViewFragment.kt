package com.sports.sportsflashes.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.sports.sportsflashes.R
import com.sports.sportsflashes.common.application.SFApplication
import com.sports.sportsflashes.common.utils.AppConstant
import com.sports.sportsflashes.model.FeaturedShows
import kotlinx.android.synthetic.main.dashboard_full_image_show.*
import kotlinx.android.synthetic.main.playable_item_layout.*
import javax.inject.Inject

/**
 *Created by Bhanu on 04-07-2020
 */
class ShowViewFragment : Fragment() {

    private lateinit var featuredShows: FeaturedShows

    @Inject
    lateinit var gson: Gson

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SFApplication.getAppComponent().inject(this)
        arguments?.let {
            featuredShows =
                gson.fromJson(
                    it.getString(AppConstant.BundleExtras.FEATURED_SHOW),
                    FeaturedShows::class.java
                )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.show_view_layout, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewForShow()
    }

    private fun initViewForShow() {
        activity?.let {
            Glide.with(this).load(featuredShows.thumbnail)
                .placeholder(
                    it.resources.getDrawable(
                        R.drawable.default_thumbnail,
                        null
                    )
                )
                .into(showImage)
        }
        showTittle.text = featuredShows.title
        showDescription.text = featuredShows.description
        show_detail_layout.visibility = View.VISIBLE

        showDescriptionDetail.tag = true

        readMore.setOnClickListener {
            if (showDescriptionDetail.tag as Boolean) {
                readMore.setText(R.string.hide_more)
                showDescriptionDetail.maxLines = Int.MAX_VALUE
                showDescriptionDetail.tag = false
            } else {
                readMore.setText(R.string.show_more)
                showDescriptionDetail.maxLines = 2
                showDescriptionDetail.tag = true
            }
        }

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }
}