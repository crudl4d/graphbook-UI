package com.dogebook.feed.fragments.feed.comments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dogebook.R
import com.dogebook.Util
import com.dogebook.databinding.FragmentCommentsBinding
import com.dogebook.feed.fragments.feed.Author
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.Executors

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
        binding.writeComment.setOnClickListener {
            lifecycleScope.launch {
                val comment = Comment(binding.commentContent.text.toString())
                withContext(Dispatchers.Default) {
                    Util.executeRequest(
                        context,
                        "/posts/${postId}/comments",
                        Util.METHOD.POST,
                        Gson().toJson(comment).toRequestBody("application/json".toMediaTypeOrNull())
                    )
                }
                comment.author = Author(0L, "You", "")
                rowsArrayList.add(0, comment)
                recyclerViewAdapter?.notifyItemInserted(0)
            }
        }
        populateData()
    }

    private fun populateData() {
        fetchComments()
    }

    private fun fetchComments() {
        val executor = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())
        executor.execute {
            val response = Util.executeRequest(requireContext(), "/posts/$postId/comments", Util.METHOD.GET, null)
            val comments = Util.gson.fromJson(response.body.string(), Array<Comment>::class.java)
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
        recyclerViewAdapter = context?.let { CommentRecyclerViewAdapter(it, rowsArrayList) }
        recyclerView = binding.requests.apply { adapter = recyclerViewAdapter }
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
                    context,
                    "/posts/$postId/comments?page=$page",
                    Util.METHOD.GET,
                    null
                )
                val responseBody = response.body.string()
                val comments = Gson().fromJson(responseBody, Array<Comment>::class.java)
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