package com.dogebook.feed.ui.main.fragments

data class Post(
    val id: Long,
    val content: String,
    val author: Long,
    val likes: Long,

    var likedByUser: Boolean = false
)
