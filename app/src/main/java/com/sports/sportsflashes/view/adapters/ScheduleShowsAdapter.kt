package com.sports.sportsflashes.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sports.sportsflashes.R
import com.sports.sportsflashes.model.ScheduleModel

/**
 *Created by Bhanu on 03-07-2020
 */
class ScheduleShowsAdapter(private val scheduleShowsList: List<ScheduleModel.WeekScheduleData>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    class ShowItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val showImage: ImageView = itemView.findViewById(R.id.showImage)
        val showTime: TextView = itemView.findViewById(R.id.showTime)
        val showTittle: TextView = itemView.findViewById(R.id.showTittle)
        val showDescription: TextView = itemView.findViewById(R.id.showDescription)
    }

    class LiveShowItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    class NoView(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {

    }

    override fun getItemViewType(position: Int): Int {
        return if (scheduleShowsList[position].__v == 0) {
            ShowView.SHOW.ordinal
        } else {
            ShowView.LIVE_SHOW.ordinal
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
            holder.showTime.text = scheduleShowsList[position].releaseTime
            Glide.with(holder.itemView.context).load(scheduleShowsList[position].thumbnail)
                .placeholder(
                    holder.itemView.context.resources.getDrawable(
                        R.drawable.default_thumbnail,
                        null
                    )
                )
                .into(holder.showImage)
        }
    }

    enum class ShowView {
        SHOW,
        LIVE_SHOW
    }

}