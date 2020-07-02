package com.sports.sportsflashes.view.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sports.sportsflashes.R
import com.sports.sportsflashes.model.FeaturedShows
import com.sports.sportsflashes.view.activites.MainActivity
import com.sports.sportsflashes.view.fragments.HomeFragment

class CircularShowAdapter(
    private val featuredShowsList: List<FeaturedShows>,
    val onItemSizeCaptured: (Int) -> Unit,
    val context: Context,
    val isMenuShow: Boolean
) :
    RecyclerView.Adapter<CircularShowAdapter.ItemHolder>(), HomeFragment.ItemPosition {
    private var index: Int = -1

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
        Glide.with(holder.itemView.context).load(featuredShowsList[position].icon)
            .placeholder(
                holder.itemView.context.resources.getDrawable(
                    R.drawable.default_thumbnail,
                    null
                )
            )
            .into(holder.image)
    }

    inner class ItemHolder(view: View) : RecyclerView.ViewHolder(view) {
        init {
            itemView.post {
                onItemSizeCaptured(itemView.measuredWidth)
            }
        }

        val image = view.findViewById<ImageView>(R.id.imageCategory)
    }

    override fun getItemPosition(): Int {
        return index
    }

}