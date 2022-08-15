package com.example.data.data_source
import com.example.data.Apikey.Companion.API_KEY
import com.example.data.dto.Weather
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
// 코루틴 위에서 동작하므로 suspend를 안붙인다고 빌드 오류는 안나지만
//데이터가 정상적으로 안들어와 문제 발생함.
}