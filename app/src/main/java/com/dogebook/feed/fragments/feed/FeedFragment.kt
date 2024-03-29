package com.dogebook.feed.fragments.feed

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dogebook.R
import com.dogebook.Util
import com.dogebook.databinding.FragmentFeedBinding
import com.google.gson.*
import kotlinx.coroutines.flow.combine
import java.util.concurrent.Executors


class FeedFragment : Fragment() {

    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!
    private lateinit var feedViewModel: FeedViewModel
    private lateinit var loadingPB: ProgressBar

    private var recyclerView: RecyclerView? = null
    private var recyclerViewAdapter: RecyclerViewAdapter? = null
    private var isLoading = false

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
        binding.refresh.isEnabled = false
        binding.refresh.visibility = View.GONE
        feedViewModel = ViewModelProvider(this)[FeedViewModel::class.java]
        initAdapter()
        binding.writePost.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_postFragment)
        }
        binding.search.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_searchFragment)
        }
        val pb = binding.feedProgressBar
        binding.refresh.setOnClickListener {
            if (binding.toggleVisibility.isChecked) feedViewModel.fetchFriendsPosts(pb)
            else feedViewModel.refreshPosts(pb)
        }
        binding.toggleVisibility.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // fetch friends posts
                feedViewModel.fetchFriendsPosts(pb)
            } else {
                //fetch public posts
                feedViewModel.refreshPosts(pb)
            }
            recyclerViewAdapter?.notifyDataSetChanged()
        }
    }


    private fun initAdapter() {
        loadingPB = requireView().findViewById(R.id.progressBar)
        loadingPB.visibility = View.VISIBLE
        recyclerView = binding.requests
        feedViewModel.rowsArrayList.observe(viewLifecycleOwner, Observer { ral ->
            recyclerViewAdapter = context?.let { RecyclerViewAdapter(ral, it, findNavController()) }
            recyclerView?.adapter = recyclerViewAdapter
            initScrollListener(ral)
        })
    }

    private fun initScrollListener(ral: ArrayList<Post?>) {
        recyclerView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?
                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == ral.size - 1) {
                        //bottom of list!
                        loadMore()
                        isLoading = true
                    }
                }
            }
        })
        showButtons()
    }

    private fun loadMore() {
        recyclerView?.post {
            val executor = Executors.newSingleThreadExecutor()
            val handler = Handler()
            executor.execute {
                val response = Util.executeRequest(
                    context,
                    if (binding.toggleVisibility.isChecked) "/posts/friends?page=${feedViewModel.page}"
                    else "/posts?page=${feedViewModel.page}",
                    Util.METHOD.GET,
                    null
                )
                val posts = Gson().fromJson(response.body.string(), Array<Post>::class.java)
                handler.post {
                    feedViewModel.page++
                    for (post in posts) {
                        feedViewModel.rowsArrayList.value?.add(post)
                    }
                    recyclerViewAdapter?.notifyItemRangeInserted(feedViewModel.page * 10, posts.size)
                }
            }
            isLoading = false
        }
    }

    private fun showButtons() {
        loadingPB.visibility = View.GONE
        binding.refresh.visibility = View.VISIBLE
        binding.writePost.visibility = View.VISIBLE
        binding.toggleVisibility.visibility = View.VISIBLE
        binding.refresh.isEnabled = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}