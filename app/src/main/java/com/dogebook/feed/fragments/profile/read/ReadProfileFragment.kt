package com.dogebook.feed.fragments.profile.read

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.dogebook.Util
import com.dogebook.databinding.FragmentEditProfileBinding
import com.dogebook.databinding.FragmentReadProfileBinding
import com.dogebook.feed.fragments.profile.User
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Response
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.properties.Delegates

class ReadProfileFragment : Fragment() {

    private var _binding: FragmentReadProfileBinding? = null
    private val binding get() = _binding!!
    private var userId by Delegates.notNull<Long>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReadProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        populateData()
    }

    private fun populateData() {
        arguments?.getLong("userId")?.let { userId = it }
        lifecycleScope.launch {
            var profilePicResponse: Response
            var user: User
            withContext(Dispatchers.IO) {
                user = Gson().fromJson(
                    Util.executeRequest(
                        context, "/users/${arguments?.getLong("userId")}", Util.METHOD.GET, null
                    ).body.string(), User::class.java
                )
                profilePicResponse = Util.executeRequest(
                    requireContext(),"/users/${arguments?.getLong("userId")}/profile-picture?isThumbnail=false", Util.METHOD.GET, null
                )
            }
            binding.rpName.text = user.toString()
            binding.rpBirthDate.text =
                LocalDateTime.parse(user.birthDate, DateTimeFormatter.ISO_ZONED_DATE_TIME)
                    .format(DateTimeFormatter.ISO_LOCAL_DATE)
            if (profilePicResponse.isSuccessful) {
                binding.rpProfilePicture.setImageBitmap(BitmapFactory.decodeStream(profilePicResponse.body.byteStream()))
            }
        }
    }
}