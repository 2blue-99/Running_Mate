package com.running.runningmate2.utils

import androidx.core.content.ContextCompat
import com.running.runningmate2.R

/**
 * 2023-11-24
 * pureum
 */
object WeatherIconHelper {
    operator fun invoke(type: Int) =
        when (type) {
            //0 없음, 1 장대비, 2367 눈, 5 비
            0 -> ContextCompat.getDrawable(
                MyApplication.getApplication(),
                R.drawable.ic___weather_suncloude
            )

            1 -> ContextCompat.getDrawable(
                MyApplication.getApplication(),
                R.drawable.ic___weather__strongrain
            )

            2 -> ContextCompat.getDrawable(
                MyApplication.getApplication(),
                R.drawable.ic__weather_snow
            )

            3 -> ContextCompat.getDrawable(
                MyApplication.getApplication(),
                R.drawable.ic__weather_snow
            )

            5 -> ContextCompat.getDrawable(
                MyApplication.getApplication(),
                R.drawable.ic___weather_rain
            )

            6 -> ContextCompat.getDrawable(
                MyApplication.getApplication(),
                R.drawable.ic__weather_snow
            )

            7 -> ContextCompat.getDrawable(
                MyApplication.getApplication(),
                R.drawable.ic__weather_snow
            )

            else -> ContextCompat.getDrawable(
                MyApplication.getApplication(),
                R.drawable.ic___weather_suncloude
            )
        }

}