package com.weather.forecast.clearsky.mainscreen.viewmodel

import android.content.Context
import android.widget.ArrayAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.weather.forecast.clearsky.R
import com.weather.forecast.clearsky.model.CorrectionModel
import com.weather.forecast.clearsky.model.ImageModel
import com.weather.forecast.clearsky.model.TrackedCityWeather
import com.weather.forecast.clearsky.model.WeatherModel
import com.weather.forecast.clearsky.network.ResultData
import com.weather.forecast.clearsky.usecase.WeatherUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(private val useCase: WeatherUseCase) : ViewModel() {
    var visibleTextView: Int = R.id.firstTempTextView
    var invisibleTextView: Int = R.id.secondTempTextView
    var currentPage = 0

    fun getWeatherData(city: String): LiveData<ResultData<WeatherModel>> {
        return useCase.getWeatherData(city).asLiveData()
    }

    fun getImage(city: String, condition: String): LiveData<ResultData<ImageModel>> {
        return useCase.getImage(city,condition).asLiveData()
    }

    fun correctLocation(loc: String): LiveData<ResultData<CorrectionModel>> {
        return useCase.correctLocation(loc).asLiveData()
    }

    private fun getCitiesList(context: Context): ArrayList<String> {
        val inputStream = context.resources.openRawResource(R.raw.cities)
        val reader = BufferedReader(InputStreamReader(inputStream))
        var line : String? = reader.readLine()
        val citiesList = ArrayList<String>()
        while (line != null) {
            citiesList.add(line)
            line = reader.readLine()
        }
        return citiesList
    }
    fun enableCitiesSuggestion(context: Context, adapter: ArrayAdapter<String>){
        viewModelScope.launch {
            adapter.addAll(getCitiesList(context))
        }
    }
    fun disableCitiesSuggestion(adapter: ArrayAdapter<String>){
        adapter.clear()
    }

    fun getTrackedCities(): LiveData<List<TrackedCityWeather>> {
        return useCase.getTrackedCities()
    }

    fun trackCity(city: TrackedCityWeather){
        viewModelScope.launch {
            useCase.trackCity(city)
        }
    }
    suspend fun getTrackedCitiesCount(): Int{
        return useCase.getTrackedCitiesCount()
    }

    fun getInvisibleTextViewId(): Int {
        return invisibleTextView
    }

    fun getVisibleTextViewId(): Int{
        return visibleTextView
    }

    fun switchTextViews(){
        val temp = visibleTextView
        visibleTextView = invisibleTextView
        invisibleTextView = temp
    }



}