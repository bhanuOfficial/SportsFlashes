package com.sports.sportsflashes.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sports.sportsflashes.R

/**
 *Created by Bhanu on 03-07-2020
 */
class ScheduleShowsAdapter(val scheduleShowsList: List<String>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    class ShowItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    class LiveShowItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    class NoView(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {

    }

    override fun getItemViewType(position: Int): Int {
        return if (scheduleShowsList[position].equals("show")) {
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

    }

    enum class ShowView {
        SHOW,
        LIVE_SHOW
    }

}