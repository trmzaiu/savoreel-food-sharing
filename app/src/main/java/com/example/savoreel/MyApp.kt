package com.example.savoreel

import android.app.Application
import com.example.savoreel.model.PostModel

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        PostModel.initCloudinary(this)
    }
}