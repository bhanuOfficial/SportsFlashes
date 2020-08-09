package com.sports.sportsflashes.view.adapters

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
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
import com.sports.sportsflashes.common.utils.DateTimeUtils
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
        var listOfSelectedIndex = ArrayList<String>()
        var selected = false
    }

    @Inject
    lateinit var gson: Gson


    class EventItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val showImage: ImageView = itemView.findViewById(R.id.showImage)
        val showTime: TextView = itemView.findViewById(R.id.showTime)
        val showTittle: TextView = itemView.findViewById(R.id.showTittle)
        val setReminder: TextView = itemView.findViewById(R.id.setReminder)
        val checkedItem: CheckBox = itemView.findViewById(R.id.checkedItem)
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
        holder.checkedItem.isChecked = listOfSelectedIndex.contains(eventList[position]._id)

        holder.showTittle.text = eventList[position].title
        holder.showTime.text = DateTimeUtils.convertServerISOTime(
            AppConstant.DateTime.STD_DATE_FORMAT,
            eventList[position].releaseTime
        )
        Glide.with(holder.itemView.context).load(eventList[position].thumbnail)
            .placeholder(
                holder.itemView.context.resources.getDrawable(
                    R.drawable.default_thumbnail,
                    null
                )
            )
            .into(holder.showImage)

        if (selected) {
            holder.checkedItem.visibility = View.VISIBLE
            holder.setReminder.text = attachedContext.getString(R.string.more)
        } else {
            holder.checkedItem.visibility = View.GONE
            holder.setReminder.text = attachedContext.getString(R.string.set_reminder)
        }

        holder.showImage.setOnClickListener {
            if (selected)
                if (listOfSelectedIndex.contains(eventList[position]._id)) {
                    listOfSelectedIndex.remove(eventList[position]._id)
                } else {
                    listOfSelectedIndex.add(eventList[position]._id)
                }
            (attachedContext as EventsFragment).onEventSelected(
                position,
                eventList[position], listOfSelectedIndex
            )
            notifyDataSetChanged()
        }
        /*holder.detailContainer.setOnClickListener {
            Navigation.findNavController(it.context as Activity, R.id.app_host_fragment)
                .navigate(R.id.action_eventsFragment_to_playableShowFragment, Bundle().apply {
                    this.putString(
                        AppConstant.BundleExtras.EVENT_ITEM,
                        gson.toJson(eventList[position])
                    )
                })
        }
*/

        if (!holder.setReminder.text.contains("More")){
            if (eventList[position].subscribed){
                holder.setReminder.text= "Remove reminder"
            }else{
                holder.setReminder.text= "Set Reminder"
            }
        }
        holder.setReminder.setOnClickListener {
            if (holder.setReminder.text.contains("More")) {
                Navigation.findNavController(it.context as Activity, R.id.app_host_fragment)
                    .navigate(R.id.action_eventsFragment_to_playableShowFragment, Bundle().apply {
                        this.putString(
                            AppConstant.BundleExtras.EVENT_ITEM,
                            gson.toJson(eventList[position])
                        )
                    })
                selected = false
            } else {
                holder.setReminder.text = attachedContext.getString(R.string.more)
                selected = true
                notifyDataSetChanged()
                (attachedContext as EventsFragment).setSelectionVisible(true)
            }
        }
    }
}