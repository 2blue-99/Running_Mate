package com.running.data.remote.dataSource
import com.running.data.remote.model.WeatherData
import com.running.domain.state.ResourceState
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface WeatherDataSource {
    @GET("1360000/VilageFcstInfoService_2.0/getUltraSrtNcst")
    suspend fun getWeatherData(@QueryMap data: HashMap<String, String>): Response<WeatherData>
}