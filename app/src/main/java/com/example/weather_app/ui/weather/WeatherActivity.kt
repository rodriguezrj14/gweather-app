package com.example.weather_app.ui.weather

import android.os.Bundle
import com.example.weather_app.R
import androidx.appcompat.app.AppCompatActivity
import com.example.weather_app.databinding.ActivityWeatherBinding
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WeatherActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWeatherBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeatherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = WeatherPagerAdapter(this)
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = if (position == 0) getString(R.string.tab_current_weather) else getString(R.string.tab_weather_history)
        }.attach()
    }
}