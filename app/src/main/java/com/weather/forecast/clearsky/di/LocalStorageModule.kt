package com.weather.forecast.clearsky.di

import android.content.Context
import com.weather.forecast.clearsky.room.CityDao
import com.weather.forecast.clearsky.room.RoomDB
import com.weather.forecast.clearsky.room.TrackedCityDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
object LocalStorageModule {
    @Provides
    fun provideRoomDb(context: Context): RoomDB {
        return RoomDB.getInstance(context)
    }

    @Provides
    fun provideCityDao(roomDB: RoomDB): CityDao {
        return roomDB.getCityDao()
    }

    @Provides
    fun provideTrackedCityDao(roomDB: RoomDB): TrackedCityDao {
        return roomDB.getTrackedCityDao()
    }
}