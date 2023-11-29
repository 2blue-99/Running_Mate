package com.running.domain.repo

import com.running.domain.SavedData.DomainWeather
import com.running.domain.state.ResourceState


/**
 * Created by Jaehyeon on 2022/08/03.
 */
interface WeatherDataRepo {
    suspend fun getWeatherRepoImpl(data: HashMap<String, String>): ResourceState<DomainWeather>
}