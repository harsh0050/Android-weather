package com.weather.forecast.clearsky.network

import com.weather.forecast.clearsky.model.ImageModel
import retrofit2.http.GET
import retrofit2.http.Path

interface ImageGenerationService {
    @GET("api/images/{city}/{condition}")
    suspend fun getImage(@Path("city") city: String,@Path("condition") condition: String) : ImageModel?
}