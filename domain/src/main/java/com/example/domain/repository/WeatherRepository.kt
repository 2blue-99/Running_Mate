package com.example.domain.repository

import com.example.domain.model.DomainWeather

/**
 * Created by Jaehyeon on 2022/08/03.
 */
interface WeatherRepository {

    suspend fun RepoGetWeatherData(data: HashMap<String, String>): DomainWeather
}