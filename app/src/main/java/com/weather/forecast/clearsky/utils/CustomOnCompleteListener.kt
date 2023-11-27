package com.weather.forecast.clearsky.utils

fun interface CustomOnCompleteListener<TResult> {
    fun onComplete(result: TResult)
}