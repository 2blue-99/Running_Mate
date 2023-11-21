package com.running.domain.usecase

import com.running.domain.SavedData.DomainWeather
import com.running.domain.repo.WeatherDataRepo

/**
 * 2023-11-13
 * pureum
 */
class GetWeatherUseCase(
    private val weatherDataRepo: WeatherDataRepo
) {
    suspend operator fun invoke(data: HashMap<String, String>): DomainWeather =
        weatherDataRepo.getWeatherRepoImpl(data)
}