package com.supersports.sportsflashes.view.adapters

import android.app.Activity
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
import com.supersports.sportsflashes.R
import com.supersports.sportsflashes.common.application.SFApplication
import com.supersports.sportsflashes.common.utils.AppConstant
import com.supersports.sportsflashes.model.FeaturedShows
import javax.inject.Inject

/**
 *Created by Bhanu on 20-07-2020
 */
class CategoryShowAdapter(private val list: ArrayList<FeaturedShows>) :
    RecyclerView.Adapter<CategoryShowAdapter.ItemHolder>() {
    @Inject
    lateinit var gson: Gson

    init {
        SFApplication.getAppComponent().inject(this)
    }

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
            LayoutInflater.from(parent.context).inflate(R.layout.category_show_item, parent, false)
        return ItemHolder(rootView)
    }


    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        if(list[position].seasons.isNotEmpty() && list[position].seasons[list[position].seasons.size - 1].episodes.isNotEmpty()){
            holder.showTittle.text =
                "Season ${list[position].seasons.size}, Episode ${list[position].seasons[list[position].seasons.size - 1].episodes.size}"
        }
        holder.showDescription.text = "Play Time: " + list[position].duration
        holder.showTime.text = list[position].title
        /* DateTimeUtils.convertServerISOTime(
             AppConstant.DateTime.STD_DATE_FORMAT,
             list[position].releaseTime
         )*/
        Glide.with(holder.itemView.context).load(list[position].thumbnail)
            .placeholder(
                holder.itemView.context.resources.getDrawable(
                    R.drawable.default_thumbnail,
                    null
                )
            )
            .into(holder.showImage)

        holder.scheduleItemContainer.setOnClickListener {
            Navigation.findNavController(it.context as Activity, R.id.app_host_fragment)
                .navigate(R.id.playableShowFragment, Bundle().apply {
                    this.putString(
                        AppConstant.BundleExtras.FEATURED_SHOW,
                        gson.toJson(list[position])
                    )
                })
        }
    }
}