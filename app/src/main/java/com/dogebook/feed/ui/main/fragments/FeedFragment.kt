package com.dogebook.feed.ui.main.fragments

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dogebook.Dogebook
import com.dogebook.R
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.util.concurrent.Executors

class FeedFragment : Fragment() {

    private val client: OkHttpClient = OkHttpClient()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_feed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        populateData()
    }

    var recyclerView: RecyclerView? = null
    var recyclerViewAdapter: RecyclerViewAdapter? = null
    var rowsArrayList: ArrayList<String?> = ArrayList()

    var isLoading = false

    private fun populateData() {
        fetchPosts()
    }

    private fun fetchPosts() {
        val executor = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())
        executor.execute {
            var response: Response? = null
            val url = ("${Dogebook.url}/posts/").toHttpUrl().newBuilder()
                .build().toString()
            val request: Request = Request.Builder()
                .url(url)
                .addHeader(
                    "Authorization",
                    requireContext().getSharedPreferences(
                        R.string.preferences.toString(),
                        Context.MODE_PRIVATE
                    )
                        .getString("TOKEN", "").toString()
                )
                .build()
            val call: Call = client.newCall(request)
            response = call.execute()
            val posts = Gson().fromJson(response.body?.string(), Array<Post>::class.java)
            handler.post {
                for (post in posts) {
                    rowsArrayList.add(post.content)
                }
                initAdapter()
                initScrollListener()
            }
        }
    }

    private fun initAdapter() {
        recyclerView = requireView().findViewById(R.id.recyclerView)
        recyclerViewAdapter = RecyclerViewAdapter(rowsArrayList)
        recyclerView!!.adapter = recyclerViewAdapter
    }

    private fun initScrollListener() {
        recyclerView!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?
                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == rowsArrayList.size - 1) {
                        //bottom of list!
                        loadMore()
                        isLoading = true
                    }
                }
            }
        })
    }

    private fun loadMore() {
        rowsArrayList.add(null)
        recyclerViewAdapter!!.notifyItemInserted(rowsArrayList.size - 1)
        val handler = Handler()
        handler.postDelayed(Runnable {
            rowsArrayList.removeAt(rowsArrayList.size - 1)
            val scrollPosition: Int = rowsArrayList.size
            recyclerViewAdapter!!.notifyItemRemoved(scrollPosition)
            var currentSize = scrollPosition
            val nextLimit = currentSize + 10
            while (currentSize - 1 < nextLimit) {
                rowsArrayList.add("Item $currentSize")
                currentSize++
            }
            recyclerViewAdapter!!.notifyDataSetChanged()
            isLoading = false
        }, 2000)
    }
}