package com.weather.forecast.clearsky.mainscreen.ui.activity

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
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
import com.weather.forecast.clearsky.databinding.ActivityMainWtpagerBinding
import com.weather.forecast.clearsky.mainscreen.viewmodel.MainViewModel
import com.weather.forecast.clearsky.network.ResultData
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.DelicateCoroutinesApi

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainViewModel>()
    private lateinit var binding: ActivityMainBinding
    private lateinit var newBinding: ActivityMainWtpagerBinding
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var locationPermissionLauncher: ActivityResultLauncher<Array<String>>

    @OptIn(DelicateCoroutinesApi::class)
    @SuppressLint("MissingPermission", "ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        newBinding = ActivityMainWtpagerBinding.inflate(layoutInflater)
//        setContentView(binding.root)
        setContentView(newBinding.root)

        binding.progressBar.visibility = View.GONE

        connectivityManager =
            getSystemService(ConnectivityManager::class.java) as ConnectivityManager

        val adapter = ArrayAdapter(
            applicationContext,
            android.R.layout.simple_dropdown_item_1line,
            ArrayList<String>()
        )
        binding.locationEditText.setAdapter(adapter)

//        initializeLocPermissionLauncher()


        binding.enableSuggestionSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.enableCitiesSuggestion(applicationContext, adapter)
            } else {
                viewModel.disableCitiesSuggestion(adapter)
            }
        }

        binding.searchButton.setOnClickListener {
            val inputLocation = binding.locationEditText.text.toString()

            if (connectivityManager.activeNetwork != null) {
                if (inputLocation.isNotBlank()) {
//                    checkGetAndSetWeatherData(inputLocation)
                } else {
                    Toast.makeText(applicationContext, "Type Something.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(applicationContext, "Connect to Internet", Toast.LENGTH_SHORT)
                    .show()
            }
        }

//        newBinding.addBtn.setOnClickListener {
//            onAddButtonPressed()
//        }


        binding.currentLocationButton.setOnClickListener {
            getAndSetCurrentLocationWeather()
        }
//        val viewPagerAdapter = MainViewPagerAdapter(supportFragmentManager, lifecycle)
//        newBinding.viewPager.adapter = viewPagerAdapter
//        newBinding.viewPager.setCurrentItem(viewModel.currentPage, true)
//
//        var trackedCities: List<TrackedCityWeather> = ArrayList()
//        viewModel.getTrackedCities().observe(this) {
//            trackedCities = it
//            viewPagerAdapter.setTrackedCities(it)
//        }
//
//        TabLayoutMediator(
//            newBinding.tabLayout, newBinding.viewPager
//        ) { _, _ ->
//        }.attach()
//
//        newBinding.viewPager.registerOnPageChangeCallback(object :
//            ViewPager2.OnPageChangeCallback() {
//
//            override fun onPageSelected(position: Int) {
//                super.onPageSelected(position)
//                val prevPosition = viewModel.currentPage
//                viewModel.currentPage = position
////                println(position)
//                val currCity = trackedCities[position]
//                newBinding.appbarTitle.text = currCity.name
//                newBinding.aqiBtn.text = getString(R.string.aqi, currCity.airQuality.pm2_5.toInt())
//
//                Log.i(TAG, "onPageSelected: $position")
//                changeTempText(prevPosition, position, currCity.temp.toInt())
////                newBinding.firstTempTextView.text =
////                    getString(R.string.degrees, currCity.temp.toInt())
//                newBinding.conditionTextView.text = getString(
//                    R.string.condition,
//                    currCity.condition.text,
//                    currCity.maxTemp.toInt(),
//                    currCity.minTemp.toInt()
//                )
//
//
//            }
//        })
//        newBinding.swipableForeground.setOnTouchListener(
//            CustomOnTouchListener(
//                applicationContext,
//                newBinding.viewPager
//            )
//        )
    }

//    fun changeTempText(oldPosition: Int, newPosition: Int, newTemp: Int) {
//        val invisibleTextView: TextView = findViewById(viewModel.getInvisibleTextViewId())
//        val visibleTextView: TextView = findViewById(viewModel.getVisibleTextViewId())
//        if(oldPosition==newPosition){
//            visibleTextView.text = getString(R.string.degrees, newTemp)
//            return
//        }
//
//        invisibleTextView.text = getString(R.string.degrees, newTemp)
//
//        if (oldPosition < newPosition) {
//            val disappearAnim =
//                AnimatorInflater.loadAnimator(applicationContext, R.animator.left_view_disappear)
//                    .apply {
//                        setTarget(visibleTextView)
//                    }
//            val appearAnim =
//                AnimatorInflater.loadAnimator(applicationContext, R.animator.right_view_appear)
//                    .apply {
//                        setTarget(invisibleTextView)
//                        startDelay = 200
//                    }
//            disappearAnim.start()
//            appearAnim.start()
//        } else {
//            val disappearAnim =
//                AnimatorInflater.loadAnimator(applicationContext, R.animator.right_view_disappear)
//                    .apply {
//                        setTarget(visibleTextView)
//                    }
//            val appearAnim =
//                AnimatorInflater.loadAnimator(applicationContext, R.animator.left_view_appear)
//                    .apply {
//                        setTarget(invisibleTextView)
//                    }
//            disappearAnim.start()
//            appearAnim.start()
//        }
//        viewModel.switchTextViews()
//
//    }

//    private fun onAddButtonPressed() {
//        GlobalScope.launch {
//            val count = viewModel.getTrackedCitiesCount()
//            val inputStream =
//                URL("https://buffer.com/library/content/images/size/w1000/2023/10/free-images-for-commercial-use--20-.png").content as InputStream
//            val map = BitmapFactory.decodeStream(inputStream)
//            runOnUiThread {
//                if (count < 10) {
//                    viewModel.getWeatherData("new delhi").observe(this@MainActivity) {
//                        if (it is ResultData.Success && it.data != null) {
////
////                                runOnUiThread {
////                                    newBinding.root.background = BitmapDrawable(resources, map)
////                                }
//                            val city = TrackedCityWeather.newInstance(it.data, map)
//                            viewModel.trackCity(city)
//                        }
//                    }
//                } else {
//                    Toast.makeText(applicationContext, "Remove a city", Toast.LENGTH_SHORT)
//                        .show()
//                }
//            }
//        }
//    }

//    @SuppressLint("MissingPermission")
//    private fun initializeLocPermissionLauncher() {
//        locationPermissionLauncher =
//            registerForActivityResult(
//                ActivityResultContracts.RequestMultiplePermissions()
//            ) { result ->
//                var isGranted = true
//                for (granted in result.values) {
//                    if (!granted) {
//                        isGranted = false
//                        break
//                    }
//                }
//                println(result)
//                if (isGranted) {
//                    val service =
//                        LocationServices.getFusedLocationProviderClient(this@MainActivity)
//                    service.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
//                        .addOnSuccessListener { location ->
//                            if (location != null)
//                                getAndSetWeatherData(
//                                    "${location.latitude},${location.longitude}",
//                                    true
//                                )
//                            else
//                                Toast.makeText(
//                                    applicationContext,
//                                    "Something went wrong while getting location.",
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                        }.addOnFailureListener {
//                            Toast.makeText(
//                                applicationContext,
//                                "${it.message}",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        }
//                }
//            }
//    }

//    private fun checkGetAndSetWeatherData(inputLocation: String) {
//        viewModel.correctLocation(inputLocation).observe(this) {
//            when (it) {
//                is ResultData.Failed -> {
//                    binding.progressBar.visibility = View.GONE
//                    getAndSetWeatherData(inputLocation, false)
//                }
//
//                is ResultData.Loading -> {
//                    binding.progressBar.visibility = View.VISIBLE
//                }
//
//                is ResultData.Success -> {
//                    binding.progressBar.visibility = View.GONE
//                    getAndSetWeatherData(it.data!!.location, false)
//                }
//            }
//        }
//    }

    private fun getAndSetCurrentLocationWeather() {
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
                locationPermissionLauncher.launch(REQUIRED_PERMISSIONS)
            else
                Toast.makeText(applicationContext, "Connect to Internet", Toast.LENGTH_SHORT)
                    .show()
        }
    }

//    private fun getAndSetWeatherData(location: String, isCurrentLocation: Boolean) {
//        viewModel.getWeatherData(location).observe(this) {
//            when (it) {
//                is ResultData.Success -> {
//                    val tempC = (it.data?.current?.temp_c?.toString()?.split(" ")
//                        ?.get(0)
//                        ?.toFloat()
//                        ?.toInt() ?: 0)
//
//                    val tempF = (it.data?.current?.temp_f?.toString()?.split(" ")
//                        ?.get(0)
//                        ?.toFloat()
//                        ?.toInt() ?: 0)
//                    val condition = it.data?.current?.condition?.text.toString()//cloudy
//                    val weatherIcon = "https:" + it.data?.current?.condition?.icon
//
//                    val city = it.data?.location?.name.orEmpty()
//                    val region = it.data?.location?.region.orEmpty()
//                    val country = it.data?.location?.country.orEmpty()
//
//                    var displayLocation = ""
//                    if (city.isNotEmpty()) {
//                        displayLocation += "$city, "
//                    }
//                    if (region.isNotEmpty()) {
//                        displayLocation += "$region, "
//                    }
//                    if (country.isNotEmpty()) {
//                        displayLocation += country
//                    }
//
//
//                    binding.degreesTextView.text = getString(R.string.temperature, tempC, tempF)
//                    binding.weatherText.text = getString(R.string.weather_text, condition)
//                    binding.showingResultTextView.text =
//                        getString(R.string.showing_results_for, displayLocation)
//
//                    println(weatherIcon)
//                    Glide.with(applicationContext).load(weatherIcon).centerCrop()
//                        .into(binding.weatherIcon)
//
//                    binding.progressBar.visibility = View.GONE
//                    if (!isCurrentLocation) {
//                        getGeneratedImage(city, condition)
//                    } else {
//                        setImageIntoWeatherImageView(R.drawable.look_outside)
//                    }
//                }
//
//                is ResultData.Failed -> {
//                    Log.d("TAG", "onCreate: failed ${it.message}")
//                    Toast.makeText(
//                        applicationContext,
//                        "Failed to get the weather.",
//                        Toast.LENGTH_SHORT
//                    )
//                        .show()
//                    binding.progressBar.visibility = View.GONE
//                }
//
//                is ResultData.Loading -> {
//                    binding.progressBar.visibility = View.VISIBLE
//                    Log.d("TAG", "onCreate: Loading")
//                }
//            }
//        }
//    }

    private fun getGeneratedImage(city: String, condition: String) {
        viewModel.getImage(formatText(city), formatText(condition)).observe(this) {
            when (it) {
                is ResultData.Failed -> {
                    binding.progressBar.visibility = View.GONE
                    setImageIntoWeatherImageView(R.drawable.cat)
                }

                is ResultData.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }

                is ResultData.Success -> {
                    binding.progressBar.visibility = View.GONE
                    setImageIntoWeatherImageView(it.data!!.url)
                }
            }
        }
    }

    private fun setImageIntoWeatherImageView(url: String) {
        binding.progressBar.visibility = View.VISIBLE
        Glide.with(applicationContext).load(url).centerCrop()
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>,
                    isFirstResource: Boolean,
                ): Boolean {
                    binding.progressBar.visibility = View.GONE
                    Log.i("debug", "onLoadFailed: ${e?.causes}")
                    setImageIntoWeatherImageView(R.drawable.cat)
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

    private fun setImageIntoWeatherImageView(resId: Int) {
        binding.progressBar.visibility = View.VISIBLE
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

    companion object {
        val REQUIRED_PERMISSIONS = arrayOf(
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        )
        const val TAG = "MainActivity"
    }


}

