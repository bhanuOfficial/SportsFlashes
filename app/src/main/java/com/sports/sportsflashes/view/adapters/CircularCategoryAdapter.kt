package com.sports.sportsflashes.view.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sports.sportsflashes.R
import com.sports.sportsflashes.view.activites.MainActivity
import kotlin.random.Random

class CircularCategoryAdapter(
    val categoryList: ArrayList<String>,
    val onItemSizeCaptured: (Int) -> Unit
) :
    RecyclerView.Adapter<CircularCategoryAdapter.ItemHolder>(), MainActivity.ItemPosition {
    private var index: Int = -1

    companion object {
        var pos: Int = 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val rootView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.category_items, parent, false)
        return ItemHolder(rootView)
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.textView.text = "" + position % categoryList.size
        pos = position
        index = position % categoryList.size
        val rnd = Random
        val color: Int = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
        holder.image.setBackgroundColor(color)
    }

    inner class ItemHolder(view: View) : RecyclerView.ViewHolder(view) {
        init {
            itemView.post {
                onItemSizeCaptured(itemView.measuredWidth)
            }
        }

        val image = view.findViewById<ImageView>(R.id.imageCategory)
        val textView = view.findViewById<TextView>(R.id.text)
    }

    override fun getItemPosition(): Int {
        return index
    }

}