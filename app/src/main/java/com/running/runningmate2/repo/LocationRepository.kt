package com.running.runningmate2.repo

import android.location.Location

/**
 * Created by Jaehyeon on 2022/08/16.
 */
interface LocationRepository {
    suspend fun getCurrentLocation(): Location?
}