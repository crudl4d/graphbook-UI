package com.dogebook.feed.fragments.profile

data class User(
    val firstName: String,
    val surname: String,
    val email: String,
    val birthDate: String,
)
{
    override fun toString(): String {
        return "$firstName $surname"
    }
}
