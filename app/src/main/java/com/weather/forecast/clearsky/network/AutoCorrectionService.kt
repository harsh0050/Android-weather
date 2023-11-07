package com.weather.forecast.clearsky.network

import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface AutoCorrectionService {
    @GET("api/correct/{location}")
    fun getCorrected(@Path("location") loc: String): Call<JsonObject>
}