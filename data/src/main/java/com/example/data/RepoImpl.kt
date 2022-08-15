package com.example.data

import com.example.data.impl.APIImpl
import com.example.domain.model.DomainWeather
import com.example.domain.repository.WeatherRepository

class RepoImpl : WeatherRepository {
    override suspend fun RepoGetWeatherData(data: HashMap<String, String>): DomainWeather {
        return APIImpl().dataFilter(APIImpl().getWeatherData(data))
    }
}