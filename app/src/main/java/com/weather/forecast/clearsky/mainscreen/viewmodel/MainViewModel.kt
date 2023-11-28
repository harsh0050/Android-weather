package com.weather.forecast.clearsky.mainscreen.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.weather.forecast.clearsky.R
import com.weather.forecast.clearsky.model.City
import com.weather.forecast.clearsky.model.CorrectionModel
import com.weather.forecast.clearsky.model.TrackedCityWeather
import com.weather.forecast.clearsky.network.ResultData
import com.weather.forecast.clearsky.usecase.WeatherUseCase
import com.weather.forecast.clearsky.utils.CustomOnCompleteListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    private val useCase: WeatherUseCase,
    private val application: Application,
) : AndroidViewModel(application) {
    private var visibleTextView: Int = R.id.firstTempTextView
    private var invisibleTextView: Int = R.id.secondTempTextView

    fun trackCity(city: City): LiveData<ResultData<Nothing?>> {
        //TODO integrate enqueue city instead

        return flow {
            emit(ResultData.Loading)
            val cityId = useCase.getCityId(city.city)
            if (cityId == null) {
                emit(ResultData.Failed())
            } else {
                val weatherData = useCase.getWeatherData("id:$cityId")
                if (weatherData == null) {
                    emit(ResultData.Failed())
                } else {
                    val imageUrl = useCase.getImage(
                        weatherData.location.name,
                        weatherData.current.condition.text
                    )
                    if (imageUrl == null) {
                        emit(ResultData.Failed())
                    } else {
                        val imgBitmap =
                            Glide.with(application.applicationContext).asBitmap().load(imageUrl.url)
                                .submit().get()
                        val cityInstance = TrackedCityWeather.newInstance(
                            cityId,
                            weatherData,
                            getDarkerByteArray(imgBitmap)
                        )
                        useCase.trackCity(cityInstance)
                        useCase.setTrackStatus(city.id)

                        //TODO return data type?
                        emit(ResultData.Success(null))
                    }
                }
            }
        }.flowOn(Dispatchers.IO).catch {
            emit(ResultData.Failed(it.toString()))
        }.asLiveData()
    }

    fun reloadAllCities(): LiveData<ResultData<Nothing?>> {
        //TODO return data type?
        return flow {
            emit(ResultData.Loading)
            val trackedCities = useCase.getTrackedCities()
            trackedCities.forEach {
                val currData = useCase.getWeatherData("id:${it.id}")
                if (currData == null) {
                    emit(ResultData.Failed())
                } else {

                    val imgUrl =
                        useCase.getImage(currData.location.name, currData.current.condition.text)
                    if (imgUrl == null) {
                        emit(ResultData.Failed())
                    } else {
                        val imgBitmap =
                            Glide.with(application.applicationContext).asBitmap().load(imgUrl.url)
                                .submit().get()
                        it.updateWeatherData(currData, getDarkerByteArray(imgBitmap))
                        useCase.updateWeather(it)
                    }
                }
            }
            emit(ResultData.Success(null))

        }.flowOn(Dispatchers.IO).catch {
            ResultData.Failed(it.toString())
        }.asLiveData()
    }
    private fun getDarkerByteArray(immutableBitmap: Bitmap): ByteArray {
        val mutableBitmap = immutableBitmap.copy(immutableBitmap.config, true)
        val width = mutableBitmap.width - 1
        val height = mutableBitmap.height - 1
        for (x in 0..width) {
            for (y in 0..height) {
                var color = mutableBitmap.getPixel(x, y)
                val hsv = FloatArray(3)
                Color.colorToHSV(color, hsv)
                hsv[2] *= 0.6f
                color = Color.HSVToColor(hsv)
                mutableBitmap.setPixel(x, y, color)
            }
        }
        val outputStream = ByteArrayOutputStream()
        mutableBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        return outputStream.toByteArray()
    }

    fun correctLocation(loc: String): LiveData<ResultData<CorrectionModel>> {
        return useCase.correctLocation(loc).asLiveData()
    }

    fun getTrackedCitiesLiveData(): LiveData<List<TrackedCityWeather>> {
        return useCase.getTrackedCitiesLiveData()
    }

    fun getTrackedCities(callback: CustomOnCompleteListener<List<TrackedCityWeather>>) {
        viewModelScope.launch {
            callback.onComplete(useCase.getTrackedCities())
        }
    }

    suspend fun getTrackedCitiesCount(): Int {
        return useCase.getTrackedCitiesCount()
    }

    fun getInvisibleTextViewId(): Int {
        return invisibleTextView
    }

    fun getVisibleTextViewId(): Int {
        return visibleTextView
    }

    fun switchTextViews() {
        val temp = visibleTextView
        visibleTextView = invisibleTextView
        invisibleTextView = temp
    }

    fun getSearchCities(): LiveData<List<City>> {
        return useCase.getCitiesList()
    }


}