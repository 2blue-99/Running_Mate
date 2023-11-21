package com.running.data.di

import com.running.data.local.room.AppDataBase
import com.running.data.remote.dataSource.GetWeatherDataSource
import com.running.data.repoImpl.GetWeatherRepoImpl
import com.running.data.repoImpl.LocalDataRepoImpl
import com.running.domain.repo.GetWeatherRepo
import com.running.domain.repo.LocalDataRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * 2023-11-13
 * pureum
 */
@Module
@InstallIn(SingletonComponent::class)
object RepoModule {

    @Singleton
    @Provides
    fun provideWeatherRepo(dataSource: GetWeatherDataSource): GetWeatherRepo =
        GetWeatherRepoImpl(dataSource)

    @Singleton
    @Provides
    fun provideRoomRepo(source: AppDataBase): LocalDataRepo =
        LocalDataRepoImpl(source.getDao())
}