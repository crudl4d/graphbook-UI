package com.dogebook.feed.fragments.feed

import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dogebook.Util
import com.dogebook.R
import com.dogebook.databinding.FragmentFeedBinding
import com.google.gson.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Response
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
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val response = Util.executeRequest(requireContext(), "/posts?page=0", Util.METHOD.GET, null)
                val posts = Util.gson.fromJson(response.body?.string(), Array<Post>::class.java)
                for (post in posts) {
                    val authorPicture = Util
                        .executeRequest(requireContext(), "/users/${post.author?.id}/profile-picture", Util.METHOD.GET, null).body?.byteStream()
                    post.authorPicture = BitmapFactory.decodeStream(authorPicture)
                    rowsArrayList.add(post)
                }
            }
            page++
            loadingPB.visibility = View.GONE
            initAdapter()
            initScrollListener()
        }
    }

    private fun initAdapter() {
        recyclerView = view?.findViewById(R.id.recyclerView)
        recyclerViewAdapter = context?.let { RecyclerViewAdapter(rowsArrayList, it, findNavController()) }
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
                val response = Util.executeRequest(
                    requireContext(),
                    "/posts?page=$page",
                    Util.METHOD.GET,
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