package com.weather.forecast.clearsky.network

sealed class ResultData<out T> {
    object Loading: ResultData<Nothing>()
    data class Success<out T>(val data: T): ResultData<T>()
    data class Failed(val message: String? = null): ResultData<Nothing>()
}