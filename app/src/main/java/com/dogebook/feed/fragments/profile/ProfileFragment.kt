package com.dogebook.feed.fragments.profile

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.dogebook.R
import com.dogebook.Util
import com.dogebook.databinding.FragmentProfileBinding
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
        binding.editProfile.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
        }
        loadingPB = requireView().findViewById<ProgressBar?>(R.id.progressBar).apply { visibility = View.VISIBLE }
        populateData()
    }

    private fun populateData() {
        lifecycleScope.launch {
            val profilePicture: Bitmap?
            val user  = withContext(Dispatchers.IO) {
                profilePicture =  BitmapFactory.decodeStream(
                    Util.executeRequest(requireContext(), "/me/profile-picture", Util.METHOD.GET, null)
                        .body.byteStream()
                )
                val response = Util.executeRequest(context ,"/me", Util.METHOD.GET, null)
                Gson().fromJson(response.body.string(), User::class.java)
            }
            binding.profilePicture.setImageBitmap(profilePicture)
            binding.name.text = user.toString()
            loadingPB.visibility = View.GONE
        }
    }
}