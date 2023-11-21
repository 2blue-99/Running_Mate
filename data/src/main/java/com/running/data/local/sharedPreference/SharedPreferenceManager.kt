package com.running.data.local.sharedPreference

import android.content.SharedPreferences
import javax.inject.Inject

/**
 * 2023-11-21
 * pureum
 */
class SharedPreferenceManager @Inject constructor(
    private val sharedPreferenceManager: SharedPreferences
){
    fun saveWeight(weight: Int) = sharedPreferenceManager.edit().putInt("runningMate_weight", weight).apply()
    fun getWeight() = sharedPreferenceManager.getInt("runningMate_weight", 0).apply {  }
}