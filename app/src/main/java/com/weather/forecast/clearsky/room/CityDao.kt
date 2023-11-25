package com.weather.forecast.clearsky.room

import androidx.room.Dao
import androidx.room.Query
import com.weather.forecast.clearsky.model.City

@Dao
interface CityDao {
    @Query("SELECT * FROM cities_table ORDER BY city")
    suspend fun getCitiesData(): List<City>

}
