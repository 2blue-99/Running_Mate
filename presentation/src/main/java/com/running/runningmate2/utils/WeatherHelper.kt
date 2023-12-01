package com.running.runningmate2.utils

import android.location.Location
import android.util.Log
import androidx.core.content.ContextCompat
import com.running.domain.SavedData.DomainWeather
import com.running.runningmate2.R
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Calendar
import java.util.Date

/**
 * 2023-11-24
 * pureum
 */
object WeatherHelper {
    fun makeIcon(type: Int) =
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

    fun makeRequest(myLocation: Location?): HashMap<String, String> {
        val now = LocalDateTime.now()
        val baseTime = when {
            now.hour > 11 -> {
                if (now.minute < 40) "${now.hour - 1}00"
                else "${now.hour}00"
            }

            now.hour == 10 -> {
                if (now.minute < 40) "0900"
                else "1000"
            }

            now.hour in 1..9 -> {
                if (now.minute < 40) "0${now.hour - 1}00"
                else "0${now.hour}00"
            }

            now.hour == 0 -> {
                if (now.minute < 40) "2300"
                else "0000"
            }

            else -> "0000"
        }

        val baseDate = if (now.hour != 0) {
            LocalDate.now().toString().replace("-", "")
        } else {
            val myDate = SimpleDateFormat("yyyy/MM/dd")
            val calendar = Calendar.getInstance()
            val today = Date()
            calendar.time = today
            calendar.add(Calendar.DATE, -1)
            myDate.format(calendar.time).replace("/", "")
        }
        val locate = myLocation?.let { TransLocationUtil.convertLocation(it) }

        return HashMap<String, String>().apply {
            put("serviceKey", com.running.runningmate2.BuildConfig.SERVICE_KEY)
            put("pageNo", "1")
            put("numOfRows", "10")
            put("dataType", "JSON")
            put("base_date", baseDate)
            put("base_time", baseTime)
            put("nx", locate?.nx?.toInt().toString() ?: "55")
            put("ny", locate?.ny?.toInt().toString() ?: "127")
        }
    }

    fun makeFakeData(): DomainWeather {
        return DomainWeather(
            temperatures = 0.0.toString(),
            rn1 = 0.0.toString(),
            eastWestWind = 0.0.toString(),
            southNorthWind = 0.0.toString(),
            humidity = 0.0.toString(),
            rainType = 0.0.toString(),
            windDirection = 0.0.toString(),
            windSpeed = 0.0.toString()
        )
    }

}