package com.dogebook.feed.fragments.feed

import java.time.LocalDateTime

data class Post(
    val id: Long,
    val content: String,
    val author: Author?,
    val likes: Long?,
    val created: LocalDateTime,

    var likedByUser: Boolean = false
)
