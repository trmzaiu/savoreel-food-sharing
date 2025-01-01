package com.example.savoreel

import android.app.Application
import android.content.Context
import com.example.savoreel.model.PostModel

class MyApp : Application() {
    companion object {
        lateinit var appContext: Context
    }

    override fun onCreate() {
        super.onCreate()
        PostModel.initCloudinary(this)
        appContext = applicationContext
    }
}