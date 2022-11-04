package com.dogebook.feed.fragments.feed

import android.graphics.Bitmap
import java.time.LocalDateTime

data class Post(
    val id: Long,
    val content: String,
    val author: Author?,
    val likes: Long?,
    val created: LocalDateTime,
    var authorPicture: Bitmap?,

    var likedByUser: Boolean = false
)
