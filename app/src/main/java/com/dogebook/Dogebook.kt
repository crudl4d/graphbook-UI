package com.dogebook

import android.app.Application

class Dogebook : Application() {
    companion object {
        lateinit var url: String
    }

    override fun onCreate() {
        super.onCreate()
        url = getString(R.string.host_url)
    }
}