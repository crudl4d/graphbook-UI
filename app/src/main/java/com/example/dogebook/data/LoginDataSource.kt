package com.example.dogebook.data

import com.example.dogebook.data.model.LoggedInUser
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrl
import java.io.IOException

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource {

    private val client: OkHttpClient = OkHttpClient()
    private val BASE_URL = "http://192.168.0.101:8080"

    fun login(username: String, password: String): Result<LoggedInUser> {
        try {
            val credential = Credentials.basic(username, password)
            val url = ("$BASE_URL/users/login/").toHttpUrl().newBuilder()
                .build().toString()

            val request: Request = Request.Builder()
                .url(url)
                .addHeader("Authorization", credential)
                .addHeader("Connection", "keep-alive")
                .addHeader("Accept-Encoding", "gzip, deflate, br")
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .addHeader("withCredentials", "true")
                .addHeader("User-Agent", "PostmanRuntime/7.29.2")
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