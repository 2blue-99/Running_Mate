package com.example.runningmate2

import android.location.Location

/**
 * Created by Jaehyeon on 2022/08/16.
 */
interface LocationRepository {
    suspend fun getCurrentLocation(): Location?
}