package com.sports.sportsflashes.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sports.sportsflashes.R
import com.sports.sportsflashes.model.WeekDays

/**
 *Created by Bhanu on 03-07-2020
 */
class ScheduleTimeAdapter(val weekDays: List<WeekDays>) :
    RecyclerView.Adapter<ScheduleTimeAdapter.WeekDayHolder>() {

    private var selectedIndex = -1

    class WeekDayHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val date: TextView = itemView.findViewById(R.id.date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeekDayHolder {
        val rootView =
            LayoutInflater.from(parent.context).inflate(R.layout.weekday_item, parent, false)
        return WeekDayHolder(rootView)
    }

    override fun getItemCount(): Int {
        return weekDays.size
    }

    override fun onBindViewHolder(holder: WeekDayHolder, position: Int) {
        if (selectedIndex == position) {
            holder.date.setBackgroundResource(R.color.red)
        }else{
            holder.date.setBackgroundResource(R.color.black_transparent)
        }

        if (selectedIndex == -1 && weekDays[position].day != "Today") {
            holder.date.setBackgroundResource(R.color.black_transparent)
        }

        holder.date.text = weekDays[position].date
        holder.date.setOnClickListener {
            selectedIndex = position
            notifyDataSetChanged()
        }
    }
}