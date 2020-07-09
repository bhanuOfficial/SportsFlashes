package com.sports.sportsflashes.view.adapters

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.sports.sportsflashes.R
import com.sports.sportsflashes.common.utils.AppConstant
import com.sports.sportsflashes.model.FeaturedShows
import com.sports.sportsflashes.view.fragments.HomeFragment
import com.sports.sportsflashes.view.fragments.ScheduleFragment

class CircularShowAdapter(
    private val featuredShowsList: List<FeaturedShows>,
    val onItemSizeCaptured: (Int) -> Unit,
    val context: Context,
    private val isMenuShow: Boolean
) :
    RecyclerView.Adapter<CircularShowAdapter.ItemHolder>(), HomeFragment.ItemPosition {
    private var index: Int = -1
    private val gson = Gson()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        return if (isMenuShow) {
            val rootView =
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.dashboard_show_items_menu, parent, false)
            ItemHolder(rootView)
        } else {
            val rootView =
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.dashboard_show_items, parent, false)
            ItemHolder(rootView)
        }
    }

    override fun getItemCount(): Int {
        return featuredShowsList.size
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        index = position
        Glide.with(holder.itemView.context).load(featuredShowsList[position].thumbnail)
            .placeholder(
                holder.itemView.context.resources.getDrawable(
                    R.drawable.default_thumbnail,
                    null
                )
            )
            .into(holder.image)

        holder.circularItemContainer.setOnClickListener {
            if (isMenuShow) {
                (context as Activity).onBackPressed()
            }
           findNavController(context as Activity, R.id.app_host_fragment)
               .navigate(R.id.action_homeFragment_to_playableShowFragment, Bundle().apply {
                    this.putString(
                        AppConstant.BundleExtras.FEATURED_SHOW,
                        gson.toJson(featuredShowsList[position])
                    )
                })
        }
    }

    inner class ItemHolder(view: View) : RecyclerView.ViewHolder(view) {
        init {
            itemView.post {
                onItemSizeCaptured(itemView.measuredWidth)
            }
        }

        val image: ImageView = view.findViewById(R.id.imageCategory)
        val circularItemContainer: CardView = view.findViewById(R.id.circularItemContainer)
    }

    override fun getItemPosition(): Int {
        return index
    }

}