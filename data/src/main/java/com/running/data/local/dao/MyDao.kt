package com.running.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.running.data.local.entity.RoomData
import kotlinx.coroutines.flow.Flow

@Dao
interface MyDao {
    @Insert
    suspend fun insertData(userData : RoomData)

    @Query("select * from RoomData")
    fun readAllData(): List<RoomData>

    @Query("DELETE FROM RoomData")
    suspend fun deleteAllData()

    @Query("Delete from RoomData Where id = :id")
    suspend fun deleteData(id : Int)

    @Delete
    suspend fun delete(userData : RoomData)
}