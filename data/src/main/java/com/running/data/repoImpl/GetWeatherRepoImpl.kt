package com.running.data.repoImpl

import com.running.data.remote.dataSource.GetWeatherDataSource
import com.running.data.remote.model.dataFilter
import com.running.domain.SavedData.DomainWeather
import com.running.domain.repo.GetWeatherRepo
import javax.inject.Inject

class GetWeatherRepoImpl @Inject constructor(
    private val getWeatherDataSource: GetWeatherDataSource
) : GetWeatherRepo {
    override suspend fun getWeatherRepoImpl(data: HashMap<String, String>): DomainWeather =
        getWeatherDataSource.getWeatherData(data).dataFilter()

}