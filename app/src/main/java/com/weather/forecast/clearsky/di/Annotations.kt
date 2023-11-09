package com.weather.forecast.clearsky.di

import javax.inject.Qualifier

class Annotations {
    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class WeatherApiUrl

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class OverkillApiUrl

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class WeatherRetrofitClient

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class OverkillRetrofitClient
}

