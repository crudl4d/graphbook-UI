package com.dogebook.feed.fragments.post

data class Post(
    val content: String,
    val visibility: String
)
{
    enum class Visibility {
        PUBLIC,
        FRIENDS
    }
}