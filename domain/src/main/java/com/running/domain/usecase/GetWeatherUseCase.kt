package com.running.domain.usecase

import com.running.domain.SavedData.DomainWeather
import com.running.domain.repo.GetWeatherRepo

/**
 * 2023-11-13
 * pureum
 */
class GetWeatherUseCase(
    private val getWeatherRepo: GetWeatherRepo
) {
    suspend operator fun invoke(data: HashMap<String, String>): DomainWeather =
        getWeatherRepo.getWeatherRepoImpl(data)
}