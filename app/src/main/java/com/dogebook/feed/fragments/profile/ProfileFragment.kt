package com.dogebook.feed.fragments.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.lifecycle.lifecycleScope
import com.dogebook.Util
import com.dogebook.R
import com.dogebook.databinding.FragmentProfileBinding
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var loadingPB: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadingPB = requireView().findViewById<ProgressBar?>(R.id.progressBar).apply { visibility = View.VISIBLE }
        populateData()
    }

    private fun populateData() {
        lifecycleScope.launch {
            val user  = withContext(Dispatchers.IO) {
                val response = Util.executeRequest(requireContext(),"/me", Util.METHOD.GET, null)
                Gson().fromJson(response.body?.string(), User::class.java)
            }
            binding.name.text = user.toString()
            loadingPB.visibility = View.GONE
        }
    }
}