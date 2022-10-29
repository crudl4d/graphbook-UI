package com.dogebook.feed.fragments.profile

data class User(
    val firstName: String,
    val surname: String,
)
{
    override fun toString(): String {
        return "$firstName $surname"
    }
}
