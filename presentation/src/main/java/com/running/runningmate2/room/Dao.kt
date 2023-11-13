package com.running.runningmate2.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface Dao {
    @Insert
    suspend fun insertData(userData : Entity)

    @Query("select * from Entity")
    fun readAllData(): Flow<List<Entity>>

    @Query("DELETE FROM Entity")
    suspend fun deleteAllData()

    @Query("Delete from Entity Where id = :id")
    suspend fun deleteData(id : Int)

    @Delete
    suspend fun delete(userData : Entity)
}