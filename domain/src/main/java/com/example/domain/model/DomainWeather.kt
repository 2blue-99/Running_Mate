package com.example.domain.model

data class DomainWeather(
    val temperatures: String,
    val rn1: String,
    val eastWestWind: String,
    val southNorthWind: String,
    val humidity: String,
    val rainType: String,
    val windDirection: String,
    val windSpeed: String
)
