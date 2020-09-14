package com.supersports.sportsflashes.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.supersports.sportsflashes.R
import com.supersports.sportsflashes.model.SportCategories
import com.supersports.sportsflashes.view.activites.MainActivity

class CategoryAdapter(private val listOfCategory: List<SportCategories>) :
    RecyclerView.Adapter<CategoryAdapter.CategoryItemHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryItemHolder {
        val rootView =
            LayoutInflater.from(parent.context).inflate(R.layout.category_item_view, parent, false)
        return CategoryItemHolder(rootView)
    }

    override fun getItemCount(): Int {
        return listOfCategory.size
    }

    override fun onBindViewHolder(categoryItemHolder: CategoryItemHolder, position: Int) {
        categoryItemHolder.categoryName.text = listOfCategory[position].category
        categoryItemHolder.categoryName.setOnClickListener {
            (it.context as MainActivity).categoryClicked(listOfCategory[position]._id)
        }
    }

    class CategoryItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var categoryName: TextView = itemView.findViewById(R.id.categoryName)
    }

    interface CategoryClickedListener{
        fun categoryClicked(categoryId:String)
    }

}