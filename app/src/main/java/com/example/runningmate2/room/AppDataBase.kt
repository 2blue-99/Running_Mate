package com.example.runningmate2.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Entity::class], version = 1, exportSchema = false)
abstract class AppDataBase:RoomDatabase() {
    abstract fun getDao() :Dao
}