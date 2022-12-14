package com.dogebook.feed.fragments.profile.posts

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.dogebook.R
import com.dogebook.Util
import com.dogebook.databinding.FragmentMyPostsBinding
import com.dogebook.feed.fragments.feed.Post
import com.dogebook.feed.fragments.feed.comments.Comment
import com.dogebook.feed.fragments.feed.comments.CommentRecyclerViewAdapter
import java.util.concurrent.Executors

class MyPostsFragment : Fragment() {

    private var _binding: FragmentMyPostsBinding? = null
    private val binding get() = _binding!!
    private lateinit var loadingPB: ProgressBar

    private var recyclerView: RecyclerView? = null
    private var recyclerViewAdapter: PostsRecyclerViewAdapter? = null
    private var rowsArrayList: ArrayList<Post?> = ArrayList()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyPostsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
            val response = Util.executeRequest(requireContext(), "/me/posts", Util.METHOD.GET, null)
            val posts = Util.gson.fromJson(response.body.string(), Array<Post>::class.java)
            handler.post {
                posts.forEach { rowsArrayList.add(it) }
                initAdapter()
                loadingPB.visibility = View.GONE
            }
        }
    }

    private fun initAdapter() {
        recyclerViewAdapter = context?.let { PostsRecyclerViewAdapter(it, rowsArrayList, findNavController()) }
        recyclerView = binding.postsList.apply { adapter = recyclerViewAdapter }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}