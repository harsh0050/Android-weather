package com.weather.forecast.clearsky.network

import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ImageGenerationService {
    @GET("api/images/{prompt}")
    fun getImage(@Path("prompt") prompt: String) : Call<JsonObject>
}