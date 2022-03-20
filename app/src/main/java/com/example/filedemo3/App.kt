package com.example.filedemo3

import android.app.Application
import android.content.Context
import com.example.filedemo3.crash.CrashHandle2

@Suppress("unused")
class App : Application() {
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
    }

    override fun onCreate() {
        super.onCreate()
        CrashHandle2.getInstance(applicationContext).init()
    }
}