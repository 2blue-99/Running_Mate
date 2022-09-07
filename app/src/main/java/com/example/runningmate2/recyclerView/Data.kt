package com.example.runningmate2.recyclerView

import android.graphics.drawable.Drawable
import com.example.runningmate2.room.Entity

data class Data(
    val id : Int,
    val time : String,
    val distance : String,
    val calorie : String,
    val step : String,
)

fun Entity.toData(): Data {
    return Data(
        id, time, distance, calorie, step
    )
}
