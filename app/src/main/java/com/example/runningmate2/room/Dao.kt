package com.example.runningmate2.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.runningmate2.RunningData

@Dao
interface Dao {
    //데이터 엑세스 오브젝트
    //데이터를 엑세스 할 수 있는 공간(데이터 가공)

    @Insert
    fun putData(userData : Entity)

    @Query("select * from Entity")
    fun getData(): LiveData<List<Entity>>
}