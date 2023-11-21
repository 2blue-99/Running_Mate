package com.running.data.repoImpl

import com.running.data.remote.dataSource.WeatherDataSource
import com.running.data.remote.model.dataFilter
import com.running.domain.SavedData.DomainWeather
import com.running.domain.repo.WeatherDataRepo
import javax.inject.Inject

class WeatherDataDataRepoImpl @Inject constructor(
    private val weatherDataSource: WeatherDataSource
) : WeatherDataRepo {
    override suspend fun getWeatherRepoImpl(data: HashMap<String, String>): DomainWeather =
        weatherDataSource.getWeatherData(data).dataFilter()

}