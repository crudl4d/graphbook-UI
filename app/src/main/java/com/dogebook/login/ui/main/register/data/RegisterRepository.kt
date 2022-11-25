package com.dogebook.login.ui.main.register.data

import com.dogebook.login.ui.main.register.data.model.RegisteredUser
import com.dogebook.login.ui.main.register.ui.register.User

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

class RegisterRepository(val dataSource: RegisterDataSource) {

    // in-memory cache of the loggedInUser object
    var user: RegisteredUser? = null
        private set

    init {
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
        user = null
    }

    fun register(user: User): Result<RegisteredUser> {
        // handle login
        val result = dataSource.register(user)

        if (result is Result.Success) {
            setLoggedInUser(result.data)
        }

        return result
    }

    private fun setLoggedInUser(registeredUser: RegisteredUser) {
        this.user = registeredUser
    }
}