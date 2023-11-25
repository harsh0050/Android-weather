package com.weather.forecast.clearsky.di

import com.weather.forecast.clearsky.network.AutoCorrectionService
import com.weather.forecast.clearsky.network.ImageGenerationService
import com.weather.forecast.clearsky.network.WeatherApiService
import com.weather.forecast.clearsky.repository.WeatherRepository
import com.weather.forecast.clearsky.room.CityDao
import com.weather.forecast.clearsky.room.TrackedCityDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
object DataRepositoryModule {
    @Provides
    fun provideDataRepository(
        weatherApiService: WeatherApiService,
        autoCorrectionService: AutoCorrectionService,
        imageGenerationService: ImageGenerationService,
        cityDao: CityDao,
        trackedCityDao: TrackedCityDao
    ): WeatherRepository {
        return WeatherRepository(
            weatherApiService,
            autoCorrectionService,
            imageGenerationService,
            cityDao,
            trackedCityDao
        )
    }
}