package com.running.data.remote.model

import com.jaehyeon.data.exception.WeatherApiException
import com.running.data.mapper.WeatherTypeMapper
import com.running.domain.model.DomainWeather

data class WeatherData(
    val response: Response
){
    data class Response(
        val header: Header,
        val body: Body
    )

    data class Header(
        val resultCode: Int,
        val resultMsg: String
    )

    data class Body(
        val dataType: String,
        val items: Items
    )

    data class Items(
        val item: List<Item>
    )

    data class Item(
        val baseData: Int,
        val baseTime: Int,
        val category: String,
        val nx : Int,
        val ny : Int,
        val obsrValue : Float
    )
}

fun WeatherData.dataFilter(): DomainWeather {
    return when(this.response.header.resultCode.toString()){
        "0" -> WeatherTypeMapper().getCategory(this.response)
        "1" -> throw WeatherApiException.ApplicationErrorException(this.response.header.resultMsg)
        "2" -> throw WeatherApiException.DataBaseErrorException(this.response.header.resultMsg)
        "3" -> throw WeatherApiException.NoDataErrorException(this.response.header.resultMsg)
        "4" -> throw WeatherApiException.HttpErrorException(this.response.header.resultMsg)
        "5" -> throw WeatherApiException.ServiceTimeOutException(this.response.header.resultMsg)
        "10" -> throw WeatherApiException.InvalidRequestParameterErrorException(this.response.header.resultMsg)
        "11" -> throw WeatherApiException.NoMandatoryRequestParametersErrorException(this.response.header.resultMsg)
        "12" -> throw WeatherApiException.NoOpenApiServiceErrorException(this.response.header.resultMsg)
        "20" -> throw WeatherApiException.ServiceAccessDeniedErrorException(this.response.header.resultMsg)
        "21" -> throw WeatherApiException.TemporarilyDisableTheServiceKeyErrorException(this.response.header.resultMsg)
        "22" -> throw WeatherApiException.LimitedNumberOfServiceRequestsExceedsErrorException(this.response.header.resultMsg)
        "30" -> throw WeatherApiException.ServiceKeyIsNotRegisteredErrorException(this.response.header.resultMsg)
        "31" -> throw WeatherApiException.DeadlineHasExpiredErrorException(this.response.header.resultMsg)
        "32" -> throw WeatherApiException.UnregisteredIpErrorException(this.response.header.resultMsg)
        "33" -> throw WeatherApiException.UnSignedCallErrorException(this.response.header.resultMsg)
        "99" -> throw WeatherApiException.UnknownErrorException(this.response.header.resultMsg)
        else -> throw Throwable()
    }
}


