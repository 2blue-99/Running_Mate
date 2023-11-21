package com.running.data.di

import com.running.data.remote.dataSource.WeatherDataSource
import com.running.data.remote.dataSourceImpl.WeatherDataSourceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

/**
 * 2023-11-13
 * pureum
 */
@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule {

    @Singleton
    @Provides
    fun provideDataSource(retrofit: Retrofit): WeatherDataSource =
        WeatherDataSourceImpl(retrofit)
}