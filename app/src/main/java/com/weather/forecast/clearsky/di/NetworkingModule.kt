package com.weather.forecast.clearsky.di

import com.weather.forecast.clearsky.network.AutoCorrectionService
import com.weather.forecast.clearsky.network.ImageGenerationService
import com.weather.forecast.clearsky.network.NetworkingConstants
import com.weather.forecast.clearsky.network.WeatherApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

@Module
@InstallIn(SingletonComponent::class)
object NetworkingModule {
    @Annotations.WeatherApiUrl
    @Provides
    fun providesBaseUrl(): String {
        return NetworkingConstants.BASE_URL
    }

    @Annotations.OverkillApiUrl
    @Provides
    fun providesHarshUrl(): String {
        return NetworkingConstants.REST_API_URL
    }

    @Provides
    fun providesLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    @Provides
    fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        val okHttpClient = OkHttpClient().newBuilder()

        okHttpClient.callTimeout(40, TimeUnit.SECONDS)
        okHttpClient.connectTimeout(40, TimeUnit.SECONDS)
        okHttpClient.readTimeout(40, TimeUnit.SECONDS)
        okHttpClient.writeTimeout(40, TimeUnit.SECONDS)
        okHttpClient.addInterceptor(loggingInterceptor)
        okHttpClient.build()
        return okHttpClient.build()
    }

    @Provides
    fun provideConverterFactory(): Converter.Factory {
        return GsonConverterFactory.create()
    }

    @Annotations.WeatherRetrofitClient
    @Provides
    fun provideWeatherRetrofitClient(
        okHttpClient: OkHttpClient,
        @Annotations.WeatherApiUrl baseUrl: String,
        converterFactory: Converter.Factory,
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(converterFactory)
            .build()
    }

    @Annotations.OverkillRetrofitClient
    @Provides
    fun provideOverkillRetrofitClient(
        okHttpClient: OkHttpClient,
        @Annotations.OverkillApiUrl baseUrl: String,
        converterFactory: Converter.Factory,
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(converterFactory)
            .build()
    }

    @Provides
    fun provideWeatherApiService(@Annotations.WeatherRetrofitClient retrofit: Retrofit): WeatherApiService {
        return retrofit.create(WeatherApiService::class.java)
    }

    @Provides
    fun provideAutoCorrectionService(@Annotations.OverkillRetrofitClient retrofit: Retrofit): AutoCorrectionService {
        return retrofit.create(AutoCorrectionService::class.java)
    }

    @Provides
    fun provideImageGenerationService(@Annotations.OverkillRetrofitClient retrofit: Retrofit): ImageGenerationService {
        return retrofit.create(ImageGenerationService::class.java)
    }
}