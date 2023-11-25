package com.weather.forecast.clearsky.utils

import android.graphics.Bitmap
import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.weather.forecast.clearsky.model.AirQuality
import com.weather.forecast.clearsky.model.Condition

@ProvidedTypeConverter
class CustomTypeConverters {
    @TypeConverter
    fun fromConditionToJson(condition: Condition): String{
        return Gson().toJson(condition)
    }

    @TypeConverter
    fun fromJsonToCondition(json: String): Condition{
        return Gson().fromJson(json, Condition::class.java)
    }

    @TypeConverter
    fun fromAirQualityToJson(airQuality: AirQuality): String{
        return Gson().toJson(airQuality)
    }

    @TypeConverter
    fun fromJsonToAirQuality(json: String): AirQuality {
        return Gson().fromJson(json, AirQuality::class.java)
    }

    @TypeConverter
    fun fromBitmap(bitmap: Bitmap): String{
        return Gson().toJson(bitmap)
    }

    @TypeConverter
    fun toBitmap(json: String): Bitmap {
        return Gson().fromJson(json, Bitmap::class.java)
    }


}