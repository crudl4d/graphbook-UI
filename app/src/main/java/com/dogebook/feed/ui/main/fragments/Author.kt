package com.dogebook.feed.ui.main.fragments

data class Author(
    val firstName: String,
    val surname: String,
)
{
    override fun toString(): String {
        return "$firstName $surname"
    }
}