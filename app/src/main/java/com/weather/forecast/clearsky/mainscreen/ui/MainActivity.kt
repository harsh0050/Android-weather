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
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
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
    private lateinit var connectivityManager:ConnectivityManager
    private lateinit var locationLauncher : ActivityResultLauncher<Array<String>>

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //initialization
        binding.progressBar.visibility = View.GONE

        connectivityManager =
            getSystemService(ConnectivityManager::class.java) as ConnectivityManager

        val adapter = ArrayAdapter(
            applicationContext,
            android.R.layout.simple_dropdown_item_1line,
            ArrayList<String>()
        )
        binding.locationEditText.setAdapter(adapter)
        locationLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { result ->
                var isGranted = true
                for (granted in result.values) {
                    if (!granted) {
                        isGranted = false
                        break
                    }
                }
                println(result)
                if (isGranted) {
                    val service =
                        LocationServices.getFusedLocationProviderClient(this@MainActivity)
                    service.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                        .addOnSuccessListener { location ->
                            if(location!=null)
                                setWeatherData("${location.latitude},${location.longitude}", true)
                            else
                                Toast.makeText(
                                    applicationContext,
                                    "Something went wrong while getting location.",
                                    Toast.LENGTH_SHORT
                                ).show()
                        }.addOnFailureListener {
                            Toast.makeText(
                                applicationContext,
                                "${it.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }
            }


        //listeners
        binding.enableSuggestionSwitch.setOnCheckedChangeListener { _, isChecked ->
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
                        binding.progressBar.visibility = View.GONE
                        setWeatherData(loc, false)
                    }
                } else {
                    Toast.makeText(applicationContext, "Type Something.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(applicationContext, "Connect to Internet", Toast.LENGTH_SHORT)
                    .show()
            }
        }


        binding.currentLocationButton.setOnClickListener {
            getCurrentLocationWeather()
        }
    }

    private fun getCurrentLocationWeather(){
        val locationRequest = LocationRequest
            .Builder(Priority.PRIORITY_LOW_POWER, 10000)
            .build()
        val settings =
            LocationSettingsRequest.Builder().addLocationRequest(locationRequest).build()
        val task = LocationServices.getSettingsClient(this).checkLocationSettings(settings)

        task.addOnFailureListener {
            println("failure: ${it.localizedMessage}")
            if (it is ResolvableApiException) {
                it.startResolutionForResult(this, 5)
            }
        }.addOnSuccessListener {
            if (connectivityManager.activeNetwork != null)
                locationLauncher.launch(REQUIRED_PERMISSIONS)
            else
                Toast.makeText(applicationContext, "Connect to Internet", Toast.LENGTH_SHORT)
                    .show()
        }
    }
    private fun setWeatherData(inputLocation: String, isCurrentLocation: Boolean) {
        binding.progressBar.visibility = View.VISIBLE
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


                    if (!isCurrentLocation) {
                        generateImage(weatherText)
                    } else {
                        setupImageIntoWeatherImageView(R.drawable.look_outside)
                    }
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
        viewModel.getImage(formatText("$imgPrompt weather"), object : IWeatherImageCallback {
            override fun onSuccess(url: String) {
                setupImageIntoWeatherImageView(url)
            }

            override fun onFailure(e: Throwable) {
                setupImageIntoWeatherImageView(R.drawable.cat)
                Log.i("Volley", e.toString())
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

    private fun setupImageIntoWeatherImageView(url: String) {
        Glide.with(applicationContext).load(url).centerCrop()
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>,
                    isFirstResource: Boolean,
                ): Boolean {
                    setupImageIntoWeatherImageView(R.drawable.cat)
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

    private fun setupImageIntoWeatherImageView(resId: Int) {
        Glide.with(applicationContext).load(resId)
            .addListener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>,
                    isFirstResource: Boolean,
                ): Boolean {
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
            })
            .into(binding.weatherImageView)
    }


    companion object {
        val REQUIRED_PERMISSIONS = arrayOf(
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        )
        const val GPS_REQUEST_CODE = 1
    }

}

