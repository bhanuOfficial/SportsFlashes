package com.sports.sportsflashes.view.adapters

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.sports.sportsflashes.R
import com.sports.sportsflashes.common.application.SFApplication
import com.sports.sportsflashes.common.utils.AppConstant
import com.sports.sportsflashes.model.MonthEventModel
import com.sports.sportsflashes.view.fragments.EventsFragment
import javax.inject.Inject

/**
 *Created by Bhanu on 07-07-2020
 */
class EventsAdapter(
    private val eventList: List<MonthEventModel>,
    private val attachedContext: Fragment
) :
    RecyclerView.Adapter<EventsAdapter.EventItemHolder>() {
    init {
        SFApplication.getAppComponent().inject(this)
    }

    companion object {
        var listOfSelectedIndex = ArrayList<Int>()
        var selected = false
    }

    @Inject
    lateinit var gson: Gson


    class EventItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val showImage: ImageView = itemView.findViewById(R.id.showImage)
        val showTime: TextView = itemView.findViewById(R.id.showTime)
        val showTittle: TextView = itemView.findViewById(R.id.showTittle)
        val setReminder: TextView = itemView.findViewById(R.id.setReminder)
        val selectedItemView: ImageView = itemView.findViewById(R.id.selectedItemView)
        val detailContainer: LinearLayout = itemView.findViewById(R.id.detailContainer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventItemHolder {
        var rootView =
            LayoutInflater.from(parent.context).inflate(R.layout.event_item, parent, false)
        return EventItemHolder(rootView)
    }

    override fun getItemCount(): Int {
        return eventList.size
    }

    override fun onBindViewHolder(holder: EventItemHolder, position: Int) {
        if (listOfSelectedIndex.contains(position)) {
            holder.selectedItemView.visibility = View.VISIBLE
        } else {
            holder.selectedItemView.visibility = View.GONE
        }
        holder.showTittle.text = eventList[position].title
        holder.showTime.text = eventList[position].releaseTime
        Glide.with(holder.itemView.context).load(eventList[position].thumbnail)
            .placeholder(
                holder.itemView.context.resources.getDrawable(
                    R.drawable.default_thumbnail,
                    null
                )
            )
            .into(holder.showImage)

        holder.showImage.setOnClickListener {
            if (selected)
                if (listOfSelectedIndex.contains(position)) {
                    listOfSelectedIndex.remove(position)
                } else {
                    listOfSelectedIndex.add(position)
                }
            (attachedContext as EventsFragment).onEventSelected(
                position,
                eventList[position], listOfSelectedIndex
            )
            notifyDataSetChanged()
        }
        holder.detailContainer.setOnClickListener {
            Navigation.findNavController(it.context as Activity, R.id.app_host_fragment)
                .navigate(R.id.action_eventsFragment_to_playableShowFragment, Bundle().apply {
                    this.putString(
                        AppConstant.BundleExtras.EVENT_ITEM,
                        gson.toJson(eventList[position])
                    )
                })
        }

        holder.setReminder.setOnClickListener {
            selected = true
            (attachedContext as EventsFragment).setSelectionVisible(true)
        }
    }
}