package com.running.data.remote.dataSourceImpl

import android.util.Log
import com.running.data.remote.dataSource.WeatherDataSource
import com.running.data.remote.model.WeatherData
import retrofit2.Retrofit
import javax.inject.Inject

class WeatherDataSourceImpl @Inject constructor(
    private val retrofit: Retrofit
): WeatherDataSource {
    override suspend fun getWeatherData(data: HashMap<String, String>): WeatherData =
        retrofit.create(WeatherDataSource::class.java).getWeatherData(data)

}
