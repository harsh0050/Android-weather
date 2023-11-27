package com.weather.forecast.clearsky.mainscreen.ui.fragment

import android.animation.AnimatorInflater
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.weather.forecast.clearsky.R
import com.weather.forecast.clearsky.adapters.MainViewPagerAdapter
import com.weather.forecast.clearsky.databinding.FragmentMainBinding
import com.weather.forecast.clearsky.mainscreen.viewmodel.MainViewModel
import com.weather.forecast.clearsky.model.TrackedCityWeather
import com.weather.forecast.clearsky.utils.CustomOnTouchListener

class MainFragment : Fragment() {
    private lateinit var binding: FragmentMainBinding
    private val viewModel by activityViewModels<MainViewModel>()
    private var currentPage = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentMainBinding.inflate(inflater)
        val viewPagerAdapter = MainViewPagerAdapter(childFragmentManager, lifecycle)
        binding.aqiBtn.visibility = View.GONE

        binding.viewPager.adapter = viewPagerAdapter
        binding.viewPager.setCurrentItem(currentPage, true)

        var trackedCities: List<TrackedCityWeather> = ArrayList()
        viewModel.getTrackedCities().observe(viewLifecycleOwner) {
            trackedCities = it
            if(trackedCities.isNotEmpty())
                binding.aqiBtn.visibility = View.VISIBLE
            else
                binding.aqiBtn.visibility = View.GONE
            viewPagerAdapter.setTrackedCities(it)
        }

        TabLayoutMediator(
            binding.tabLayout, binding.viewPager
        ) { _, _ ->
        }.attach()


        binding.viewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                println("onPageSelected() $position")
                val prevPosition = currentPage
                currentPage = position
//                println(position)
                val currCity = trackedCities[position]
                binding.appbarTitle.text = currCity.name
                binding.aqiBtn.text = getString(R.string.aqi, currCity.airQuality.pm2_5.toInt())

                //TODO animate background change
                val imageBitmap = BitmapFactory.decodeByteArray(currCity.imageByteArray,0,currCity.imageByteArray.size)
                binding.root.background = imageBitmap.toDrawable(resources)

                changeTempText(prevPosition, position, currCity.temp.toInt())
                binding.conditionTextView.text = getString(
                    R.string.condition,
                    currCity.condition.text,
                    currCity.maxTemp.toInt(),
                    currCity.minTemp.toInt()
                )
            }
        })
        binding.swipableForeground.setOnTouchListener(
            CustomOnTouchListener(
                requireContext(),
                binding.viewPager
            )
        )
        binding.addBtn.setOnClickListener {
            findNavController().navigate(R.id.action_MainFragment_to_ManageCitiesFragment)
        }
        return binding.root
    }

    fun changeTempText(oldPosition: Int, newPosition: Int, newTemp: Int) {
        val invisibleTextView: TextView =
            requireView().findViewById(viewModel.getInvisibleTextViewId())
        val visibleTextView: TextView = requireView().findViewById(viewModel.getVisibleTextViewId())


        if (newPosition-oldPosition==1) {
            invisibleTextView.text = getString(R.string.degrees, newTemp)
            val disappearAnim =
                AnimatorInflater.loadAnimator(requireContext(), R.animator.left_view_disappear)
                    .apply {
                        setTarget(visibleTextView)
                    }
            val appearAnim =
                AnimatorInflater.loadAnimator(requireContext(), R.animator.right_view_appear)
                    .apply {
                        setTarget(invisibleTextView)
                        startDelay = 200
                    }
            disappearAnim.start()
            appearAnim.start()
        } else if(oldPosition-newPosition==1){
            invisibleTextView.text = getString(R.string.degrees, newTemp)
            val disappearAnim =
                AnimatorInflater.loadAnimator(requireContext(), R.animator.right_view_disappear)
                    .apply {
                        setTarget(visibleTextView)
                    }
            val appearAnim =
                AnimatorInflater.loadAnimator(requireContext(), R.animator.left_view_appear)
                    .apply {
                        setTarget(invisibleTextView)
                    }
            disappearAnim.start()
            appearAnim.start()
        }else{
            visibleTextView.text = getString(R.string.degrees, newTemp)
            return
        }
        viewModel.switchTextViews()

    }

}