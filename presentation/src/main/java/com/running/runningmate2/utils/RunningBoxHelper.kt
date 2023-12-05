package com.running.runningmate2.utils

import android.util.Log
import com.running.domain.model.RunningData
import com.running.runningmate2.databinding.BoxRecodeBinding
import com.running.runningmate2.databinding.FragmentRecordBinding

/**
 * 2023-12-05
 * pureum
 */
object RunningBoxHelper {
    fun makeRunningBox(dataList: List<RunningData>): RunningData{
        var seconde = 0
        var minute = 0
        var hour = 0
        var calorie = 0.0
        var step = 0
        var distance = 0.0
        var date = 0
        var nowDay = ""
        var _minute = ""
        var _seconde = ""
        var _hour = ""
        for ( data in dataList) {
            Log.e("TAG", "record initObserver: $data", )
            if (nowDay != data.now.split(" ")[0]) {
                date++
                nowDay = data.now.split(" ")[0]
            }
            seconde += data.time.split(":")[2].toInt()
            minute += data.time.split(":")[1].toInt()
            hour += data.time.split(":")[0].toInt()
            calorie += data.calorie.split(" ")[0].toDouble()
            step += data.step.split(" ")[0].toInt()
            distance += data.distance.split(" ")[0].toDouble()
        }
        if (seconde >= 60) {
            val gap = seconde/60
            minute += seconde / 60
            seconde -= 60 * gap
        }
        if (minute >= 60) {
            val gap = minute/60
            hour += minute / 60
            minute -= 60 * gap
        }

        _seconde = if (seconde.toString().length == 1) "0$seconde"
        else seconde.toString()

        _minute = if (minute.toString().length == 1) "0$minute"
        else minute.toString()

        _hour = if(hour.toString().length == 1) "0$hour"
        else hour.toString()

        return RunningData(
            dayOfWeek = "${date}일",
            time = "${_hour}시간 ${_minute}분 ${_seconde}초",
            distance = "${String.format("%.2f", distance)} M",
            calorie = "${String.format("%.2f", calorie)} Kcal",
            step = "${step}걸음"
        )
    }

    fun makeFakeBox() = RunningData(
        dayOfWeek = "0일",
        time = "00시간 00분 00초",
        distance = "0.00 M",
        calorie = "0.00 Kcal",
        step = "0걸음"
    )
}