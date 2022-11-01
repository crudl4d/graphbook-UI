package com.dogebook.feed.fragments.feed.comments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dogebook.Util
import com.dogebook.R
import com.dogebook.databinding.FragmentCommentsBinding
import com.google.gson.Gson
import java.util.concurrent.Executors

/**
 * A fragment representing a list of Items.
 */
class CommentsFragment : Fragment() {

    private var _binding: FragmentCommentsBinding? = null
    private val binding get() = _binding!!
    private lateinit var loadingPB: ProgressBar

    private var recyclerView: RecyclerView? = null
    private var recyclerViewAdapter: CommentRecyclerViewAdapter? = null
    private var rowsArrayList: ArrayList<Comment?> = ArrayList()
    private var postId = -1L

    private var isLoading = false
    private var page = 0


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCommentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.getLong("postId")?.let { postId = it }
        loadingPB = requireView().findViewById(R.id.progressBar)
        loadingPB.visibility = View.VISIBLE
        populateData()
    }

    private fun populateData() {
        fetchComments()
    }

    private fun fetchComments() {
        val executor = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())
        executor.execute {
            val response = Util.executeRequest(requireContext(), "/posts/$postId/comments?page=0", Util.METHOD.GET, null)
            val comments = Util.gson.fromJson(response.body?.string(), Array<Comment>::class.java)
            page++
            handler.post {
                comments.forEach { rowsArrayList.add(it) }
                initAdapter()
                initScrollListener()
                loadingPB.visibility = View.GONE
            }
        }
    }

    private fun initAdapter() {
        recyclerViewAdapter = CommentRecyclerViewAdapter(requireContext(), rowsArrayList)
        recyclerView = binding.recyclerView.apply { adapter = recyclerViewAdapter }
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
                    "/posts/$postId/comments?page=$page",
                    Util.METHOD.GET,
                    null
                )
                val x = response.body?.string()
                val comments = Gson().fromJson(x, Array<Comment>::class.java)
                handler.post {
                    page++
                    comments.forEach { rowsArrayList.add(it) }
                    recyclerViewAdapter?.notifyDataSetChanged()
                }
            }
            isLoading = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }}