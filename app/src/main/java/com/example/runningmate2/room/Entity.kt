package com.example.runningmate2.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity
data class Entity (
    @PrimaryKey(autoGenerate = true) var id: Int,
    @ColumnInfo var now: String,
    @ColumnInfo val time:String,
    @ColumnInfo val distance:String,
    @ColumnInfo val calorie:String,
    @ColumnInfo val step:String
)