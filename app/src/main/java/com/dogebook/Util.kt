package com.dogebook

import android.app.Application
import android.content.Context
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Response.Builder
import java.lang.reflect.Type
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit


class Util : Application() {
    companion object {
        lateinit var url: String
        val gson = GsonBuilder().registerTypeAdapter(LocalDateTime::class.java,
            JsonDeserializer { json: JsonElement, _: Type?, _: JsonDeserializationContext? ->
                LocalDateTime.parse(
                    json.asJsonPrimitive.asString
                )
            } as JsonDeserializer<LocalDateTime>).create()

        fun getToken(context: Context?): String {
            return context?.getSharedPreferences(
                R.string.preferences.toString(),
                Context.MODE_PRIVATE
            )?.getString("TOKEN", "").toString()
        }

        fun executeRequest(
            ctx: Context?,
            url: String,
            method: METHOD,
            body: RequestBody?
        ): Response {
            var body = body
            if (body == null) body = FormBody.Builder().build()
            val fullUrl = ("${Util.url}$url").toHttpUrl().newBuilder()
                .build().toString()
            val requestBuilder = Request.Builder()
                .url(fullUrl)
                .addHeader("Authorization", getToken(ctx))
            when (method) {
                METHOD.GET -> requestBuilder.get()
                METHOD.POST -> requestBuilder.post(body)
                METHOD.PATCH -> requestBuilder.patch(body)
                METHOD.PUT -> requestBuilder.put(body)
                METHOD.DELETE -> requestBuilder.delete(body)
            }
            val client = OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()
            val call: Call = client
                .newCall(requestBuilder.build())
            call.execute().let {
//                if (!it.isSuccessful && ctx != null) {
//                    Toast.makeText(ctx, "Network error", Toast.LENGTH_LONG).show()
//                }
                return it
            }
        }
    }

    enum class METHOD {
        GET,
        POST,
        PATCH,
        PUT,
        DELETE;
    }

    override fun onCreate() {
        super.onCreate()
        url = getString(R.string.host_url)
    }
}