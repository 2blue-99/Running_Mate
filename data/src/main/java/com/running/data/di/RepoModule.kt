package com.running.data.di

import com.running.data.remote.dataSource.GetWeatherDataSource
import com.running.data.repoImpl.GetGetWeatherRepoImpl
import com.running.domain.repo.GetWeatherRepository
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
    fun provideRepository(dataSource: GetWeatherDataSource): GetWeatherRepository =
        GetGetWeatherRepoImpl(dataSource)
}