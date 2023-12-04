package com.running.runningmate2.utils

import android.location.Location
import kotlin.math.*

object LocationHelper {

    data class TransLocation(
        val nx: Double,
        val ny: Double
    )

    fun convertLocation(location: Location): TransLocation {
        val grid = 5.0
        val xo = 43
        val yo = 136

        val degrad = Math.PI / 180.0

        val re = 6371.00877 / grid
        val slat1 = 30.0 * degrad
        val slat2 = 60.0 * degrad
        val olon = 126.0 * degrad
        val olat = 38.0 * degrad

        var sn = tan(Math.PI * 0.25 + slat2 * 0.5) / tan(Math.PI * 0.25 + slat1 * 0.5)
        sn = ln(cos(slat1) / cos(slat2)) / ln(sn)

        var sf = tan(Math.PI * 0.25 + slat1 * 0.5)
        sf = sf.pow(sn) * cos(slat1) / sn

        var ro = tan(PI * 0.25 + olat * 0.5)
        ro = re * sf / ro.pow(sn)

        var ra = tan(Math.PI * 0.25 + location.latitude * degrad * 0.5)
        ra = re * sf / ra.pow(sn)
        var theta: Double = location.longitude * degrad - olon
        if (theta > Math.PI) theta -= 2.0 * Math.PI
        if (theta < -Math.PI) theta += 2.0 * Math.PI
        theta *= sn

        return TransLocation(
            nx = floor(ra * sin(theta) + xo + 0.5),
            ny = floor(ro - ra * cos(theta) + yo + 0.5)
        )
    }
}