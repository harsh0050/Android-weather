package com.weather.forecast.clearsky.repository

import androidx.lifecycle.LiveData
import com.weather.forecast.clearsky.model.City
import com.weather.forecast.clearsky.model.CorrectionModel
import com.weather.forecast.clearsky.model.ImageModel
import com.weather.forecast.clearsky.model.TrackedCityWeather
import com.weather.forecast.clearsky.model.WeatherModel
import com.weather.forecast.clearsky.network.AutoCorrectionService
import com.weather.forecast.clearsky.network.ImageGenerationService
import com.weather.forecast.clearsky.network.WeatherApiService
import com.weather.forecast.clearsky.room.CityDao
import com.weather.forecast.clearsky.room.TrackedCityDao
import javax.inject.Inject

class WeatherRepository @Inject constructor(
    private val weatherApiService: WeatherApiService,
    private val autoCorrectionService: AutoCorrectionService,
    private val imageGenerationService: ImageGenerationService,
    private val cityDao: CityDao,
    private val trackedCityDao: TrackedCityDao
) {
    suspend fun getWeatherData(city: String): WeatherModel? {
        return weatherApiService.getWeatherData(city)
    }

    suspend fun getCorrected(location: String): CorrectionModel? {
        return autoCorrectionService.getCorrected(location)
    }

    suspend fun getImage(city: String, condition: String): ImageModel? {
        return imageGenerationService.getImage(city, condition)
    }

    suspend fun getCitiesList(): List<City> {
        return cityDao.getCitiesData()
    }

    fun getTrackedCities(): LiveData<List<TrackedCityWeather>> {
        return trackedCityDao.getTrackedCities()
    }
    suspend fun trackCity(city: TrackedCityWeather){
        trackedCityDao.trackCity(city)
    }
    suspend fun removeCity(city: TrackedCityWeather){
        trackedCityDao.removeCity(city)
    }
    suspend fun updateWeather(newCityData: TrackedCityWeather){
        trackedCityDao.updateWeather(newCityData)
    }
    suspend fun getTrackedCitiesCount(): Int {
        return trackedCityDao.getSize()
    }

}