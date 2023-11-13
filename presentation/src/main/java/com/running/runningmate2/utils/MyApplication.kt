package com.running.runningmate2.utils

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {
    init {
        instance = this
    }


    companion object {
        private var instance: MyApplication? = null

        fun getApplication() : MyApplication {
            return instance!!
        }
    }
}