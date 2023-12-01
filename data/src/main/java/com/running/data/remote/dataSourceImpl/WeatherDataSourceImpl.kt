package com.running.data.remote.dataSourceImpl

import com.running.data.exception.errorHandler
import com.running.data.remote.dataSource.WeatherDataSource
import com.running.data.remote.model.WeatherData
import com.running.data.state.ResponseState
import com.running.domain.state.ResourceState
import javax.inject.Inject

class WeatherDataSourceImpl @Inject constructor(
    private val dataSource: WeatherDataSource
) {
    suspend fun getWeatherData(data: HashMap<String, String>): ResponseState<WeatherData> {
        return dataSource.getWeatherData(data).errorHandler()
    }
}
