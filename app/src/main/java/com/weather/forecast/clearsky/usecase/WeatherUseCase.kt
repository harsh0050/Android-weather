package com.weather.forecast.clearsky.usecase

import androidx.lifecycle.LiveData
import com.weather.forecast.clearsky.model.City
import com.weather.forecast.clearsky.model.CorrectionModel
import com.weather.forecast.clearsky.model.ImageModel
import com.weather.forecast.clearsky.model.TrackedCityWeather
import com.weather.forecast.clearsky.model.WeatherModel
import com.weather.forecast.clearsky.network.ResultData
import com.weather.forecast.clearsky.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class WeatherUseCase @Inject constructor(
    private val weatherRepository: WeatherRepository,
) {
    suspend fun getWeatherData(id: String): WeatherModel? {
        return weatherRepository.getWeatherData(id)
    }
    suspend fun getCityId(city: String): Int? {
        return weatherRepository.getCityId(city)
    }

    suspend fun getImage(city: String, condition: String): ImageModel? {
        return weatherRepository.getImage(city,condition)
    }
//    fun getWeatherData(id: String): Flow<ResultData<WeatherModel>> {
//        return flow {
//            emit(ResultData.Loading)
//
//            val weatherModel = weatherRepository.getWeatherData(id)
//
//            val resultData = if (weatherModel == null) {
//                ResultData.Failed()
//            } else {
//                ResultData.Success(weatherModel)
//            }
//            emit(resultData)
//        }.catch {
//            emit(ResultData.Failed())
//        }
//    }

//    fun getCityId(city: String): Flow<ResultData<Int>> {
//        return flow {
//            emit(ResultData.Loading)
//
//            val cityId = weatherRepository.getCityId(city)
//
//            val resultData = if (cityId == null) {
//                ResultData.Failed()
//            } else {
//                ResultData.Success(cityId)
//            }
//            emit(resultData)
//        }.catch {
//            emit(ResultData.Failed())
//        }
//    }
//
//    fun getImage(city: String, condition: String): Flow<ResultData<ImageModel>> {
//        val flow = flow {
//            emit(ResultData.Loading)
//            val imageModel = weatherRepository.getImage(city, condition)
//
//            if (imageModel == null) {
//                emit(ResultData.Failed())
//            } else {
//                emit(ResultData.Success(imageModel))
//            }
//        }.catch {
//            emit(ResultData.Failed())
//        }
//        return flow
//    }

    fun correctLocation(location: String): Flow<ResultData<CorrectionModel>> {
        val flow = flow {
            emit(ResultData.Loading)
            val correctionModel = weatherRepository.getCorrected(location)

            if (correctionModel == null) {
                emit(ResultData.Failed())
            } else {
                emit(ResultData.Success(correctionModel))
            }
        }.catch {
            emit(ResultData.Failed())
        }
        return flow
    }

    fun getCitiesList(): LiveData<List<City>> {
        return weatherRepository.getCitiesList()
    }

    suspend fun setTrackStatus(id: Int) {
        weatherRepository.setTrackStatus(id)
    }


    fun getTrackedCitiesLiveData(): LiveData<List<TrackedCityWeather>> {
        return weatherRepository.getTrackedCitiesLiveData()
    }

    suspend fun getTrackedCities(): List<TrackedCityWeather> {
        return weatherRepository.getTrackedCities()
    }

    suspend fun trackCity(city: TrackedCityWeather) {
        weatherRepository.trackCity(city)
    }

    suspend fun removeCity(city: TrackedCityWeather) {
        weatherRepository.removeCity(city)
    }

    suspend fun updateWeather(newCityData: TrackedCityWeather) {
        weatherRepository.updateWeather(newCityData)
    }

    suspend fun getTrackedCitiesCount(): Int {
        return weatherRepository.getTrackedCitiesCount()
    }

}