package com.running.data.remote.dataSourceImpl

import com.running.data.remote.dataSource.GetWeatherDataSource
import com.running.data.remote.model.WeatherData
import retrofit2.Retrofit
import javax.inject.Inject

class GetWeatherDataSourceImpl @Inject constructor(
    private val retrofit: Retrofit
): GetWeatherDataSource {
    override suspend fun getWeatherData(data: HashMap<String, String>): WeatherData =
        retrofit.create(GetWeatherDataSource::class.java).getWeatherData(data)

}
