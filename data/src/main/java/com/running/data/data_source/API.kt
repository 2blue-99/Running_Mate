package com.running.data.data_source
import com.running.data.dto.Weather
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface API {
    /**
     * ServiceKey
     * pageNo
     * numOfRows
     * dataType
     * base_date
     * base_time
     * nx
     * ny
     */
    @GET("1360000/VilageFcstInfoService_2.0/getUltraSrtNcst")
    suspend fun getWeatherData(@QueryMap data: HashMap<String, String>): Weather

}