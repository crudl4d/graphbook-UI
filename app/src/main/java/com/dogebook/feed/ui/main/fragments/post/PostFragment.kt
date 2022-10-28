package com.dogebook.feed.ui.main.fragments.post

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.navigation.fragment.findNavController
import com.dogebook.databinding.FragmentPostBinding
import com.dogebook.feed.FeedActivity

class PostFragment : Fragment() {

    private var _binding: FragmentPostBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.button.setOnClickListener {
            (requireActivity() as FeedActivity).showTabs()
        }
        super.onViewCreated(view, savedInstanceState)
    }
}