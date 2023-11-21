package com.running.domain.repo

import com.running.domain.model.RunningData

/**
 * 2023-11-21
 * pureum
 */
interface LocalDataRepo {
    suspend fun insertData(userData : RunningData)
    fun readAllData(): List<RunningData>
    suspend fun deleteAllData()
    suspend fun deleteData(id : Int)
}