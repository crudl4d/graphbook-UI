package com.dogebook.login.ui.main.register.ui.register

/**
 * Data validation state of the login form.
 */
data class RegisterFormState(
    val usernameError: Int? = null,
    val passwordError: Int? = null,
    val birthDateError: Int? = null,
    val isDataValid: Boolean = false
)