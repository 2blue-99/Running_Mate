package com.running.data.di

import com.running.data.remote.dataSource.GetWeatherDataSource
import com.running.data.remote.dataSourceImpl.GetWeatherDataSourceImpl
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
    fun provideDataSource(retrofit: Retrofit): GetWeatherDataSource =
        GetWeatherDataSourceImpl(retrofit)
}