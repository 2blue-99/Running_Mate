package com.running.data

import com.running.data.impl.APIImpl
import com.running.domain.model.DomainWeather
import com.running.domain.repository.WeatherRepository
class RepoImpl : WeatherRepository {
    override suspend fun RepoGetWeatherData(data: HashMap<String, String>): DomainWeather {
        return APIImpl().dataFilter(APIImpl().getWeatherData(data))
    }
}