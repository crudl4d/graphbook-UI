package com.dogebook.feed.fragments.profile.posts

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import com.dogebook.R
import com.dogebook.Util
import com.dogebook.feed.fragments.feed.Post
import java.util.concurrent.Executors

class PostsRecyclerViewAdapter(
    private val ctx: Context,
    private var mItemList: MutableList<Post?>?,
    private val navController: NavController
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_ITEM = 0
    private val VIEW_TYPE_LOADING = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_ITEM) {
            val view: View =
                LayoutInflater.from(parent.context).inflate(R.layout.post_row, parent, false)
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
        var postContent: TextView
        var likeButton: Button
        var commentButton: Button
        var likesCount: TextView
        var author: TextView
        var picture: ImageView

        init {
            postContent = itemView.findViewById(R.id.post_content)
            likeButton = itemView.findViewById(R.id.like_button)
            commentButton = itemView.findViewById(R.id.comments)
            likesCount = itemView.findViewById(R.id.likeCount)
            author = itemView.findViewById(R.id.author)
            picture = itemView.findViewById(R.id.post_author_picture)
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
        val item: Post? = mItemList?.get(position)
        with(viewHolder) {

            author.visibility = View.GONE
            likeButton.visibility = View.GONE
            picture.visibility = View.GONE
            postContent.text = item?.content
            likesCount.text = item?.likes.toString()
            commentButton.setOnClickListener {
                val length = mItemList?.size
                mItemList = mutableListOf()
                notifyItemRangeRemoved(0, length ?: position)
                with(Bundle()) {
                    putLong("postId", item?.id ?: -1)
                    navController.navigate(R.id.action_myPostsFragment_to_commentsFragment, this)
                }
            }
            postContent.setOnClickListener {
                val builder: AlertDialog.Builder = AlertDialog.Builder(ctx)
                builder.setTitle("Delete")
                builder.setMessage("Do you want to delete the post?")
                builder.setPositiveButton("Delete") { _, _ ->
                    run {
                        val executor = Executors.newSingleThreadExecutor()
                        val handler = Handler(Looper.getMainLooper())
                        executor.execute {
                                Util.executeRequest(ctx, "/posts/${item?.id}", Util.METHOD.DELETE, null)
                            handler.post {
                                mItemList?.removeAt(position)
                                notifyItemRemoved(position)
                            }
                        }
                    }
                }
                val dialog: AlertDialog = builder.create()
                dialog.show()
            }
        }
    }
}