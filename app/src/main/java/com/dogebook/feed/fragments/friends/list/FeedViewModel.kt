package com.dogebook.feed.fragments.friends.list

import android.app.Application
import android.graphics.BitmapFactory
import androidx.lifecycle.*
import com.dogebook.Util
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FeedViewModel(app: Application) : AndroidViewModel(app) {

    private val _rowsArrayList = MutableLiveData<ArrayList<Request?>>()
    val rowsArrayList: LiveData<ArrayList<Request?>> = _rowsArrayList

    private val context = getApplication<Application>().applicationContext
    var page = 0

    init {
        populateData()
    }

    private fun populateData() {
        fetchPosts()
    }

    private fun fetchPosts() {
        val ral = arrayListOf<Request?>()
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val response = Util.executeRequest(context, "/friends", Util.METHOD.GET, null)
                val requests = Util.gson.fromJson(response.body.string(), Array<Request>::class.java)
                for (request in requests) {
                    val authorPicture = Util
                        .executeRequest(context, "/users/${request.id}/profile-picture", Util.METHOD.GET, null).body?.byteStream()
                    request.authorPicture = BitmapFactory.decodeStream(authorPicture)
                    ral.add(request)
                }
            }
            page++
            _rowsArrayList.value = ral
        }
    }
}