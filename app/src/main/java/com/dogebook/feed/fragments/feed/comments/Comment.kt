package com.dogebook.feed.fragments.feed.comments

import com.dogebook.feed.fragments.feed.Author
import java.util.*

data class Comment(
    val id: Long,
    val content: String,
    val author: Author,
    val likes: Long,
    val created: Date,

    var likedByUser: Boolean = false
)
