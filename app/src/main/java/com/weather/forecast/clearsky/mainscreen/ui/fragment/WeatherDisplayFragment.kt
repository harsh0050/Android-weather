package com.weather.forecast.clearsky.mainscreen.ui.fragment

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
        binding.windDirectionTextview.text = expandDirection(weatherData.windDirection)
        binding.windSpeedTextview.text = weatherData.windSpeed.toString()+"km/h"
        binding.compassNeedle.rotation = weatherData.windDegree.toFloat()
        binding.sunriseTextview.text = weatherData.sunrise
        binding.sunsetTextview.text = weatherData.sunset
        binding.humidityTextview.text = weatherData.humidity.toString()+"%"
        binding.realFeelTextview.text = weatherData.realFeel.toString()+"°"
        binding.uvTextview.text = weatherData.uv.toString()
        binding.pressureTextview.text = weatherData.pressureMb.toString()+" mbar"
        binding.chanceOfRainTextview.text = weatherData.chanceOfRain.toString()+"%"
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