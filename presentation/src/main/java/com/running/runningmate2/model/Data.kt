package com.running.runningmate2.model

import com.running.runningmate2.room.Entity

data class Data(
    val id : Int,
    val now : String,
    val time : String,
    val distance : String,
    val calorie : String,
    val step : String,
)

fun Entity.toData(): Data {
    return Data(
        id, now, time, distance, calorie, step
    )
}
