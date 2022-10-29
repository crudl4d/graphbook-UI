package com.dogebook.feed.fragments.feed

data class Author(
    val firstName: String,
    val surname: String,
)
{
    override fun toString(): String {
        return "$firstName $surname"
    }
}