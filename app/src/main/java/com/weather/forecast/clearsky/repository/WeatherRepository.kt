package com.weather.forecast.clearsky.repository

import com.weather.forecast.clearsky.model.CorrectionModel
import com.weather.forecast.clearsky.model.ImageModel
import com.weather.forecast.clearsky.model.WeatherModel
import com.weather.forecast.clearsky.network.AutoCorrectionService
import com.weather.forecast.clearsky.network.ImageGenerationService
import com.weather.forecast.clearsky.network.WeatherApiService
import javax.inject.Inject

class WeatherRepository @Inject constructor(
    private val weatherApiService: WeatherApiService,
    private val autoCorrectionService: AutoCorrectionService,
    private val imageGenerationService: ImageGenerationService,
) {
    suspend fun getWeatherData(city: String): WeatherModel? {
        return weatherApiService.getWeatherData(city)
    }

    suspend fun getCorrected(location: String): CorrectionModel? {
        return autoCorrectionService.getCorrected(location)
    }

    suspend fun getImage(prompt: String): ImageModel? {
        return imageGenerationService.getImage(prompt)
    }
}