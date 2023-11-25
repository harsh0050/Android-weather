package com.weather.forecast.clearsky.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cities_table")
data class City(
    @ColumnInfo(name = "city") val city: String,
    @ColumnInfo(name = "lat") val lat: Float,
    @ColumnInfo(name = "lng") val lng: Float,
    @ColumnInfo(name = "country") val country: String,
    @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = false) val id: Int
)
