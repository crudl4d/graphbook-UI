package com.dogebook.feed.fragments.friends.list

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.dogebook.R
import com.dogebook.Util
import java.util.concurrent.Executors


class RecyclerViewAdapter(
    private val mItemList: MutableList<Request?>?,
    private val ctx: Context,
    private val navController: NavController
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_ITEM = 0
    private val VIEW_TYPE_LOADING = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_ITEM) {
            val view: View =
                LayoutInflater.from(parent.context).inflate(R.layout.friend_details_row, parent, false)
            ItemViewHolder(view)
        } else {
            val view: View =
                LayoutInflater.from(parent.context).inflate(R.layout.item_loading, parent, false)
            LoadingViewHolder(view)
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        if (viewHolder is ItemViewHolder) {
            populateItemRows(viewHolder, position)
        } else if (viewHolder is LoadingViewHolder) {
            showLoadingView(viewHolder, position)
        }
    }

    override fun getItemCount(): Int {
        return mItemList?.size ?: 0
    }

    override fun getItemViewType(position: Int): Int {
        return if (mItemList?.get(position) == null) VIEW_TYPE_LOADING else VIEW_TYPE_ITEM
    }

    private inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name: TextView
        var remove: Button
        var picture: ImageView

        init {
            name = itemView.findViewById(R.id.request_name)
            remove = itemView.findViewById(R.id.remove)
            picture = itemView.findViewById(R.id.request_picture)
        }
    }

    private inner class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var progressBar: ProgressBar

        init {
            progressBar = itemView.findViewById(R.id.progressBar)
        }
    }

    private fun showLoadingView(viewHolder: LoadingViewHolder, position: Int) {
        //ProgressBar would be displayed
    }

    private fun populateItemRows(viewHolder: ItemViewHolder, position: Int) {
        val item: Request? = mItemList?.get(position)
        with(viewHolder) {
            name.text = "${item?.firstName} ${item?.surname}"
            val executor = Executors.newSingleThreadExecutor()
            val handler = Handler(Looper.getMainLooper())
            remove.setOnClickListener {
                executor.execute {
                    Util.executeRequest(ctx, "/friends/${item?.id}", Util.METHOD.DELETE, null)
                    handler.post {
                        Toast.makeText(ctx, "Friend removed", Toast.LENGTH_LONG).show()
                        mItemList?.removeAt(position)
                        this@RecyclerViewAdapter.notifyItemRemoved(position)
                    }
                }
            }
            executor.execute {
                Util.executeRequest(ctx, "/friends", Util.METHOD.POST, null)
                handler.post {
                    //todo increment likes
                }
            }
            if (item?.authorPicture != null) picture.setImageBitmap(item.authorPicture)
            else picture.setImageBitmap(BitmapFactory.decodeResource(ctx.resources, R.drawable.img))
        }
    }
}