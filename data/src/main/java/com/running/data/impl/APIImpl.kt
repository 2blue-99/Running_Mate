package com.running.data.impl

import android.util.Log
import com.jaehyeon.data.exception.WeatherApiException
import com.running.data.data_source.API
import com.running.data.dto.Weather
import com.running.data.dto.WeatherType
import com.running.domain.model.DomainWeather
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class APIImpl: API {

    private val BASE_URL = com.running.data.BuildConfig.BASE_URL

    override suspend fun getWeatherData(data: HashMap<String, String>): Weather {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(API::class.java).getWeatherData(data)
    }

    fun dataFilter(resource : Weather): DomainWeather {

        Log.e(javaClass.simpleName, "resource : ${resource.response.header.resultCode}")
        return when(resource.response.header.resultCode.toString()){

            "0" -> WeatherType().getCategory(resource.response)
            "1" -> throw WeatherApiException.ApplicationErrorException(resource.response.header.resultMsg)
            "2" -> throw WeatherApiException.DataBaseErrorException(resource.response.header.resultMsg)
            "3" -> throw WeatherApiException.NoDataErrorException(resource.response.header.resultMsg)
            "4" -> throw WeatherApiException.HttpErrorException(resource.response.header.resultMsg)
            "5" -> throw WeatherApiException.ServiceTimeOutException(resource.response.header.resultMsg)
            "10" -> throw WeatherApiException.InvalidRequestParameterErrorException(resource.response.header.resultMsg)
            "11" -> throw WeatherApiException.NoMandatoryRequestParametersErrorException(resource.response.header.resultMsg)
            "12" -> throw WeatherApiException.NoOpenApiServiceErrorException(resource.response.header.resultMsg)
            "20" -> throw WeatherApiException.ServiceAccessDeniedErrorException(resource.response.header.resultMsg)
            "21" -> throw WeatherApiException.TemporarilyDisableTheServiceKeyErrorException(resource.response.header.resultMsg)
            "22" -> throw WeatherApiException.LimitedNumberOfServiceRequestsExceedsErrorException(resource.response.header.resultMsg)
            "30" -> throw WeatherApiException.ServiceKeyIsNotRegisteredErrorException(resource.response.header.resultMsg)
            "31" -> throw WeatherApiException.DeadlineHasExpiredErrorException(resource.response.header.resultMsg)
            "32" -> throw WeatherApiException.UnregisteredIpErrorException(resource.response.header.resultMsg)
            "33" -> throw WeatherApiException.UnSignedCallErrorException(resource.response.header.resultMsg)
            "99" -> throw WeatherApiException.UnknownErrorException(resource.response.header.resultMsg)
            else -> throw Throwable()
        }
    }
}
