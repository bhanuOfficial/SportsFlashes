package com.sports.sportsflashes.view.adapters

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.sports.sportsflashes.R
import com.sports.sportsflashes.common.utils.AppConstant
import com.sports.sportsflashes.model.FeaturedShows
import com.sports.sportsflashes.view.fragments.HomeFragment
import com.sports.sportsflashes.view.fragments.ReminderFragment
import kotlinx.coroutines.selects.SelectInstance

class ReminderShowAdapter(
    private val featuredShowsList: List<FeaturedShows>,
    val instance:Fragment
) :
    RecyclerView.Adapter<ReminderShowAdapter.ItemHolder>(), HomeFragment.ItemPosition {
    private var index: Int = -1
    private val gson = Gson()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val rootView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.dashboard_show_items_menu, parent, false)
        return ItemHolder(rootView)
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
            (instance as ReminderFragment).onItemClicked(featuredShowsList[position])
        }
    }

    inner class ItemHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.imageCategory)
        val circularItemContainer: CardView = view.findViewById(R.id.circularItemContainer)
    }

    override fun getItemPosition(): Int {
        return index
    }

    interface OnReminderItemClickListner{
        fun onItemClicked(featuredShows: FeaturedShows)
    }
}