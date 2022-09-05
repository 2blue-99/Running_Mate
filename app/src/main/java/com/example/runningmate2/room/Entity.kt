package com.example.runningmate2.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Entity (
    @ColumnInfo val time:String,
    @ColumnInfo val distance:String,
    @ColumnInfo val calorie:String,
    @ColumnInfo val step:String
){
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}
