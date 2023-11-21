package com.running.data.local.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.running.domain.model.RunningData

@Entity
data class RoomData (
    @PrimaryKey(autoGenerate = true) var id: Int,
    @ColumnInfo var dayOfWeek: String,
    @ColumnInfo var now: String,
    @ColumnInfo val time:String,
    @ColumnInfo val distance:String,
    @ColumnInfo val calorie:String,
    @ColumnInfo val step:String
)

fun RoomData.toLocalData(): RunningData =
    RunningData(id, dayOfWeek, now, time, distance, calorie, step)
