package com.running.data.repoImpl

import android.location.Location
import com.running.data.local.location.LocationDataHelper
import com.running.domain.repo.LocationDataRepo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * 2023-11-22
 * pureum
 */
class LocationDataRepoImpl @Inject constructor(
    private val locationDataHelper: LocationDataHelper
): LocationDataRepo {
    override fun getLocationDataStream(): Flow<Location> =
        locationDataHelper.getLocationDataStream()

    override fun removeLocationDataStream() {
        locationDataHelper.removeLocationDataStream()
    }

}