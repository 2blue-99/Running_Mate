package com.running.domain.usecase

import com.running.domain.model.RunningData
import com.running.domain.repo.LocalDataRepo

/**
 * 2023-11-13
 * pureum
 */

class LocalDataUseCase(
    private val localDataRepo: LocalDataRepo
) {
    suspend fun insertData(userData: RunningData) = localDataRepo.insertData(userData)
    fun readAllData() = localDataRepo.readAllData()
    suspend fun deleteData(id: Int): Int = localDataRepo.deleteData(id)

}