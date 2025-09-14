package com.example.weather_app.ui.weather

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weather_app.R
import com.example.weather_app.databinding.FragmentWeatherHistoryBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WeatherHistoryFragment : Fragment(R.layout.fragment_weather_history) {

    private lateinit var binding: FragmentWeatherHistoryBinding
    private lateinit var adapter: WeatherHistoryAdapter

    private val viewModel: WeatherViewModel by viewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentWeatherHistoryBinding.bind(view)

        adapter = WeatherHistoryAdapter()
        binding.rvWeather.adapter = adapter
        binding.rvWeather.layoutManager = LinearLayoutManager(requireContext())

        viewModel.history.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
        }

        viewModel.loadHistory()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadHistory()
    }
}