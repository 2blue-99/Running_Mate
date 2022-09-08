package com.example.runningmate2.recyclerView

import android.graphics.drawable.Drawable
import android.location.Location
import com.example.runningmate2.room.Entity
import java.time.LocalDateTime

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
