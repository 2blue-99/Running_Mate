package com.running.data.local.location

import android.location.Location
import kotlinx.coroutines.flow.Flow

/**
 * 2023-11-21
 * pureum
 */
interface LocationDataHelper {
    suspend fun startLocationDataStream():Location?
}