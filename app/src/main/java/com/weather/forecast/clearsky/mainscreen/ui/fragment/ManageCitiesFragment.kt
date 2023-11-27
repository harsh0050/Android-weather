package com.weather.forecast.clearsky.mainscreen.ui.fragment

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.weather.forecast.clearsky.R
import com.weather.forecast.clearsky.adapters.CustomOnClickListener
import com.weather.forecast.clearsky.adapters.ManageCitiesTrackedCitiesAdapter
import com.weather.forecast.clearsky.adapters.SearchListAdapter
import com.weather.forecast.clearsky.databinding.FragmentManageCitiesBinding
import com.weather.forecast.clearsky.mainscreen.viewmodel.MainViewModel
import com.weather.forecast.clearsky.model.City
import com.weather.forecast.clearsky.model.TrackedCityWeather
import com.weather.forecast.clearsky.model.WeatherModel
import com.weather.forecast.clearsky.network.ResultData
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

@OptIn(DelicateCoroutinesApi::class)
class ManageCitiesFragment : Fragment(), CustomOnClickListener {
    private lateinit var binding: FragmentManageCitiesBinding
    private lateinit var trackedCitiesAdapter: ManageCitiesTrackedCitiesAdapter
    private lateinit var searchAdapter: SearchListAdapter
    private val viewModel by activityViewModels<MainViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentManageCitiesBinding.inflate(inflater)

        binding.upBtn.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.searchFilterRecyclerView.visibility = View.GONE
        binding.noResultFound.visibility = View.GONE

        trackedCitiesAdapter = ManageCitiesTrackedCitiesAdapter()
        searchAdapter = SearchListAdapter(binding.noResultFound, this)

        binding.trackedCitiesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.trackedCitiesRecyclerView.adapter = trackedCitiesAdapter

        binding.searchFilterRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.searchFilterRecyclerView.adapter = searchAdapter


        viewModel.getTrackedCities().observe(viewLifecycleOwner) {
            trackedCitiesAdapter.setTrackedCities(it)
        }

        binding.searchBar.setOnQueryTextFocusChangeListener { _, currentlyFocused ->
            if (currentlyFocused) {
//                animator.reverse()
                binding.cancelButton.visibility = View.VISIBLE
                binding.trackedCitiesRecyclerView.visibility = View.GONE
                binding.searchFilterRecyclerView.visibility = View.VISIBLE
            }
        }

        binding.cancelButton.setOnClickListener {
            binding.searchBar.setQuery("", false)
            binding.searchBar.clearFocus()
            binding.trackedCitiesRecyclerView.visibility = View.VISIBLE
            binding.searchFilterRecyclerView.visibility = View.GONE
            binding.noResultFound.visibility = View.GONE
            binding.cancelButton.visibility = View.GONE
        }
        binding.cancelButton.visibility = View.GONE

        viewModel.getSearchCities().observe(viewLifecycleOwner) { allCities ->
            binding.searchBar.query.let {
                if (it.isNotEmpty())
                    filterList(allCities, it.toString())
            }
            binding.searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    //nothing
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    filterList(allCities, newText)
                    return true
                }
            })
        }
        return binding.root
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun filterList(list: List<City>, text: String?) {
        val filteredList: MutableList<City> = ArrayList()
        if (text == null || text.length < 3) {
            searchAdapter.updateFilteredList(filteredList)
            return
        }
        GlobalScope.launch {
            list.forEach {
                if (checkString(it.city.lowercase(), text)) {
                    filteredList.add(it)
                }
            }
            activity?.runOnUiThread {
                searchAdapter.updateFilteredList(filteredList)
            }
        }
    }

    private fun checkString(cityName: String, text: String): Boolean {
        if (cityName.startsWith(text)) {
            return true
        }
        cityName.split(" ").forEach {
            if (it.startsWith(text)) {
                return true
            }
        }
        return false
    }

    override fun onClick(location: City, holder: SearchListAdapter.CustomViewHolder) {
        GlobalScope.launch(Dispatchers.IO) {
            val count = viewModel.getTrackedCitiesCount()
            requireActivity().runOnUiThread{
                if(count>=10){
                    Toast.makeText(context, "Remove a city to add.", Toast.LENGTH_SHORT).show()
                }else{
                    searchGetAndSetWeatherData(location,holder)
                }
            }
        }
    }

    private fun searchGetAndSetWeatherData(
        location: City,
        holder: SearchListAdapter.CustomViewHolder
    ) {
        viewModel.getCityId(location.city).observe(this){
            when(it){
                ResultData.Loading -> {
                    holder.statusText.text = "Adding..."
                    holder.addButton.visibility = View.GONE
                    holder.statusText.visibility = View.VISIBLE
                }
                is ResultData.Failed -> {
                    holder.addButton.visibility = View.VISIBLE
                    holder.statusText.visibility = View.GONE
                    holder.statusText.text = getString(R.string.added)
                    //TODO
                }
                is ResultData.Success -> {
                    val apiCityId = it.data
                    getAndSetWeatherData(apiCityId, location, holder)
                }
            }
        }
    }

    private fun getAndSetWeatherData(
        apiCityId: Int,
        location: City,
        holder: SearchListAdapter.CustomViewHolder
    ) {
        println("getAndSetWeatherData($apiCityId,location)")

        viewModel.getWeatherData(apiCityId).observe(this) {
            when (it) {
                is ResultData.Success -> {
                    val weather = it.data
                    generateImageUrl(apiCityId, weather, location, holder)
                }

                is ResultData.Failed -> {
                    holder.addButton.visibility = View.VISIBLE
                    holder.statusText.visibility = View.GONE
                    holder.statusText.text = getString(R.string.added)
                    //TODO
                }

                is ResultData.Loading -> {
                    //TODO
                }
            }
        }
    }

    private fun generateImageUrl(
        apiCityId: Int,
        weatherModel: WeatherModel,
        location: City,
        holder: SearchListAdapter.CustomViewHolder
    ) {
        val city = weatherModel.location.name
        val condition = weatherModel.current.condition.text
        viewModel.getImage(formatText(city), formatText(condition)).observe(this) {
            when (it) {
                is ResultData.Success -> {
                    val imgUrl = it.data.url
                    downloadImageAndTrackCity(apiCityId, weatherModel, imgUrl, location,holder)
                }

                is ResultData.Failed -> {
                    holder.addButton.visibility = View.VISIBLE
                    holder.statusText.visibility = View.GONE
                    holder.statusText.text = getString(R.string.added)
                }

                is ResultData.Loading -> {
                    //TODO
                }
            }
        }
    }

    private fun downloadImageAndTrackCity(
        apiCityId: Int,
        weatherModel: WeatherModel,
        urlString: String,
        location: City,
        holder: SearchListAdapter.CustomViewHolder,
    ) {
        Glide.with(this).asBitmap().load(urlString).listener(object : RequestListener<Bitmap>{
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Bitmap>,
                isFirstResource: Boolean,
            ): Boolean {
                holder.addButton.visibility = View.VISIBLE
                holder.statusText.visibility = View.GONE
                holder.statusText.text = getString(R.string.added)
                //TODO

                return true
            }

            override fun onResourceReady(
                bitmap: Bitmap,
                model: Any,
                target: Target<Bitmap>?,
                dataSource: DataSource,
                isFirstResource: Boolean,
            ): Boolean {
                getDarkerByteArray(bitmap)
                val city = TrackedCityWeather.newInstance(
                    apiCityId,
                    weatherModel,
                    getDarkerByteArray(bitmap)
                )
                holder.statusText.text = getString(R.string.added)
                viewModel.trackCity(city, location.id)

                return true
            }
        }).into(object: CustomTarget<Bitmap>(){
            override fun onResourceReady(bitmap: Bitmap, transition: Transition<in Bitmap>?) {}
            override fun onLoadCleared(placeholder: Drawable?) {}
        })
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