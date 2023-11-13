package com.running.data.repoImpl

import com.running.data.remote.dataSource.GetWeatherDataSource
import com.running.data.remote.model.dataFilter
import com.running.domain.model.DomainWeather
import com.running.domain.repo.GetWeatherRepository
import javax.inject.Inject

class GetGetWeatherRepoImpl @Inject constructor(
    private val getWeatherDataSource: GetWeatherDataSource
) : GetWeatherRepository {
    override suspend fun getWeatherRepoImpl(data: HashMap<String, String>): DomainWeather =
        getWeatherDataSource.getWeatherData(data).dataFilter()

}