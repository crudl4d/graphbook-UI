package com.dogebook.feed.fragments.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dogebook.Dogebook
import com.dogebook.databinding.FragmentProfileBinding
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        populateData()
    }

    private fun populateData() {
        runBlocking {
            launch(Dispatchers.Default) {
            val response = Dogebook.executeRequest(requireContext(),"/me", Dogebook.METHOD.GET, null)
            val user = Gson().fromJson(response.body?.string(), User::class.java)
            binding.name.text = user.toString()
            }
        }
    }
}