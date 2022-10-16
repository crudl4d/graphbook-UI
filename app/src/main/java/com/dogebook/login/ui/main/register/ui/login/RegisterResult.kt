package com.dogebook.login.ui.main.register.ui.login

/**
 * Authentication result : success (user details) or error message.
 */
data class RegisterResult(
    val success: RegisteredUserView? = null,
    val error: Int? = null
)