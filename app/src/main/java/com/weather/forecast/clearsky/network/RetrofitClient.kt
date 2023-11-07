package com.weather.forecast.clearsky.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClient {
    companion object {
        @Volatile
        private var INSTANCE: Retrofit? = null

        fun getInstance(): Retrofit {
            if (INSTANCE == null) {
                val client = Retrofit.Builder()
                    .baseUrl(NetworkingConstants.REST_API_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                INSTANCE = client
            }
            return INSTANCE!!
        }
    }
}