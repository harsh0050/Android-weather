package com.weather.forecast.clearsky.mainscreen.ui

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.weather.forecast.clearsky.R
import com.weather.forecast.clearsky.databinding.ActivityMainBinding
import com.weather.forecast.clearsky.interfaces.IWeatherImageCallback
import com.weather.forecast.clearsky.mainscreen.viewmodel.MainViewModel
import com.weather.forecast.clearsky.network.ResultData
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<MainViewModel>()
    private lateinit var binding: ActivityMainBinding
    private var isInternetConnected = false

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.progressBar.visibility = View.GONE

        val connectivityManager =
            getSystemService(ConnectivityManager::class.java) as ConnectivityManager

        val adapter = ArrayAdapter(
            applicationContext,
            android.R.layout.simple_dropdown_item_1line,
            ArrayList<String>()
        )
        binding.locationEditText.setAdapter(adapter)
        binding.enableSuggestionSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                viewModel.enableCitiesSuggestion(applicationContext, adapter)
            } else {
                viewModel.disableCitiesSuggestion(adapter)
            }
        }


        binding.searchButton.setOnClickListener {
            Log.i("Harsh", "Clicked");
            val inputLocation = binding.locationEditText.text.toString()

            if (connectivityManager.activeNetwork != null) {
                if (inputLocation.isNotBlank()) {
                    binding.progressBar.visibility = View.VISIBLE
                    viewModel.checkLocation(inputLocation) { loc ->
                        setWeatherData(loc)
                    }
                }else{
                    Toast.makeText(applicationContext, "Type Something.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(applicationContext, "Connect to Internet", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    }

    private fun setWeatherData(inputLocation: String) {
        viewModel.getWeatherData(inputLocation).observe(this) {
            when (it) {
                is ResultData.Success -> {
                    val tempC = (it.data?.current?.temp_c?.toString()?.split(" ")
                        ?.get(0)
                        ?.toFloat()
                        ?.toInt() ?: 0)

                    val tempF = (it.data?.current?.temp_f?.toString()?.split(" ")
                        ?.get(0)
                        ?.toFloat()
                        ?.toInt() ?: 0)
                    val weatherText = it.data?.current?.condition?.text.toString()//cloudy
                    val weatherIcon = "https:" + it.data?.current?.condition?.icon
                    val location =
                        it.data?.location?.name + ", " + it.data?.location?.region + ", " + it.data?.location?.country

                    binding.degreesTextView.text = "Temperature: $tempC °C / $tempF F"
                    binding.weatherText.text = "($weatherText)"
                    binding.showingResultTextView.text = "Showing Results for $location"

                    println(weatherIcon)
                    Glide.with(applicationContext).load(weatherIcon).centerCrop()
                        .into(binding.weatherIcon)

                    binding.progressBar.visibility = View.GONE
                    generateImage(weatherText)
                }

                is ResultData.Failed -> {
                    Log.d("TAG", "onCreate: failed ${it.message}")
                    Toast.makeText(
                        applicationContext,
                        "Failed to get the weather.",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    binding.progressBar.visibility = View.GONE
                }

                is ResultData.Loading -> {
                    Log.d("TAG", "onCreate: Loading")
                }
            }
        }
    }

    private fun generateImage(imgPrompt: String) {
        binding.progressBar.visibility = View.VISIBLE
        viewModel.getImage(formatText(imgPrompt), object : IWeatherImageCallback {
            override fun onSuccess(url: String) {
                Glide.with(applicationContext).load(url).centerCrop()
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>,
                            isFirstResource: Boolean,
                        ): Boolean {
                            binding.weatherImageView.setImageResource(R.drawable.cat)
                            binding.progressBar.visibility = View.GONE
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable,
                            model: Any,
                            target: Target<Drawable>?,
                            dataSource: DataSource,
                            isFirstResource: Boolean,
                        ): Boolean {
                            binding.progressBar.visibility = View.GONE
                            return false
                        }
                    }).into(binding.weatherImageView)
            }

            override fun onFailure(e: Throwable) {
                binding.weatherImageView.setImageResource(R.drawable.cat)
                Log.i("Volley", e.toString())
                binding.progressBar.visibility = View.GONE

            }
        })
    }

    private fun formatText(text: String): String {
        var ans = ""
        for (ch in text.toCharArray()) {
            if (ch == ' ') {
                ans += "%20"
            } else {
                ans += ch
            }
        }
        return ans
    }

}

