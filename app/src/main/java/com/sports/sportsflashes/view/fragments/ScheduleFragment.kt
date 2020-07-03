package com.sports.sportsflashes.view.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sports.sportsflashes.R
import com.sports.sportsflashes.common.application.SFApplication
import com.sports.sportsflashes.model.FeaturedShows
import com.sports.sportsflashes.view.adapters.CircularShowAdapter
import kotlinx.android.synthetic.main.schedule_fragment.*
import java.lang.reflect.Type
import javax.inject.Inject


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ScheduleFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ScheduleFragment : Fragment() {
    private var featuredShows = listOf<FeaturedShows>()

    @Inject
    lateinit var gson: Gson

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SFApplication.getAppComponent().inject(this)
        arguments?.let {
            val featuredListType: Type = object : TypeToken<ArrayList<FeaturedShows?>?>() {}.type
            featuredShows =
                gson.fromJson<List<FeaturedShows>>(it.getString("CHECK"), featuredListType)
            Log.d("BHANU", "value ---> " + it.getString("CHECK"))
            Log.d("BHANU", "List value ---> $featuredShows")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.schedule_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initScheduleShowRecycler()
        initWeeViewRecycler()
        initSchedulerRecycler()

    }

    fun initScheduleShowRecycler() {
        schedule_shows_recycler.setHasFixedSize(true)
        schedule_shows_recycler.layoutManager = LinearLayoutManager(activity).apply {
            this.orientation = LinearLayoutManager.HORIZONTAL
            this.reverseLayout = false
        }
        schedule_shows_recycler.adapter =
            activity?.let {
                CircularShowAdapter(featuredShows, {
                    var smallItemWidth = it
                }, it, true)
            }
    }

    fun initWeeViewRecycler() {
        weekView_recycler.setHasFixedSize(true)
        weekView_recycler.layoutManager = LinearLayoutManager(activity).apply {
            this.orientation = LinearLayoutManager.HORIZONTAL
            this.reverseLayout = false
        }
    }

    fun initSchedulerRecycler() {
        scheduleRecycler.setHasFixedSize(true)
        scheduleRecycler.layoutManager = LinearLayoutManager(activity).apply {
            this.orientation = LinearLayoutManager.HORIZONTAL
            this.reverseLayout = false
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ScheduleFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ScheduleFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

        @JvmStatic
        fun newInstance(): ScheduleFragment {
            return ScheduleFragment()
        }
    }

}