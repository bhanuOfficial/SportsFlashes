package com.sports.sportsflashes.view.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sports.sportsflashes.R
import com.sports.sportsflashes.view.activites.MainActivity
import kotlin.random.Random

/*class ImageCategoryAdapter(
    val categoryList: ArrayList<String>,
    val onItemSizeCaptured: (Int) -> Unit
) : PagerAdapter() {
    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return (view == `object`)
    }

    override fun getCount(): Int {
        return categoryList.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = LayoutInflater.from(container.context)
        val layout = inflater.inflate(
            R.layout.full_image_category,
            container,
            false
        )
        val textView = layout.findViewById<TextView>(R.id.gradient)
        textView.text = "" + position
        val rnd = Random
        val color: Int = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
        textView.setBackgroundColor(color)
        container.addView(layout)
        layout.post {
            onItemSizeCaptured(layout.measuredWidth)
        }
        return layout
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View?)
    }

}*/

class ImageCategoryAdapter(
    val categoryList: ArrayList<String>,
    val onItemSizeCaptured: (Int) -> Unit
) : RecyclerView.Adapter<ImageCategoryAdapter.ItemHolder>(), MainActivity.ItemPosition {

    companion object {
        var pos: Int = 0
    }
    private var index: Int = -1
    private var view: View? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val rootView =
            LayoutInflater.from(parent.context).inflate(R.layout.full_image_category, parent, false)
        return ItemHolder(rootView)
    }

    override fun getItemCount(): Int {
//        return Int.MAX_VALUE
        return categoryList.size
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.textView.text = "" + position
        view = holder.itemView
        index = position
        val rnd = Random
        val color: Int = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
        holder.textView.setBackgroundColor(color)
    }

    inner class ItemHolder(view: View) : RecyclerView.ViewHolder(view) {
        //        val image = view.findViewById<ImageView>(R.id.imageCategory)
        val textView = view.findViewById<TextView>(R.id.gradient)

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
