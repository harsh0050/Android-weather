package com.weather.forecast.clearsky.network

import com.weather.forecast.clearsky.model.ImageModel
import retrofit2.http.GET
import retrofit2.http.Path

interface ImageGenerationService {
    @GET("api/images/{prompt}")
    suspend fun getImage(@Path("prompt") prompt: String) : ImageModel?
}