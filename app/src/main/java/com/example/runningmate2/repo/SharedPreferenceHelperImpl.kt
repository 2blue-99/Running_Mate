package com.example.runningmate2.repo

import android.app.Application
import android.content.Context
import androidx.core.content.edit
import com.example.runningmate2.MyApplication
import com.example.runningmate2.R


/**
 * Created by pureum on 2022/09/08.
 */
class SharedPreferenceHelperImpl : SharedPreferenceHelper {

    private val sharedPreferences = MyApplication.getApplication().getSharedPreferences(MyApplication.getApplication().getString(R.string.app_name), Context.MODE_PRIVATE)

    override var weight: Int
        get() = sharedPreferences.getInt(WEIGHT, 0)
        @Synchronized
        set(value) {
            sharedPreferences.edit(false) {
                putInt(WEIGHT, value)
            }
        }

    override fun clear() {
        sharedPreferences.edit {
            remove(WEIGHT).apply()
        }
    }


    companion object {
        const val WEIGHT = "sharedPreference_weight"
    }
}