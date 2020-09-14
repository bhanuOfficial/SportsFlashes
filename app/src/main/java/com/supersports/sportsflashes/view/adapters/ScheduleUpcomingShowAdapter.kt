package com.supersports.sportsflashes.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.supersports.sportsflashes.R
import com.supersports.sportsflashes.common.utils.AppConstant
import com.supersports.sportsflashes.common.utils.DateTimeUtils
import com.supersports.sportsflashes.model.LiveSeasonModel

/**
 *Created by Bhanu on 20-07-2020
 */
class ScheduleUpcomingShowAdapter(private val list: ArrayList<LiveSeasonModel.GenericUpcomingShows>) :
    RecyclerView.Adapter<ScheduleUpcomingShowAdapter.ItemHolder>() {
    class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val showImage: ImageView = itemView.findViewById(R.id.showImage)
        val showTime: TextView = itemView.findViewById(R.id.showTime)
        val showTittle: TextView = itemView.findViewById(R.id.showTittle)
        val showDescription: TextView = itemView.findViewById(R.id.showDescription)
        val scheduleItemContainer: RelativeLayout =
            itemView.findViewById(R.id.scheduleItemContainer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val rootView =
            LayoutInflater.from(parent.context).inflate(R.layout.schedule_show_item, parent, false)
        return ItemHolder(rootView)
    }


    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.showTittle.text = list[position].title
        holder.showDescription.text = list[position].description
        if (list[position].startTime.isNotEmpty()){
            holder.showTime.text = DateTimeUtils.convertServerISOTime(
                AppConstant.DateTime.STD_DATE_FORMAT,
                list[position].startTime
            )
        }else{
            holder.showTime.text = DateTimeUtils.convertServerISOTime(
                AppConstant.DateTime.STD_DATE_FORMAT,
                list[position].releaseTime
            )
        }
        Glide.with(holder.itemView.context).load(list[position].thumbnail)
            .placeholder(
                holder.itemView.context.resources.getDrawable(
                    R.drawable.default_thumbnail,
                    null
                )
            )
            .into(holder.showImage)
    }
}