package com.running.domain.usecase

import com.running.domain.model.DomainWeather
import com.running.domain.repo.GetWeatherRepository

/**
 * 2023-11-13
 * pureum
 */
class GetWeatherUseCase(
    private val getWeatherRepository: GetWeatherRepository
) {
    suspend operator fun invoke(data: HashMap<String, String>): DomainWeather =
        getWeatherRepository.getWeatherRepoImpl(data)
}