package com.sports.sportsflashes.view.adapters

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.sports.sportsflashes.R
import com.sports.sportsflashes.common.application.SFApplication
import com.sports.sportsflashes.common.utils.AppConstant
import com.sports.sportsflashes.common.utils.DateTimeUtils
import com.sports.sportsflashes.model.ScheduleModel
import javax.inject.Inject

/**
 *Created by Bhanu on 03-07-2020
 */
class ScheduleShowsAdapter(
    private val scheduleShowsList: List<ScheduleModel.WeekScheduleData>,
    private val context: Context
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    @Inject
    lateinit var gson: Gson

    init {
        SFApplication.getAppComponent().inject(this)
    }

    class ShowItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val showImage: ImageView = itemView.findViewById(R.id.showImage)
        val showTime: TextView = itemView.findViewById(R.id.showTime)
        val showTittle: TextView = itemView.findViewById(R.id.showTittle)
        val showDescription: TextView = itemView.findViewById(R.id.showDescription)
        val scheduleItemContainer: RelativeLayout =
            itemView.findViewById(R.id.scheduleItemContainer)
    }

    class LiveShowItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val showImage: ImageView = itemView.findViewById(R.id.showImage)
        val showTime: TextView = itemView.findViewById(R.id.showTime)
        val showTittle: TextView = itemView.findViewById(R.id.showTittle)
        val showDescription: TextView = itemView.findViewById(R.id.showDescription)
        val scheduleItemContainer: RelativeLayout =
            itemView.findViewById(R.id.scheduleItemContainer)
    }

    class NoView(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {

    }

    override fun getItemViewType(position: Int): Int {
        return if (scheduleShowsList[position].live) {
            ShowView.LIVE_SHOW.ordinal
        } else {
            ShowView.SHOW.ordinal
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            ShowView.SHOW.ordinal -> {
                val rootView =
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.schedule_show_item, parent, false)
                return ShowItemHolder(rootView)
            }
            ShowView.LIVE_SHOW.ordinal -> {
                val rootView =
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.schedule_show_live_item, parent, false)
                return LiveShowItemHolder(rootView)
            }
            else -> {
                return NoView(null)
            }
        }
    }

    override fun getItemCount(): Int {
        return scheduleShowsList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ShowItemHolder) {
            holder.showTittle.text = scheduleShowsList[position].title
            holder.showDescription.text = scheduleShowsList[position].description
            holder.showTime.text = DateTimeUtils.convertServerISOTime(
                AppConstant.DateTime.STD_DATE_FORMAT,
                scheduleShowsList[position].releaseTime
            )
            Glide.with(holder.itemView.context).load(scheduleShowsList[position].thumbnail)
                .placeholder(
                    holder.itemView.context.resources.getDrawable(
                        R.drawable.default_thumbnail,
                        null
                    )
                )
                .into(holder.showImage)

            holder.scheduleItemContainer.setOnClickListener {
                Navigation.findNavController(context as Activity, R.id.app_host_fragment)
                    .navigate(R.id.playableShowFragment, Bundle().apply {
                        this.putString(
                            AppConstant.BundleExtras.FEATURED_SHOW,
                            gson.toJson(scheduleShowsList[position])
                        )
                    })
            }
        } else if (holder is LiveShowItemHolder) {
            holder.showTittle.text = scheduleShowsList[position].title
            holder.showDescription.text = scheduleShowsList[position].description
            holder.showTime.text = DateTimeUtils.convertServerISOTime(
                AppConstant.DateTime.TIME_FORMAT,
                scheduleShowsList[position].releaseTime
            )
            Glide.with(holder.itemView.context).load(scheduleShowsList[position].thumbnail)
                .placeholder(
                    holder.itemView.context.resources.getDrawable(
                        R.drawable.default_thumbnail,
                        null
                    )
                )
                .into(holder.showImage)

            holder.scheduleItemContainer.setOnClickListener {
                Navigation.findNavController(context as Activity, R.id.app_host_fragment)
                    .navigate(R.id.action_scheduleFragment_to_liveShowFragment, Bundle().apply {
                        this.putString(
                            AppConstant.BundleExtras.LIVE_SHOW_ID,
                            scheduleShowsList[position]._id
                        )
                    })
            }
        }
    }

    enum class ShowView {
        SHOW,
        LIVE_SHOW
    }

}