package com.dogebook.feed.fragments.friends.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dogebook.R
import com.dogebook.databinding.FragmentFriendListBinding
import com.dogebook.databinding.FragmentFriendRequestsBinding


class FriendListFragment : Fragment() {

    private var _binding: FragmentFriendListBinding? = null
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
        _binding = FragmentFriendListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        feedViewModel = ViewModelProvider(this)[FeedViewModel::class.java]
        initAdapter()
    }

    private fun initAdapter() {
        loadingPB = requireView().findViewById(R.id.progressBar)
        loadingPB.visibility = View.VISIBLE
        recyclerView = view?.findViewById(R.id.requests)
        feedViewModel.rowsArrayList.observe(viewLifecycleOwner, Observer { ral ->
            recyclerViewAdapter = context?.let { RecyclerViewAdapter(ral, it, findNavController()) }
            recyclerView?.adapter = recyclerViewAdapter
            loadingPB.visibility = View.GONE
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}