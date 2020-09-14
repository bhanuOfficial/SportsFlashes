package com.supersports.sportsflashes.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.supersports.sportsflashes.R
import com.supersports.sportsflashes.common.utils.AppConstant
import com.supersports.sportsflashes.common.utils.DateTimeUtils
import com.supersports.sportsflashes.model.SeasonsEpisode
import com.supersports.sportsflashes.view.fragments.ShowViewFragment

/**
 *Created by Bhanu on 19-07-2020
 */
class MoreEpisodeAdapter(
    private val moreEpisodesList: List<SeasonsEpisode>,
    val showViewFragment: Fragment
) :
    RecyclerView.Adapter<MoreEpisodeAdapter.MoreEpisodesHolder>() {


    class MoreEpisodesHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val showImage: ImageView = itemView.findViewById(R.id.showImage)
        val showTime: TextView = itemView.findViewById(R.id.showTime)
        val showTittle: TextView = itemView.findViewById(R.id.showTittle)
        val playItem: ImageView = itemView.findViewById(R.id.playItem)
        val showDescription: TextView = itemView.findViewById(R.id.showDescription)
        val scheduleItemContainer: RelativeLayout =
            itemView.findViewById(R.id.scheduleItemContainer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoreEpisodesHolder {
        val rootView =
            LayoutInflater.from(parent.context).inflate(R.layout.schedule_show_item, parent, false)
        return MoreEpisodesHolder(rootView)
    }

    override fun getItemCount(): Int {
        return moreEpisodesList.size
    }

    override fun onBindViewHolder(holder: MoreEpisodesHolder, position: Int) {
        holder.showTittle.text = moreEpisodesList[position].title
        holder.showDescription.text = moreEpisodesList[position].description
        holder.playItem.visibility = View.VISIBLE
        holder.showTime.text = DateTimeUtils.convertServerISOTime(
            AppConstant.DateTime.STD_DATE_FORMAT,
            moreEpisodesList[position].releaseTime
        )
        Glide.with(holder.itemView.context).load(moreEpisodesList[position].thumbnail)
            .placeholder(
                holder.itemView.context.resources.getDrawable(
                    R.drawable.default_thumbnail,
                    null
                )
            )
            .into(holder.showImage)
        holder.showImage.setOnClickListener {
            (showViewFragment as ShowViewFragment).onSeasonClick(position)
        }
    }

    interface OnSeasonItemClickedListener {
        fun onSeasonClick(position: Int)
    }
}