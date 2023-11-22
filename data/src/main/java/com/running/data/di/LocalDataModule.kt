package com.running.data.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.running.data.local.location.LocationDataHelper
import com.running.data.local.location.LocationDataHelperImpl
import com.running.data.local.room.AppDataBase
import com.running.data.local.sharedPreference.SharedPreferenceHelperImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * 2023-11-13
 * pureum
 */


@Module
@InstallIn(SingletonComponent::class)
object LocalDataModule {
    @Singleton
    @Provides
    fun provideAppDataBase(@ApplicationContext appContext: Context): AppDataBase =
        Room.databaseBuilder(appContext, AppDataBase::class.java, "myDataBase")
            .build()

    @Singleton
    @Provides
    fun providePreference(@ApplicationContext appContext: Context): SharedPreferenceHelperImpl =
        SharedPreferenceHelperImpl(appContext.getSharedPreferences(appContext.packageName, Context.MODE_PRIVATE))

    @Singleton
    @Provides
    fun provideLocation(@ApplicationContext appContext: Context): LocationDataHelper =
        LocationDataHelperImpl(appContext)
}