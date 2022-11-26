package com.dogebook.feed.fragments.search

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.dogebook.R
import com.dogebook.Util
import com.dogebook.databinding.FragmentSearchBinding
import com.dogebook.feed.fragments.feed.comments.Comment
import com.dogebook.feed.fragments.feed.comments.CommentRecyclerViewAdapter
import com.dogebook.feed.fragments.profile.User
import java.util.concurrent.Executors

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var loadingPB: ProgressBar

    private var recyclerView: RecyclerView? = null
    private var recyclerViewAdapter: UsersRecyclerViewAdapter? = null
    private var rowsArrayList: ArrayList<FoundUser?> = ArrayList()
    private var postId = -1L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.searchButton.setOnClickListener {
//            loadingPB = view.findViewById(R.id.progressBar)
//            loadingPB.visibility = View.VISIBLE
            fillFoundUsers()
//            loadingPB.visibility = View.GONE
        }
        super.onViewCreated(view, savedInstanceState)
    }

    private fun fillFoundUsers() {
        val executor = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())
        executor.execute {
            val response = Util.executeRequest(requireContext(), "/users/search?name=${binding.searchText.text}", Util.METHOD.GET, null)
            val users = Util.gson.fromJson(response.body.string(), Array<FoundUser>::class.java)
            handler.post {
                users.forEach { rowsArrayList.add(it) }
                initAdapter()
//                loadingPB.visibility = View.GONE
            }
        }
    }

    private fun initAdapter() {
        recyclerViewAdapter = context?.let { UsersRecyclerViewAdapter(it, rowsArrayList, findNavController()) }
        recyclerView = binding.foundUsers.apply { adapter = recyclerViewAdapter }
    }
}