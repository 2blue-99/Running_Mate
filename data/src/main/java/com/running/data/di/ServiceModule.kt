package com.running.data.di

import com.running.data.BuildConfig
import com.running.data.remote.dataSource.WeatherDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/**
 * 2023-11-13
 * pureum
 */

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {

    @Singleton
    @Provides
    fun provideWeatherService(retrofit: Retrofit): WeatherDataSource =
        retrofit.create(WeatherDataSource::class.java)
}