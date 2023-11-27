package com.weather.forecast.clearsky.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.weather.forecast.clearsky.mainscreen.ui.fragment.WeatherDisplayFragment
import com.weather.forecast.clearsky.model.TrackedCityWeather

class MainViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    val currentPage = 0
    private var trackedCities :List<TrackedCityWeather> = ArrayList()
    override fun getItemCount(): Int {
        return trackedCities.size
    }

    override fun createFragment(position: Int): Fragment {
        return WeatherDisplayFragment(trackedCities[position])
    }
    fun setTrackedCities(list: List<TrackedCityWeather>){
        trackedCities = list
        notifyDataSetChanged()
    }

}