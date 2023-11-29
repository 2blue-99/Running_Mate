package com.running.domain.usecase

import com.running.domain.SavedData.DomainWeather
import com.running.domain.repo.WeatherDataRepo
import com.running.domain.state.ResourceState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * 2023-11-13
 * pureum
 */
class GetWeatherUseCase(
    private val weatherDataRepo: WeatherDataRepo
) {
    suspend operator fun invoke(data: HashMap<String, String>): Flow<ResourceState<DomainWeather>> = flow {
        emit(ResourceState.Loading())
        emit(weatherDataRepo.getWeatherRepoImpl(data))
    }
}