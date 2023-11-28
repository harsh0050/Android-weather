package com.weather.forecast.clearsky.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.weather.forecast.clearsky.model.TrackedCityWeather

@Dao
interface TrackedCityDao {
    @Query("SELECT * FROM tracked_city_weather_table ORDER BY added_timestamp")
    fun getTrackedCitiesLiveData(): LiveData<List<TrackedCityWeather>>

    @Query("SELECT * FROM tracked_city_weather_table ORDER BY added_timestamp")
    suspend fun getTrackedCities(): List<TrackedCityWeather>
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun trackCity(city: TrackedCityWeather)
    @Delete
    suspend fun removeCity(city: TrackedCityWeather)
    @Update
    suspend fun updateWeather(newData: TrackedCityWeather)

    @Query("SELECT COUNT(id) FROM tracked_city_weather_table")
    suspend fun getSize(): Int
}