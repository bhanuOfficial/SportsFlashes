package com.sports.sportsflashes.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sports.sportsflashes.R
import com.sports.sportsflashes.common.utils.AppConstant
import com.sports.sportsflashes.model.FeaturedShows
import com.sports.sportsflashes.model.LiveSeasonModel
import com.sports.sportsflashes.repository.api.STATUS
import com.sports.sportsflashes.view.adapters.CategoryShowAdapter
import com.sports.sportsflashes.viewmodel.CategoryShowViewModel
import kotlinx.android.synthetic.main.category_show_fragment.*

/**
 *Created by Bhanu on 19-07-2020
 */
class CategoryShowFragment : Fragment() {

    private val categoryId by lazy {
        arguments?.getString(AppConstant.BundleExtras.CATEGORY_ID)
    }
    private lateinit var viewModel: CategoryShowViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(CategoryShowViewModel::class.java)
        val rootView = inflater.inflate(R.layout.category_show_fragment, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        getShowsByCategory()
    }

    private fun initViews() {
        categoryShowRecycler.setHasFixedSize(true)
        categoryShowRecycler.layoutManager = LinearLayoutManager(activity).also {
            it.reverseLayout = false
            it.orientation = RecyclerView.VERTICAL
        }
    }

    private fun getShowsByCategory() {
        categoryId?.let {
            activity?.let { it1 ->
                viewModel.getCategoryShow(it).observe(it1, Observer {
                    if (it.status == STATUS.SUCCESS) {
                        categoryShowRecycler.adapter =
                            CategoryShowAdapter(it.data as ArrayList<FeaturedShows>)
                    }
                })
            }
        }
    }

}