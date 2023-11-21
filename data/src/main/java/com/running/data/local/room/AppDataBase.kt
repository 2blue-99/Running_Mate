package com.running.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.running.data.local.room.dao.MyDao
import com.running.data.local.room.entity.RoomData

@Database(entities = [RoomData::class], version = 1, exportSchema = false)
abstract class AppDataBase:RoomDatabase() {
    abstract fun getDao() : MyDao
}