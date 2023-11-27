package com.running.domain.repo

import android.location.Location
import kotlinx.coroutines.flow.Flow

/**
 * 2023-11-22
 * pureum
 */
interface LocationDataRepo {
//    var getLocationDataStream: Flow<Location>
    suspend fun startLocationDataStream(): Location?
}