package com.running.data.di

import com.running.domain.repo.WeatherDataRepo
import com.running.domain.repo.LocalDataRepo
import com.running.domain.repo.LocationDataRepo
import com.running.domain.usecase.GetWeatherUseCase
import com.running.domain.usecase.LocalDataUseCase
import com.running.domain.usecase.LocationUseCase
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
object UseCaseModule {

    @Provides
    @Singleton
    fun provideWeatherUseCase(repo: WeatherDataRepo): GetWeatherUseCase =
        GetWeatherUseCase(repo)

    @Provides
    @Singleton
    fun provideLocalUseCase(repo: LocalDataRepo): LocalDataUseCase =
        LocalDataUseCase(repo)

    @Provides
    @Singleton
    fun provideLocationUseCase(repo: LocationDataRepo): LocationUseCase =
        LocationUseCase(repo)

}