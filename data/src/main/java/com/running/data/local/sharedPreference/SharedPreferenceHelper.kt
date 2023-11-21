package com.running.data.local.sharedPreference

/**
 * 2023-11-21
 * pureum
 */
interface SharedPreferenceHelper {
    fun saveWeight(weight: Int)
    fun getWeight(): Int
}