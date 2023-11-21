package com.running.data.local.sharedPreference

import android.content.SharedPreferences
import javax.inject.Inject

/**
 * 2023-11-21
 * pureum
 */
class SharedPreferenceHelperImpl @Inject constructor(
    private val sharedPreferenceManager: SharedPreferences
): SharedPreferenceHelper {
    override fun saveWeight(weight: Int) = sharedPreferenceManager.edit().putInt("runningMate_weight", weight).apply()
    override fun getWeight(): Int = sharedPreferenceManager.getInt("runningMate_weight", 0)
}