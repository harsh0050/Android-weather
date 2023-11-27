package com.weather.forecast.clearsky.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.weather.forecast.clearsky.model.City

@Dao
interface CityDao {
    @Query("SELECT * FROM cities_table ORDER BY city")
    fun getCitiesData(): LiveData<List<City>>

    @Query("UPDATE cities_table SET isTracked = 1 WHERE id = :id")
    suspend fun setTrackStatus(id: Int)

}
