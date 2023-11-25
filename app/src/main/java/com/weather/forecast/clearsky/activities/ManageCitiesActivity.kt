package com.weather.forecast.clearsky.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.weather.forecast.clearsky.databinding.ActivityManageCitiesBinding
import com.weather.forecast.clearsky.usecase.WeatherUseCase
import javax.inject.Inject

class ManageCitiesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityManageCitiesBinding
    @Inject private lateinit var useCase: WeatherUseCase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageCitiesBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}