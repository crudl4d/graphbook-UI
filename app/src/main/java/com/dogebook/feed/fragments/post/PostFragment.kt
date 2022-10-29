package com.dogebook.feed.fragments.post

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.dogebook.Dogebook
import com.dogebook.databinding.FragmentPostBinding
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody


class PostFragment : Fragment() {

    private var _binding: FragmentPostBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostBinding.inflate(inflater, container, false)
        binding.editTextTextMultiLine.requestFocus()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.post.setOnClickListener {
            val post = Post(binding.editTextTextMultiLine.text.toString())
            runBlocking (Dispatchers.Default){
                val response = Dogebook.executeRequest(
                    requireContext(),
                    "/posts",
                    Dogebook.METHOD.POST,
                    Gson().toJson(post).toRequestBody("application/json".toMediaTypeOrNull())
                )
                response.body
            }
        }
        super.onViewCreated(view, savedInstanceState)
    }
}