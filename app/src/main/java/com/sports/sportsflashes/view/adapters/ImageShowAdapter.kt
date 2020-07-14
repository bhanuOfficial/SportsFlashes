package com.sports.sportsflashes.view.adapters

import android.content.Context
import android.util.Log
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


class ImageShowAdapter(
    private val featuredShowsList: List<FeaturedShows>,
    val onItemSizeCaptured: (Int) -> Unit,
    val context: Context
) : RecyclerView.Adapter<ImageShowAdapter.ItemHolder>(), HomeFragment.ItemPosition {
    companion object {
        var pos: Int = 0
    }

    private var index: Int = -1
    private var view: View? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val rootView =
            LayoutInflater.from(parent.context).inflate(R.layout.dashboard_full_image_show, parent, false)
        return ItemHolder(rootView)
    }

    override fun getItemCount(): Int {
//        return Int.MAX_VALUE
        return featuredShowsList.size
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
//        holder.textView.text = "" + position
        view = holder.itemView
        index= position
        Glide.with(holder.itemView.context).load(featuredShowsList[position].thumbnail)
            .placeholder(
                holder.itemView.context.resources.getDrawable(
                    R.drawable.default_thumbnail,
                    null
                )
            )
            .into(holder.showThumb)
    }

    inner class ItemHolder(view: View) : RecyclerView.ViewHolder(view) {
        //        val image = view.findViewById<ImageView>(R.id.imageCategory)
        val showThumb: ImageView = view.findViewById(R.id.showImage)

        init {
            itemView.post {
                onItemSizeCaptured(itemView.measuredWidth)
            }
        }
    }

    override fun getItemPosition(): Int {
        return index
    }
}
