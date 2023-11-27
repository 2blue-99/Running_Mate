package com.running.domain.usecase

import android.location.Location
import android.util.Log
import com.running.domain.repo.LocationDataRepo
import com.running.domain.state.ResourceState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * 2023-11-22
 * pureum
 */
class LocationUseCase(
    private val locationDataRepo: LocationDataRepo
) {
    fun getLocationDataStream(): Flow<ResourceState<Location>> = flow {
        while (true){
            when(val result = locationDataRepo.startLocationDataStream()){
                null -> emit(ResourceState.Error(message = "잘못된 위치 오류"))
                else -> emit(ResourceState.Success(result))
            }
            delay(1500L)
        }
    }
}