package com.running.data.di

import com.running.data.local.location.LocationDataHelper
import com.running.data.local.room.AppDataBase
import com.running.data.remote.dataSource.WeatherDataSource
import com.running.data.remote.dataSourceImpl.WeatherDataSourceImpl
import com.running.data.repoImpl.WeatherDataDataRepoImpl
import com.running.data.repoImpl.LocalDataRepoImpl
import com.running.data.repoImpl.LocationDataRepoImpl
import com.running.domain.repo.WeatherDataRepo
import com.running.domain.repo.LocalDataRepo
import com.running.domain.repo.LocationDataRepo
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
    fun provideWeatherRepo(dataSource: WeatherDataSourceImpl): WeatherDataRepo =
        WeatherDataDataRepoImpl(dataSource)

    @Singleton
    @Provides
    fun provideRoomRepo(source: AppDataBase): LocalDataRepo =
        LocalDataRepoImpl(source.getDao())

    @Singleton
    @Provides
    fun provideLocationRepo(helper: LocationDataHelper): LocationDataRepo =
        LocationDataRepoImpl(helper)
}