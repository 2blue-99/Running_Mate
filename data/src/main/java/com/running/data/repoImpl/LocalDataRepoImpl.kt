package com.running.data.repoImpl

import com.running.data.local.room.dao.MyDao
import com.running.data.local.room.entity.toLocalData
import com.running.data.mapper.DataMapper.toRoomData
import com.running.domain.model.RunningData
import com.running.domain.repo.LocalDataRepo
import javax.inject.Inject

/**
 * 2023-11-21
 * pureum
 */
class LocalDataRepoImpl @Inject constructor(
    private val roomMyDao: MyDao
): LocalDataRepo {
    override suspend fun insertData(runningData: RunningData) =
        roomMyDao.insertData(runningData.toRoomData())

    override fun readAllData(): List<RunningData> =
        roomMyDao.readAllData().map { it.toLocalData() }


//    override suspend fun deleteAllData() =
//        roomMyDao.deleteAllData()

    override suspend fun deleteData(id: Int) = roomMyDao.deleteData(id)
}