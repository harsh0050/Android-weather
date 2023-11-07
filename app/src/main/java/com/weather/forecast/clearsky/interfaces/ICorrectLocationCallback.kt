package com.weather.forecast.clearsky.interfaces

fun interface ICorrectLocationCallback {
    fun onCorrect(corrected: String)
}