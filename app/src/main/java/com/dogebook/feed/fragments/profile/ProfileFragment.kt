package com.dogebook.feed.fragments.profile

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
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
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var loadingPB: ProgressBar
    private lateinit var getImageFromGallery: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        getImageFromGallery =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == Activity.RESULT_OK) {
                    setProfilePicture(result)
                }
            }
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.editProfile.visibility = View.GONE
        binding.profilePicture.visibility = View.GONE
        binding.button.visibility = View.GONE
        binding.name.visibility = View.GONE
        binding.birthDate.visibility = View.GONE
        binding.editProfile.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
        }
        binding.myPosts.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_myPostsFragment)
        }
        binding.button.setOnClickListener {
            getImageFromGallery.launch(Intent().apply {
                type = "image/*";
                action = Intent.ACTION_GET_CONTENT
            })
        }
        binding.friendRequests.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_friendRequestFragment)
        }
        binding.friendList.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_friendListFragment)
        }
        loadingPB = requireView().findViewById<ProgressBar?>(R.id.progressBar)
            .apply { visibility = View.VISIBLE }
        populateData()
    }

    private fun populateData() {
        lifecycleScope.launch {
            val profilePicture: Bitmap?
            val user = withContext(Dispatchers.IO) {
                profilePicture = BitmapFactory.decodeStream(
                    Util.executeRequest(
                        requireContext(),
                        "/me/profile-picture",
                        Util.METHOD.GET,
                        null
                    )
                        .body.byteStream()
                )
                val response = Util.executeRequest(context, "/me", Util.METHOD.GET, null)
                Gson().fromJson(response.body.string(), User::class.java)
            }
            if (profilePicture.toString() != "null") {
                binding.profilePicture.setImageBitmap(profilePicture)
            }
            binding.name.text = user.toString()
            binding.birthDate.text =
                LocalDateTime.parse(user.birthDate, DateTimeFormatter.ISO_ZONED_DATE_TIME)
                    .format(DateTimeFormatter.ISO_LOCAL_DATE)
            loadingPB.visibility = View.GONE
            binding.editProfile.visibility = View.VISIBLE
            binding.profilePicture.visibility = View.VISIBLE
            binding.button.visibility = View.VISIBLE
            binding.name.visibility = View.VISIBLE
            binding.birthDate.visibility = View.VISIBLE
        }
    }

    private fun setProfilePicture(result: ActivityResult) {
        binding.profilePicture.setImageURI(result.data?.data)
        lifecycleScope.launch {
            val file = File(context?.cacheDir, "profile-picture")
            withContext(Dispatchers.IO) {
                file.createNewFile()
                val bitMapData = ByteArrayOutputStream().let {
                    MediaStore.Images.Media.getBitmap(context?.contentResolver, result.data?.data)
                        .compress(Bitmap.CompressFormat.PNG, 0, it)
                    it.toByteArray()
                }
                FileOutputStream(file).apply {
                    write(bitMapData)
                    flush()
                    close()
                }
                val pic = MultipartBody.Builder().addFormDataPart(
                    "image", "image",
                    RequestBody.Companion.create("".toMediaTypeOrNull(), file)
                ).build()
                Util.executeRequest(context, "/me/profile-picture", Util.METHOD.POST, pic)
            }
        }
    }
}