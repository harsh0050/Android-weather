package com.weather.forecast.clearsky.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.weather.forecast.clearsky.R
import com.weather.forecast.clearsky.model.TrackedCityWeather

class ManageCitiesTrackedCitiesAdapter :
    RecyclerView.Adapter<ManageCitiesTrackedCitiesAdapter.CustomViewHolder>() {
    private var trackedCities: List<TrackedCityWeather> = ArrayList()

    inner class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var city: TextView = itemView.findViewById(R.id.city_name)
        var temperatureAndAqi: TextView = itemView.findViewById(R.id.temperature_aqi)
        var temperature: TextView = itemView.findViewById(R.id.big_temperature)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.tracked_city_item_view, parent, false)
        val holder = CustomViewHolder(view)
        return holder
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val curr = trackedCities[position]
        holder.city.text = curr.name
        holder.temperature.text = "${curr.temp.toInt()}°"
        holder.temperatureAndAqi.text = "AQI ${curr.airQuality.pm2_5.toInt()}  ${curr.maxTemp.toInt()}° / ${curr.minTemp.toInt()}°"
    }

    override fun getItemCount(): Int {
        return trackedCities.size
    }

    fun setTrackedCities(newList: List<TrackedCityWeather>) {
        this.trackedCities = newList
        notifyDataSetChanged()
    }
}