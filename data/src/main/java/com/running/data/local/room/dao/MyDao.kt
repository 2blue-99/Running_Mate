package com.running.data.local.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.running.data.local.room.entity.RoomData
import kotlinx.coroutines.flow.Flow

@Dao
interface MyDao {
    @Insert
    fun insertData(userData : RoomData)

    @Query("select * from RoomData")
    fun readAllData(): List<RoomData>

//    @Query("DELETE FROM RoomData")
//    suspend fun deleteAllData()

    @Query("Delete from RoomData Where id = :id")
    fun deleteData(id : Int):Int

//    @Delete
//    suspend fun delete(userData : RoomData)
}