package com.example.runningmate2.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.runningmate2.RunningData
import kotlinx.coroutines.flow.Flow

@Dao
interface Dao {
    //데이터 엑세스 오브젝트
    //데이터를 엑세스 할 수 있는 공간(데이터 가공)

    @Insert
    suspend fun insertData(userData : Entity)

    @Query("select * from Entity")
    fun readAllData(): Flow<List<Entity>>

    @Query("DELETE FROM Entity")
    suspend fun deleteAllData()

    @Query("Delete from Entity Where id = :position")
    suspend fun deleteData(position : Int)

    @Delete
    suspend fun delete(userData : Entity)
}