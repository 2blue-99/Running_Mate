package com.running.runningmate2.utils

import java.time.LocalDate
import java.time.LocalTime
import java.util.Calendar

/**
 * 2023-11-24
 * pureum
 */
class TimeHelper {
    fun getTime(): String = "${LocalDate.now()} ${LocalTime.now().hour}:${LocalTime.now().minute}"


    fun getDay(): String =
        when (Calendar.getInstance().get(Calendar.DAY_OF_WEEK).toString()) {
            "1" -> "일"
            "2" -> "월"
            "3" -> "화"
            "4" -> "수"
            "5" -> "목"
            "6" -> "금"
            else -> "토"
        }
}