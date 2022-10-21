package com.dogebook.login.ui.main.register.data

import com.dogebook.login.ui.main.register.data.model.LoggedInUser
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrl
import java.io.IOException

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class RegisterDataSource {

    private val client: OkHttpClient = OkHttpClient()
    private val BASE_URL = "http://192.168.0.102:8080"

    fun login(username: String, password: String): Result<LoggedInUser> {
        try {
            val credential = Credentials.basic(username, password)
            val url = ("$BASE_URL/users/login/").toHttpUrl().newBuilder()
                .build().toString()
            val request: Request = Request.Builder()
                .post(FormBody.Builder().build())
                .url(url)
                .addHeader("Authorization", credential)
                .build()
            val call: Call = client.newCall(request)
            val response: Response = call.execute()
            val user = LoggedInUser(response.header("id", "")!!, response.header("name", "")!!)
            return Result.Success(user)
        } catch (e: Throwable) {
            return Result.Error(IOException("Error logging in", e))
        }
    }

    fun logout() {
        // TODO: revoke authentication
    }
}