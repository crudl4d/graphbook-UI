package com.dogebook.feed.fragments.profile.edit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.dogebook.R
import com.dogebook.Util
import com.dogebook.databinding.FragmentEditProfileBinding
import com.dogebook.feed.fragments.profile.User
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        populateData()
    }

    private fun populateData() {
        lifecycleScope.launch {
            val user  = withContext(Dispatchers.IO) {
                val response = Util.executeRequest(context ,"/me", Util.METHOD.GET, null)
                Gson().fromJson(response.body.string(), User::class.java)
            }
            binding.firstName.setText(user.firstName)
            binding.lastName.setText(user.surname)
            binding.birthdate.setText(user.birthDate)
            binding.username.setText(user.email)
        }
    }
}