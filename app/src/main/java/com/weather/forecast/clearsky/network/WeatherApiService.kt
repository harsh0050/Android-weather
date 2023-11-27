package com.weather.forecast.clearsky.network

import com.weather.forecast.clearsky.model.SearchLocationModel
import com.weather.forecast.clearsky.model.WeatherModel
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET(NetworkingConstants.GET_WEATHER)
    suspend fun getWeatherData(@Query("q") id: String): WeatherModel?

    @GET(NetworkingConstants.GET_CITY_ID)
    suspend fun searchCity(
        @Query("q") city: String,
    ): List<SearchLocationModel>?
}