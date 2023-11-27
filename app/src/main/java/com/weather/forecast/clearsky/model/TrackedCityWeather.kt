package com.weather.forecast.clearsky.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tracked_city_weather_table")
data class TrackedCityWeather(
    @ColumnInfo(name = "id") @PrimaryKey var id: Int,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "lat") var lat: Float,
    @ColumnInfo(name = "lng") var lng: Float,
    @ColumnInfo(name = "temp") var temp: Double,
    @ColumnInfo(name = "condition") var condition: Condition,
    @ColumnInfo(name = "windSpeed") var windSpeed: Double,
    @ColumnInfo(name = "windDegree") var windDegree: Int,
    @ColumnInfo(name = "windDirection") var windDirection: String,
    @ColumnInfo(name = "pressureMb") var pressureMb: Double,
    @ColumnInfo(name = "humidity") var humidity: Int,
    @ColumnInfo(name = "realFeel") var realFeel: Double,
    @ColumnInfo(name = "uv") var uv: Double,
    @ColumnInfo(name = "airQuality") var airQuality: AirQuality,
    @ColumnInfo(name = "maxTemp") var maxTemp: Double,
    @ColumnInfo(name = "minTemp") var minTemp: Double,
    @ColumnInfo(name = "chanceOfRain") var chanceOfRain: Int,
    @ColumnInfo(name = "sunrise") var sunrise: String,
    @ColumnInfo(name = "sunset") var sunset: String,
    @ColumnInfo(name = "added_timestamp") val addedTimestamp: Long,
    @ColumnInfo(name = "image_byte_array") var imageByteArray: ByteArray
) {

    fun updateWeatherData(newWeatherModel: WeatherModel, imageByteArray: ByteArray){
        this.name = newWeatherModel.location.name
        this.lat = newWeatherModel.location.lat.toFloat()
        this.lng = newWeatherModel.location.lon.toFloat()
        this.temp = newWeatherModel.current.temp_c
        this.condition = newWeatherModel.current.condition
        this.windSpeed = newWeatherModel.current.wind_kph
        this.windDegree = newWeatherModel.current.wind_degree
        this.windDirection = newWeatherModel.current.wind_dir
        this.pressureMb = newWeatherModel.current.pressure_mb
        this.humidity = newWeatherModel.current.humidity
        this.realFeel = newWeatherModel.current.feelslike_c
        this.uv = newWeatherModel.current.uv
        this.airQuality = newWeatherModel.current.air_quality
        this.maxTemp = newWeatherModel.forecast.forecastday[0].day.maxtemp_c
        this.minTemp = newWeatherModel.forecast.forecastday[0].day.mintemp_c
        this.chanceOfRain = newWeatherModel.forecast.forecastday[0].day.daily_chance_of_rain
        this.sunrise = newWeatherModel.forecast.forecastday[0].astro.sunrise
        this.sunset = newWeatherModel.forecast.forecastday[0].astro.sunset
        this.imageByteArray = imageByteArray
    }


    companion object {
        fun newInstance(apiCityId: Int, weatherModel: WeatherModel, imageByteArray: ByteArray): TrackedCityWeather {
            return TrackedCityWeather(
                apiCityId,
                weatherModel.location.name,
                weatherModel.location.lat.toFloat(),
                weatherModel.location.lon.toFloat(),
                weatherModel.current.temp_c,
                weatherModel.current.condition,
                weatherModel.current.wind_kph,
                weatherModel.current.wind_degree,
                weatherModel.current.wind_dir,
                weatherModel.current.pressure_mb,
                weatherModel.current.humidity,
                weatherModel.current.feelslike_c,
                weatherModel.current.uv,
                weatherModel.current.air_quality,
                weatherModel.forecast.forecastday[0].day.maxtemp_c,
                weatherModel.forecast.forecastday[0].day.mintemp_c,
                weatherModel.forecast.forecastday[0].day.daily_chance_of_rain,
                weatherModel.forecast.forecastday[0].astro.sunrise,
                weatherModel.forecast.forecastday[0].astro.sunset,
                System.currentTimeMillis(),
                imageByteArray
            )
        }
    }


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TrackedCityWeather

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id
    }
}