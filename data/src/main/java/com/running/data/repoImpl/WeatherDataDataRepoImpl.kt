package com.running.data.repoImpl

import com.running.data.remote.dataSource.WeatherDataSource
import com.running.data.remote.dataSourceImpl.WeatherDataSourceImpl
import com.running.data.remote.model.dataFilter
import com.running.data.state.ResponseState
import com.running.domain.SavedData.DomainWeather
import com.running.domain.repo.WeatherDataRepo
import com.running.domain.state.ResourceState
import javax.inject.Inject

class WeatherDataDataRepoImpl @Inject constructor(
    private val weatherDataSource: WeatherDataSourceImpl
) : WeatherDataRepo {
    override suspend fun getWeatherRepoImpl(data: HashMap<String, String>): ResourceState<DomainWeather>{
        return when(val result = weatherDataSource.getWeatherData(data)){
            is ResponseState.Success -> ResourceState.Success(data = result.data.dataFilter())
            is ResponseState.Error -> ResourceState.Error(message = result.message)
        }
    }
}