package com.example.domain.model

data class DomainWeather(
    val temperatures: String = "loading..",
    val rn1: String,
    val eastWestWind: String,
    val southNorthWind: String,
    val humidity: String = "loading..",
    val rainType: String,
    val windDirection: String,
    val windSpeed: String
)
