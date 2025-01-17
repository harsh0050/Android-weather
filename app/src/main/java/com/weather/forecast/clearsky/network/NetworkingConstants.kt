package com.weather.forecast.clearsky.network

object NetworkingConstants {
    const val BASE_URL = "https://api.weatherapi.com/v1/"
    // example : https://api.weatherapi.com/v1/forecast.json?key=81d2f00f32eb4da4b4e45914210708%20&days=10&aqi=yes&alerts=yes&q=amsterdam
    const val GET_WEATHER = "forecast.json?key=81d2f00f32eb4da4b4e45914210708%20&days=10&aqi=yes&alerts=yes"
    const val GET_CITY_ID = "search.json?key=81d2f00f32eb4da4b4e45914210708"
    const val REST_API_URL = "https://overkill1109.pythonanywhere.com"
}