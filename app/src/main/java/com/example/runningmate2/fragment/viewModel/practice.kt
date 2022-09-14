package com.example.runningmate2.fragment.viewModel

import android.icu.util.LocaleData
import java.time.Instant.now
import java.time.LocalDate
import java.time.LocalDate.now
import java.time.LocalDateTime
import java.time.LocalDateTime.now
import java.time.LocalTime
import java.time.LocalTime.now
import java.time.MonthDay.now
import java.util.*

fun main(){
    val A = LocalDate.now()
    val B = LocalDateTime.now()
    val C = LocalTime.now()
    var nowTime = "${LocalDate.now()} ${LocalTime.now().hour}:${LocalTime.now().minute}"
    println("$nowTime")
}