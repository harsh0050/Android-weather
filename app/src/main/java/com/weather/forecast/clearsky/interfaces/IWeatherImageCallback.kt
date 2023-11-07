package com.weather.forecast.clearsky.interfaces

interface IWeatherImageCallback {
    fun onSuccess(url : String)
    fun onFailure(e: Throwable)
}