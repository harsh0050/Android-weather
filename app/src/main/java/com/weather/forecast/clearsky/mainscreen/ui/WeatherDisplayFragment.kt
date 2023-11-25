package com.weather.forecast.clearsky.mainscreen.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.weather.forecast.clearsky.databinding.FragmentWeatherDisplayBinding
import com.weather.forecast.clearsky.mainscreen.viewmodel.MainViewModel
import com.weather.forecast.clearsky.model.TrackedCityWeather

class WeatherDisplayFragment(private val weatherData: TrackedCityWeather) : Fragment() {

    private lateinit var binding : FragmentWeatherDisplayBinding
    private val model: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentWeatherDisplayBinding.inflate(inflater)
        binding.windDirectionTextView.text = expandDirection(weatherData.windDirection)
        binding.windSpeedTextView.text = weatherData.windSpeed.toString()+"km/h"
        binding.compassNeedle.rotation = weatherData.windDegree.toFloat()
        binding.sunriseTextView.text = weatherData.sunrise
        binding.sunsetTextView.text = weatherData.sunset
        binding.humidityTextView.text = weatherData.humidity.toString()+"%"
        binding.realFeelTextView.text = weatherData.realFeel.toString()+"°"
        binding.uvTextView.text = weatherData.uv.toString()
        binding.pressureTextView.text = weatherData.pressureMb.toString()+" mbar"
        binding.chanceOfRainTextView.text = weatherData.chanceOfRain.toString()+"%"
        return binding.root
    }
    private fun expandDirection(direction: String): String {
        return when(direction){
            "N"->"North"
            "S"->"South"
            "E"->"East"
            "W"->"West"
            "NE"->"Northeast"
            "NW"->"Northwest"
            "SE"->"Southeast"
            "SW"->"Southwest"
            else->direction
        }
    }

}