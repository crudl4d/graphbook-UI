package com.dogebook.feed.fragments.feed

import android.app.Application
import android.graphics.BitmapFactory
import androidx.lifecycle.*
import com.dogebook.Util
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FeedViewModel(app: Application) : AndroidViewModel(app) {

    private val _rowsArrayList = MutableLiveData<ArrayList<Post?>>()
    val rowsArrayList: LiveData<ArrayList<Post?>> = _rowsArrayList

    private val context = getApplication<Application>().applicationContext
    var page = 0

    init {
        populateData()
    }

    private fun populateData() {
        fetchPosts()
    }

    private fun fetchPosts() {
        val ral = arrayListOf<Post?>()
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val response = Util.executeRequest(context, "/posts?page=0", Util.METHOD.GET, null)
                val posts = Util.gson.fromJson(response.body.string(), Array<Post>::class.java)
                for (post in posts) {
                    val authorPicture = Util
                        .executeRequest(context, "/users/${post.author?.id}/profile-picture", Util.METHOD.GET, null).body?.byteStream()
                    post.authorPicture = BitmapFactory.decodeStream(authorPicture)
                    ral.add(post)
                }
            }
            page++
            _rowsArrayList.value = ral
        }
    }
}