package com.supersports.sportsflashes.view.adapters

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.supersports.sportsflashes.R
import com.supersports.sportsflashes.common.application.SFApplication
import com.supersports.sportsflashes.common.utils.AppConstant
import com.supersports.sportsflashes.common.utils.DateTimeUtils
import com.supersports.sportsflashes.model.FeaturedShows
import com.supersports.sportsflashes.model.MessageEvent
import com.supersports.sportsflashes.model.SearchResult
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

/**
 *Created by Bhanu on 18-09-2020
 */
class SearchAdapter(private val context: Context, private val searchResults: List<FeaturedShows>) :
    RecyclerView.Adapter<SearchAdapter.SearchHolder>() {
    @Inject
    lateinit var gson: Gson

    init {
        SFApplication.getAppComponent().inject(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchHolder {
        val rootView =
            LayoutInflater.from(context).inflate(R.layout.schedule_show_item, parent, false)
        return SearchHolder(rootView)
    }

    override fun onBindViewHolder(holder: SearchHolder, position: Int) {
        holder.showTittle.text = searchResults[position].title
        holder.showDescription.text = searchResults[position].description
        holder.showTime.text = DateTimeUtils.convertServerISOTime(
            AppConstant.DateTime.TIME_FORMAT_HOURS,
            searchResults[position].releaseTime
        )
        /* if (searchResults[position].radio) {
             holder.showTime.text = DateTimeUtils.convertServerISOTime(
                 AppConstant.DateTime.TIME_FORMAT_HOURS,
                 searchResults[position].startTime
             )
         } else {*/

//        }

        Glide.with(holder.itemView.context).load(searchResults[position].thumbnail)
            .placeholder(
                holder.itemView.context.resources.getDrawable(
                    R.drawable.default_thumbnail,
                    null
                )
            )
            .into(holder.showImage)

        holder.scheduleItemContainer.setOnClickListener {
            EventBus.getDefault().post(
                MessageEvent(
                    MessageEvent.SEARCH_RESULT,
                    gson.toJson(searchResults[position])
                )
            )

           /* val featuredShows =
                gson.fromJson(
                    gson.toJson(searchResults[position]),
                    FeaturedShows::class.java
                )*/
//            Log.d("BHANU","FEATURED SHOW-- > $featuredShows")
            (context as Activity).finish()
        }
    }

    override fun getItemCount(): Int {
        return searchResults.size
    }

    class SearchHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val showImage: ImageView = itemView.findViewById(R.id.showImage)
        val showTime: TextView = itemView.findViewById(R.id.showTime)
        val showTittle: TextView = itemView.findViewById(R.id.showTittle)
        val playItem: ImageView = itemView.findViewById(R.id.playItem)
        val showDescription: TextView = itemView.findViewById(R.id.showDescription)
        val scheduleItemContainer: RelativeLayout =
            itemView.findViewById(R.id.scheduleItemContainer)
    }
}