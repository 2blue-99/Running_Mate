package com.running.data.repoImpl

import com.running.data.remote.dataSourceImpl.GetWeatherDataSourceImpl
import com.running.domain.model.DomainWeather
import com.running.domain.repo.WeatherRepository
class GetWeatherRepoImpl : WeatherRepository {
    override suspend fun RepoGetWeatherData(data: HashMap<String, String>): DomainWeather {
        return GetWeatherDataSourceImpl().dataFilter(GetWeatherDataSourceImpl().getWeatherData(data))
    }
}