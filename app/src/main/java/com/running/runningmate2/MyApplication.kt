package com.running.runningmate2

import android.app.Application
import android.content.Context
import androidx.room.Room

class MyApplication : Application() {
    init {

        instance = this
    }


    companion object {
        private var instance: MyApplication? = null
        fun applicationContext() : Context {
            return instance!!.applicationContext
        }

        fun getApplication() : MyApplication{
            return instance!!
        }
    }
}