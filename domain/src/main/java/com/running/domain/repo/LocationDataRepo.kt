package com.running.domain.repo

import android.location.Location
import kotlinx.coroutines.flow.Flow

/**
 * 2023-11-22
 * pureum
 */
interface LocationDataRepo {
    fun getLocationDataStream(): Flow<Location>
    fun removeLocationDataStream()
}