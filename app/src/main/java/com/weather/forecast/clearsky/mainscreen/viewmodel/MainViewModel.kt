package com.weather.forecast.clearsky.mainscreen.viewmodel

import android.content.Context
import android.widget.ArrayAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.weather.forecast.clearsky.R
import com.weather.forecast.clearsky.interfaces.ICorrectLocationCallback
import com.weather.forecast.clearsky.interfaces.IWeatherImageCallback
import com.weather.forecast.clearsky.model.WeatherModel
import com.weather.forecast.clearsky.network.AutoCorrectionService
import com.weather.forecast.clearsky.network.ImageGenerationService
import com.weather.forecast.clearsky.network.ResultData
import com.weather.forecast.clearsky.network.RetrofitClient
import com.weather.forecast.clearsky.usecase.WeatherUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(private val useCase: WeatherUseCase) : ViewModel() {

    private val retrofitClient = RetrofitClient.getInstance()

    fun getWeatherData(city: String): LiveData<ResultData<WeatherModel>> {
        return useCase.getWeatherData(city).asLiveData()
    }

    fun getImage(prompt: String, callback: IWeatherImageCallback){
        val service = retrofitClient.create(ImageGenerationService::class.java)
        service.getImage(prompt).enqueue(object:Callback<JsonObject>{
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                val imgUrl= response.body()?.get("url")?.asString!!
                callback.onSuccess(imgUrl)
            }
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                callback.onFailure(t)
            }
        })
    }

    fun checkLocation(loc: String, callback: ICorrectLocationCallback){
        val service = retrofitClient.create(AutoCorrectionService::class.java)
        service.getCorrected(loc).enqueue(object : Callback<JsonObject>{
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                val location = response.body()?.get("location")?.asString!!
                callback.onCorrect(location)
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                callback.onCorrect(loc) //failed retrieving correct location
                // go with the old one
            }
        })
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

}