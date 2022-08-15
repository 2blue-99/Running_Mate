package com.example.data.dto

import android.util.Log
import com.example.domain.model.DomainWeather

class WeatherType {
    fun getCategory(response : Weather.Response): DomainWeather {
//        var response = response.response
        Log.e(javaClass.simpleName, "getCategory in, response : $response", )
        val temp = response.body.items.item.filter { it.category == "T1H" }.map { it.obsrValue }.first().toString()
        val rn1 = response.body.items.item.filter { it.category == "RN1" }.map { it.obsrValue }.first().toString()
        val eastWestWind = response.body.items.item.filter { it.category == "UUU" }.map { it.obsrValue }.first().toString()
        val southNorthWind = response.body.items.item.filter { it.category == "VVV" }.map { it.obsrValue }.first().toString()
        val humidity = response.body.items.item.filter { it.category == "REH" }.map { it.obsrValue }.first().toString()
        val rainType = response.body.items.item.filter { it.category == "PTY" }.map { it.obsrValue }.first().toString()
        val windDirection = response.body.items.item.filter { it.category == "VEC" }.map { it.obsrValue }.first().toString()
        val windSpeed = response.body.items.item.filter { it.category == "WSD" }.map { it.obsrValue }.first().toString()

        return DomainWeather(
            temp, rn1, eastWestWind, southNorthWind, humidity, rainType, windDirection, windSpeed
        )

    }

}