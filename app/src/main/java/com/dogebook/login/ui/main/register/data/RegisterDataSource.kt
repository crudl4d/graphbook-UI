package com.dogebook.login.ui.main.register.data

import com.dogebook.Util
import com.dogebook.login.ui.main.register.data.model.RegisteredUser
import com.dogebook.login.ui.main.register.ui.register.User
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class RegisterDataSource {


    fun register(user: User): Result<RegisteredUser> {
        return try {
            val response = Util.executeRequest(null, "/users/register", Util.METHOD.POST,
                Gson().toJson(user).toRequestBody("application/json".toMediaTypeOrNull()))
            val user = RegisteredUser(response.header("id", "")!!, response.header("name", "")!!)
            Result.Success(user)
        } catch (e: Throwable) {
            Result.Error(IOException("Error registering", e))
        }
    }

    fun logout() {
        // TODO: revoke authentication
    }
}