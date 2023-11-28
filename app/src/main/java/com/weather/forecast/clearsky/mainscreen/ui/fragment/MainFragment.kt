package com.weather.forecast.clearsky.mainscreen.ui.fragment

import android.animation.AnimatorInflater
import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.graphics.drawable.toDrawable
import androidx.dynamicanimation.animation.SpringAnimation
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
import com.weather.forecast.clearsky.network.ResultData
import com.weather.forecast.clearsky.utils.CustomOnTouchListener
import com.weather.forecast.clearsky.utils.OnRefreshCallback
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@OptIn(DelicateCoroutinesApi::class)
class MainFragment : Fragment(), OnRefreshCallback {
    private lateinit var binding: FragmentMainBinding
    private val viewModel by activityViewModels<MainViewModel>()
    private var currentPage = 0
    private lateinit var customOnTouchListener: CustomOnTouchListener
    @SuppressLint("ClickableViewAccessibility")
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

        customOnTouchListener = CustomOnTouchListener(
            requireContext(),
            binding.viewPager,
            binding.updatingText,
            binding.tabLayout,
            this
        )

        TabLayoutMediator(
            binding.tabLayout, binding.viewPager
        ) { tab, _ ->
            tab.view.isClickable = false
        }.attach()

        var trackedCities: List<TrackedCityWeather> = ArrayList()

        viewModel.getTrackedCitiesLiveData().observe(viewLifecycleOwner) {
            trackedCities = it
            if(trackedCities.isNotEmpty())
                binding.aqiBtn.visibility = View.VISIBLE
            else
                binding.aqiBtn.visibility = View.GONE
            viewPagerAdapter.setTrackedCities(it)
        }

        binding.viewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                val prevPosition = currentPage
                currentPage = position
                val currCity = trackedCities[position]
                binding.appbarTitle.text = currCity.name
                binding.aqiBtn.text = getString(R.string.aqi, currCity.airQuality.pm2_5.toInt())

                //TODO animate background change
                setUpImage(currCity.imageByteArray)

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
            //TODO check scrollY
            customOnTouchListener

        )
        binding.addBtn.setOnClickListener {
            findNavController().navigate(R.id.action_MainFragment_to_ManageCitiesFragment)
        }

        return binding.root
    }

    private fun setUpImage(imageByteArray: ByteArray) {
        GlobalScope.launch(Dispatchers.IO) {
            val imageBitmap = BitmapFactory.decodeByteArray(imageByteArray,0,imageByteArray.size)
            val imgDrawable = imageBitmap.toDrawable(resources)
            requireActivity().runOnUiThread {
                binding.root.background = imgDrawable
            }
        }
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

    override fun refresh(springAnimation: SpringAnimation) {
        viewModel.reloadAllCities().observe(viewLifecycleOwner){
            when(it){
                is ResultData.Failed -> {
                    binding.updatingText.text = getString(R.string.reload_failed)
                    springAnimation.start()
                    customOnTouchListener.isReloading = false
                    //TODO
                }
                ResultData.Loading -> {
                    binding.updatingText.text = getString(R.string.reload_in_progress)
                }
                is ResultData.Success -> {
                    binding.updatingText.text = getString(R.string.update_successful)
                    springAnimation.start()
                    customOnTouchListener.isReloading = false
                }
            }
        }
    }

}