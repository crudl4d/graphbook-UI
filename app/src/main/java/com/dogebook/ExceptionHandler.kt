package com.dogebook

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.dogebook.login.InitialActivity


class ExceptionHandler(private var ctx: Context) : Thread.UncaughtExceptionHandler {

    override fun uncaughtException(t: Thread, e: Throwable) {
        t.interrupt()
        Toast.makeText(ctx, "Network error", Toast.LENGTH_LONG).show()
        Log.e(t.javaClass.name, e.message ?:"", e)
        ctx.startActivity(Intent(ctx, InitialActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }
}