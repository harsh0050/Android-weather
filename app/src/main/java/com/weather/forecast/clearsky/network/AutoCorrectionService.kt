package com.weather.forecast.clearsky.network

import com.weather.forecast.clearsky.model.CorrectionModel
import retrofit2.http.GET
import retrofit2.http.Path

interface AutoCorrectionService {
    @GET("api/correct/{location}")
    suspend fun getCorrected(@Path("location") loc: String): CorrectionModel?
}