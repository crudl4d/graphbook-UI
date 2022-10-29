package com.dogebook

import android.app.Application
import android.content.Context
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrl

class Dogebook : Application() {
    companion object {
        lateinit var url: String

        fun getToken(context: Context): String {
            return context.getSharedPreferences(R.string.preferences.toString(), Context.MODE_PRIVATE).getString("TOKEN", "").toString()
        }

        fun executeRequest(ctx: Context, url: String, method: METHOD, body: RequestBody?): Response {
            var body = body
            if (body == null) body = FormBody.Builder().build()
            val fullUrl = ("${Dogebook.url}$url").toHttpUrl().newBuilder()
                .build().toString()
            val requestBuilder = Request.Builder()
                .url(fullUrl)
                .addHeader("Authorization", getToken(ctx))
            when (method) {
                METHOD.GET -> requestBuilder.get()
                METHOD.POST -> requestBuilder.post(body)
                METHOD.PATCH -> requestBuilder.patch(body)
                METHOD.PUT -> requestBuilder.put(body)
            }
            val call: Call = OkHttpClient().newCall(requestBuilder.build())
            return call.execute()
        }
    }

    enum class METHOD {
        GET,
        POST,
        PATCH,
        PUT;
    }

    override fun onCreate() {
        super.onCreate()
        url = getString(R.string.host_url)
    }
}