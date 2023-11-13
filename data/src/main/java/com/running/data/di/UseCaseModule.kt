package com.running.data.di

import com.running.data.repoImpl.GetGetWeatherRepoImpl
import com.running.domain.repo.GetWeatherRepository
import com.running.domain.usecase.GetWeatherUseCase
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
    fun provideUseCase(repo: GetWeatherRepository): GetWeatherUseCase =
        GetWeatherUseCase(repo)

}