package com.dogebook.login.ui.main.login.data

import com.dogebook.Dogebook
import com.dogebook.login.ui.main.login.data.model.LoggedInUser
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrl
import java.io.IOException

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource {

    fun login(credentials: String): Result<LoggedInUser> {
        try {
            val url = ("${Dogebook.url}/users/login/").toHttpUrl().newBuilder()
                .build().toString()
            val request: Request = Request.Builder()
                .post(FormBody.Builder().build())
                .url(url)
                .addHeader("Authorization", credentials)
                .build()
            val call: Call = OkHttpClient().newCall(request)
            val response: Response = call.execute()
            if (!response.isSuccessful) return Result.Error(IOException("Error logging in"))
            val user = LoggedInUser(
                response.header("id", "")!!, response.header("name", "")!!
            )
            return Result.Success(user)
        } catch (e: Throwable) {
            return Result.Error(IOException("Error logging in", e))
        }
    }
}