package com.dogebook.feed.fragments.feed.comments

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dogebook.Util
import com.dogebook.R
import java.util.concurrent.Executors

/**
 * [RecyclerView.Adapter] that can display a [PlaceholderItem].
 * TODO: Replace the implementation with code for your data type.
 */
class CommentRecyclerViewAdapter(
    private val ctx: Context,
    private val mItemList: List<Comment?>?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_ITEM = 0
    private val VIEW_TYPE_LOADING = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_ITEM) {
            val view: View =
                LayoutInflater.from(parent.context).inflate(R.layout.comment_row, parent, false)
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

    /**
     * The following method decides the type of ViewHolder to display in the RecyclerView
     *
     * @param position
     * @return
     */
    override fun getItemViewType(position: Int): Int {
        return if (mItemList?.get(position) == null) VIEW_TYPE_LOADING else VIEW_TYPE_ITEM
    }

    private inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var content: TextView
        var likeButton: Button
        var likesCount: TextView
        var author: TextView

        init {
            content = itemView.findViewById(R.id.comm_content)
            likeButton = itemView.findViewById(R.id.comm_like_button)
            likesCount = itemView.findViewById(R.id.comm_like_count)
            author = itemView.findViewById(R.id.comm_author)
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
        val item: Comment? = mItemList?.get(position)
        with(viewHolder) {
            content.text = item?.content
            likesCount.text = item?.likes.toString()
            author.text = item?.author.toString()
            likeButton.setOnClickListener {
                if (item?.likedByUser == false) {
                    likesCount.text = (likesCount.text.toString().toInt() + 1).toString()
                    it.setBackgroundColor(this@CommentRecyclerViewAdapter.ctx.getColor(R.color.purple_500))
                    item.likedByUser = true
                } else {
                    likesCount.text = (likesCount.text.toString().toInt() - 1).toString()
                    it.setBackgroundColor(this@CommentRecyclerViewAdapter.ctx.getColor(R.color.purple_light))
                    item?.likedByUser = false
                }
                val executor = Executors.newSingleThreadExecutor()
                val handler = Handler(Looper.getMainLooper())
                executor.execute {
                    Util.executeRequest(ctx, "/posts/${item?.id}/like", Util.METHOD.POST, null)
                    handler.post {
                        //todo increment likes
                    }
                }
            }
        }
    }

}