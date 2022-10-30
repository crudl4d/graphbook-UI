package com.dogebook.feed.fragments.feed

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dogebook.Dogebook
import com.dogebook.R
import com.dogebook.databinding.FragmentFeedBinding
import com.dogebook.feed.MainActivity
import com.dogebook.feed.fragments.RecyclerViewAdapter
import com.google.gson.*
import java.lang.reflect.Type
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.util.concurrent.Executors


class FeedFragment : Fragment() {

    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!
    private lateinit var loadingPB: ProgressBar

    private var recyclerView: RecyclerView? = null
    private var recyclerViewAdapter: RecyclerViewAdapter? = null
    private var rowsArrayList: ArrayList<Post?> = ArrayList()

    private var isLoading = false
    private var page = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.writePost.setOnClickListener {
            (requireActivity() as MainActivity).hideTabs()
            findNavController().navigate(R.id.action_feedFragment_to_postFragment)
        }

        loadingPB = requireView().findViewById(R.id.progressBar)
        loadingPB.visibility = View.VISIBLE
        populateData()
    }

    private fun populateData() {
        fetchPosts()
    }

    private fun fetchPosts() {
        val executor = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())
        executor.execute {
            val response = Dogebook.executeRequest(requireContext(), "/posts?page=0", Dogebook.METHOD.GET, null)
            val posts = Dogebook.gson.fromJson(response.body?.string(), Array<Post>::class.java)
            page++
            handler.post {
                for (post in posts) {
                    rowsArrayList.add(post)
                }
                loadingPB.visibility = View.GONE
                initAdapter()
                initScrollListener()
            }
        }
    }

    private fun initAdapter() {
        recyclerView = view?.findViewById(R.id.recyclerView)
        recyclerViewAdapter = RecyclerViewAdapter(rowsArrayList, requireContext(), findNavController())
        recyclerView?.adapter = recyclerViewAdapter
    }

    private fun initScrollListener() {
        recyclerView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?
                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == rowsArrayList.size - 1) {
                        //bottom of list!
                        loadMore()
                        isLoading = true
                    }
                }
            }
        })
    }

    private fun loadMore() {
        recyclerView?.post {
            val executor = Executors.newSingleThreadExecutor()
            val handler = Handler()
            executor.execute {
                val response = Dogebook.executeRequest(
                    requireContext(),
                    "/posts?page=$page",
                    Dogebook.METHOD.GET,
                    null
                )
                val posts = Gson().fromJson(response.body?.string(), Array<Post>::class.java)
                handler.post {
                    page++
                    for (post in posts) {
                        rowsArrayList.add(post)
                    }
                    recyclerViewAdapter?.notifyDataSetChanged()
                }
            }
            isLoading = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}