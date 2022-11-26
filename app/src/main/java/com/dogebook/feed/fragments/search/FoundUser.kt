package com.dogebook.feed.fragments.search

data class FoundUser(
    val id: Long,
    val firstName: String,
    val surname: String,
)
{
    override fun toString(): String {
        return "$firstName $surname"
    }
}
