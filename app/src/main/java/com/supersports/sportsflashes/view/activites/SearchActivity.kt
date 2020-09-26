package com.supersports.sportsflashes.view.activites

import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.supersports.sportsflashes.R
import com.supersports.sportsflashes.model.FeaturedShows
import com.supersports.sportsflashes.model.MessageEvent
import com.supersports.sportsflashes.model.SearchResult
import com.supersports.sportsflashes.repository.api.STATUS
import com.supersports.sportsflashes.view.adapters.SearchAdapter
import com.supersports.sportsflashes.viewmodel.SearchActivityViewModel
import kotlinx.android.synthetic.main.activity_search.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class SearchActivity : AppCompatActivity() {
    private lateinit var viewModel: SearchActivityViewModel
    private lateinit var searchAdapter: SearchAdapter
    private var searchResult = ArrayList<FeaturedShows>()
    private lateinit var searchRecycler: RecyclerView
    var delay: Long = 1000 // 1 seconds after user stops typing

    var last_text_edit: Long = 0
    var handler = Handler()
    private val inputFinishChecker = Runnable {
        if (System.currentTimeMillis() > last_text_edit + delay - 500) {
            Handler().postDelayed({ getSearchResult(searchString) }, 500)
        }
    }

    private var searchString = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
        viewModel = ViewModelProvider(this).get(SearchActivityViewModel::class.java)
        setContentView(R.layout.activity_search)
        searchRecycler = findViewById(R.id.searchRecycler)
        initSearchRecycler()
        searchViewEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(char: CharSequence?, p1: Int, p2: Int, p3: Int) {
                handler.removeCallbacks(inputFinishChecker)

            }

            override fun afterTextChanged(editable: Editable?) {
                if (editable?.length!! > 0) {
                    progressBar.visibility = View.VISIBLE
                    last_text_edit = System.currentTimeMillis()
                    handler.postDelayed(inputFinishChecker, delay)
                    searchString = editable.toString()
                }
            }

        })
    }

    private fun initSearchRecycler() {
        searchRecycler.setHasFixedSize(true)
        searchRecycler.layoutManager = LinearLayoutManager(this).also {
            it.reverseLayout = false
            it.orientation = RecyclerView.VERTICAL

        }
    }

    private fun getSearchResult(search: String) {
        viewModel.getSearchResult(search).observe(this, Observer {
            if (it.status == STATUS.SUCCESS) {
                progressBar.visibility = View.GONE
                if (it.data != null) {
                    noData.visibility= View.GONE
                    searchResult.clear()
                    searchResult.addAll(it.data as List<FeaturedShows>)
//                    searchResult = it.data as List<SearchResult>
                    if (searchResult.isNotEmpty()) {
                        if (!this::searchAdapter.isInitialized) {
                            searchAdapter = SearchAdapter(this@SearchActivity, searchResult)
                            searchRecycler.adapter = searchAdapter
                        } else
                            searchAdapter.notifyDataSetChanged()
                    } else {
                        if (this::searchAdapter.isInitialized) {
                            searchAdapter.notifyDataSetChanged()
                        }
                    }
                    if (it.data.isEmpty()){
                        noData.visibility= View.VISIBLE
                    }
                }else{
                    noData.visibility= View.VISIBLE
                }
            } else if (it.status == STATUS.ERROR) {
                progressBar.visibility = View.GONE
                noData.visibility= View.VISIBLE
            }
        })
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(messageEvent: MessageEvent) {}

}