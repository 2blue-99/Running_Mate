package com.running.runningmate2.utils

import java.time.LocalDate
import java.time.LocalTime
import java.util.Calendar

/**
 * 2023-11-24
 * pureum
 */
class TimeHelper {
    private var _second = 0
    private var _minute = 0
    private var _hour = 0
    private var second = ""
    private var minute = ""
    private var hour = ""

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

    fun getCustomTime():String{
        _second++
        if (_second == 60) {
            _second = 0
            _minute++
        } else if (_minute == 60) {
            _hour++
            _minute = 0
        }

        if (_second.toString().length == 1)
            second = "0$_second"
        else
            second = "$_second"


        if (_minute.toString().length == 1)
            minute = "0$_minute"
        else
            minute = "$_minute"


        if (_hour.toString().length == 1)
            hour = "0$_hour"
        else
            hour = "$_hour"

        return "$hour:$minute:$second"
    }
}