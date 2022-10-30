package com.dogebook.feed.fragments.feed.comments

import com.dogebook.feed.fragments.feed.Author
import java.time.LocalDateTime

data class Comment(
    val id: Long,
    val content: String,
    val author: Author,
    val likes: Long,
    val created: LocalDateTime,

    var likedByUser: Boolean = false
)
