package com.weather.forecast.clearsky.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.weather.forecast.clearsky.model.City
import com.weather.forecast.clearsky.model.TrackedCityWeather
import com.weather.forecast.clearsky.utils.CustomTypeConverters

@Database(entities = [City::class, TrackedCityWeather::class], version = 2)
@TypeConverters(CustomTypeConverters::class)
abstract class RoomDB : RoomDatabase() {
    abstract fun getCityDao(): CityDao
    abstract fun getTrackedCityDao(): TrackedCityDao

    companion object {
        @Volatile
        private var INSTANCE: RoomDB? = null
        fun getInstance(context: Context): RoomDB {


            if (INSTANCE != null) {
                return INSTANCE!!
            }
            INSTANCE = Room.databaseBuilder(context, RoomDB::class.java, "database.db")
                .createFromAsset("room_database.db")
//                .fallbackToDestructiveMigration()
                .addTypeConverter(CustomTypeConverters())
                .build()
            return INSTANCE!!
        }
    }
}