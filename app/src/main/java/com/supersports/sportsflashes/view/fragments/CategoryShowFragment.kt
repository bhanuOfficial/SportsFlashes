package com.supersports.sportsflashes.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.supersports.sportsflashes.R
import com.supersports.sportsflashes.common.utils.AppConstant
import com.supersports.sportsflashes.model.FeaturedShows
import com.supersports.sportsflashes.repository.api.STATUS
import com.supersports.sportsflashes.view.adapters.CategoryShowAdapter
import com.supersports.sportsflashes.viewmodel.CategoryShowViewModel
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
            activity?.let { activity ->
                viewModel.getCategoryShow(it).observe(activity, Observer {
                    if (it.status == STATUS.SUCCESS) {
                        val categoryListData = it.data as ArrayList<FeaturedShows>
                        if (noData != null)
                            if (categoryListData.isEmpty()) {
                                noData.visibility = View.VISIBLE
                            } else
                                noData.visibility = View.GONE
                        if (categoryShowRecycler != null)
                            categoryShowRecycler.adapter =
                                CategoryShowAdapter(it.data as ArrayList<FeaturedShows>)
                    } else if (it.status == STATUS.ERROR) {
                        if (noData != null)
                            noData.visibility = View.VISIBLE
                    }
                })
            }
        }
    }

}