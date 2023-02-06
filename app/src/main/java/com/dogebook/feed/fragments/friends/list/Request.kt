package com.dogebook.feed.fragments.friends.list

import android.graphics.Bitmap

data class Request(
    val id: Long,
    val firstName: String,
    val surname: String?,
    var authorPicture: Bitmap?,
)
