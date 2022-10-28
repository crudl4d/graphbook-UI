package com.dogebook.feed.ui.main.fragments.feed

data class Post(
    val id: Long,
    val content: String,
    val author: Author,
    val likes: Long,

    var likedByUser: Boolean = false
)
