package com.example.weather_app.di

import android.content.Context
import com.example.weather_app.data.local.DatabaseHelper
import com.example.weather_app.data.remote.WeatherApi
import com.example.weather_app.data.repository.UserRepositoryImpl
import com.example.weather_app.data.repository.WeatherRepositoryImpl
import com.example.weather_app.domain.repository.UserRepository
import com.example.weather_app.domain.repository.WeatherRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabaseHelper(@ApplicationContext context: Context): DatabaseHelper = DatabaseHelper(context)

    @Provides
    @Singleton
    fun provideUserRepository(dbHelper: DatabaseHelper): UserRepository =
        UserRepositoryImpl(dbHelper)

    @Provides
    @Singleton
    fun provideWeatherRepository(
        api: WeatherApi,
        dbHelper: DatabaseHelper
    ): WeatherRepository = WeatherRepositoryImpl(api, dbHelper)
}