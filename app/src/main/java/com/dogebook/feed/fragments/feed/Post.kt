package com.dogebook.feed.fragments.feed

import java.util.Date

data class Post(
    val id: Long,
    val content: String,
    val author: Author,
    val likes: Long,
    val created: Date,

    var likedByUser: Boolean = false
)
