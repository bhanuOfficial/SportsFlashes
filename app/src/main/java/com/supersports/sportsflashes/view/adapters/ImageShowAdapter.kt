package com.supersports.sportsflashes.view.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.supersports.sportsflashes.R
import com.supersports.sportsflashes.common.application.SFApplication
import com.supersports.sportsflashes.model.FeaturedShows
import com.supersports.sportsflashes.model.ThumbnailData
import com.supersports.sportsflashes.view.customviewimpl.FadingImageView
import com.supersports.sportsflashes.view.fragments.HomeFragment
import java.lang.reflect.Type
import javax.inject.Inject


class ImageShowAdapter(
    private val featuredShowsList: List<FeaturedShows>,
    val onItemSizeCaptured: (Int) -> Unit,
    val context: Context
) : RecyclerView.Adapter<ImageShowAdapter.ItemHolder>(), HomeFragment.ItemPosition {
    companion object {
        var pos: Int = 0
    }

    init {
        SFApplication.getAppComponent().inject(this)
    }

    private var index: Int = -1
    private var view: View? = null

    @Inject
    lateinit var gson: Gson

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val rootView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.dashboard_full_image_show, parent, false)
        return ItemHolder(rootView)
    }

    override fun getItemCount(): Int {
//        return Int.MAX_VALUE
        return featuredShowsList.size
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
//        holder.textView.text = "" + position
        view = holder.itemView
        index = position
        pos = position
        if (featuredShowsList[position].radio) {
            val thumbTokenType: Type = object : TypeToken<ThumbnailData?>() {}.type
            val thumbData: ThumbnailData =
                gson.fromJson(
                    gson.toJson(featuredShowsList[position].thumbnailData),
                    thumbTokenType
                )

//            val thumbData = (featuredShowsList[position].thumbnailData as ThumbnailData)
            if (thumbData.thumbnail.isNotEmpty()) {
                Glide.with(context).load(thumbData.thumbnail)
                    .placeholder(R.drawable.default_thumbnail)
                    .into(holder.showThumb)
            }else{
                Glide.with(context).load(featuredShowsList[position].icon)
                    .placeholder(R.drawable.default_thumbnail)
                    .into(holder.showThumb)
            }
        } else {
            if (featuredShowsList[position].thumbnail != null && featuredShowsList[position].thumbnail.isNotEmpty()) {
                Glide.with(context).load(featuredShowsList[position].thumbnail)
                    .placeholder(R.drawable.default_thumbnail)
                    .into(holder.showThumb)
            }
        }


        holder.showThumb.setFadingEdgeLength(50)
        holder.showThumb.setFadeDirection(FadingImageView.FadeSide.BOTTOM_SIDE)
        holder.showThumb.isHorizontalFadingEdgeEnabled = true
        holder.showThumb.isVerticalFadingEdgeEnabled = true
    }

    inner class ItemHolder(view: View) : RecyclerView.ViewHolder(view) {
        //        val image = view.findViewById<ImageView>(R.id.imageCategory)
        val showThumb: FadingImageView = view.findViewById(R.id.showImage)

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
