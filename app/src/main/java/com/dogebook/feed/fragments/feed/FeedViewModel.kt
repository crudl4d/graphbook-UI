package com.dogebook.feed.fragments.feed

import android.app.Application
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ProgressBar
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
        fetchPosts(null)
    }

    private fun fetchPosts(pb: ProgressBar?) {
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
                if (pb != null) {
                    Handler(Looper.getMainLooper()).post { pb.visibility = View.GONE }
                }
            }
            page++
            _rowsArrayList.value = ral
        }
    }

    fun refreshPosts(pb: ProgressBar) {
        pb.visibility = View.VISIBLE
        rowsArrayList.value?.clear()
        _rowsArrayList.value = rowsArrayList.value
        fetchPosts(pb)
    }
}