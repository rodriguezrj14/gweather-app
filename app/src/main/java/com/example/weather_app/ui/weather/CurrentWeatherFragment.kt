package com.example.weather_app.ui.weather

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.weather_app.R
import com.example.weather_app.databinding.FragmentCurrentWeatherBinding
import com.example.weather_app.utils.LocationUtils
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import androidx.core.view.isVisible

@AndroidEntryPoint
class CurrentWeatherFragment : Fragment(R.layout.fragment_current_weather) {

    private lateinit var binding: FragmentCurrentWeatherBinding
    private val viewModel: WeatherViewModel by viewModels()
    private var errorDialog: AlertDialog? = null
    private var locationPermissionDialog: AlertDialog? = null
    private var locationSettingsDialog: AlertDialog? = null
    private var permissionDenied = false

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                permissionDenied = false
                fetchWeatherForCurrentLocation()
            } else {
                permissionDenied = true
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCurrentWeatherBinding.bind(view)

        checkLocationPermission()
        observeWeather()
    }

    fun checkLocationPermission() {
        showLoading(true)
        if (LocationUtils.hasLocationPermission(requireContext())) {
            fetchWeatherForCurrentLocation()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun fetchWeatherForCurrentLocation() {
        showLoading(true)

        if (!LocationUtils.isLocationEnabled(requireContext())) {
            showEnableLocationDialog()
            return
        }

        LocationUtils.fetchCurrentLocation(this) { location ->
            location?.let {
                viewModel.fetchWeather(it.latitude, it.longitude)
            } ?: run {
                showLoading(false)
                binding.weatherCard.visibility = View.GONE
                showErrorDialog(getString(R.string.location_unavailable))
                return@fetchCurrentLocation
            }
        }
    }


    private fun showPermissionDeniedDialog() {
        if (locationPermissionDialog?.isShowing == true) {
            return
        }

        locationPermissionDialog = AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.location_permission_title))
            .setMessage(getString(R.string.location_permission_message))
            .setPositiveButton(getString(R.string.open_settings)) { _, _ ->
                openAppSettings()
            }
            .setCancelable(false)
            .show()
    }
    private fun openAppSettings() {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", requireContext().packageName, null)
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private fun showEnableLocationDialog() {
        if (locationSettingsDialog?.isShowing == true) return

        locationSettingsDialog = AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.enable_location_title))
            .setMessage(getString(R.string.enable_location_message))
            .setPositiveButton(getString(R.string.open_settings)) { _, _ ->
                openLocationSettings()
            }
            .setCancelable(false)
            .show()
    }

    private fun openLocationSettings() {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private fun observeWeather() {
        viewModel.weather.observe(viewLifecycleOwner) { weather ->
            showLoading(false)

            binding.apply {
                tvCity.text = getString(R.string.city_country, weather.name, weather.sys.country)
                tvTemp.text = getString(R.string.temperature, weather.main.temp)
                tvSunrise.text = getString(R.string.sunrise, convertUnixToTime(weather.sys.sunrise))
                tvSunset.text = getString(R.string.sunset, convertUnixToTime(weather.sys.sunset))
            }

            val iconCode = weather.weather.firstOrNull()?.icon ?: "01d"
            val isClearSky = weather.weather.firstOrNull()?.id == 800
            val finalIconCode = if (isClearSky && isAfter6PM()) "01n" else iconCode

            val iconUrl = "https://openweathermap.org/img/wn/$finalIconCode.png"
            Glide.with(this)
                .load(iconUrl)
                .into(binding.ivWeatherIcon)
        }

        viewModel.error.observe(viewLifecycleOwner) { message ->
            binding.progressBar.visibility = View.GONE
            binding.weatherCard.visibility = View.GONE

            message?.let {
                showErrorDialog(it)
                viewModel.clearError()
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
            binding.weatherCard.visibility = View.GONE
        } else {
            binding.progressBar.visibility = View.GONE
            binding.weatherCard.visibility = View.VISIBLE
        }
    }


    private fun showErrorDialog(message: String) {
        if (errorDialog?.isShowing == true) return

        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.error_title))
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton(getString(R.string.retry)) { dialog, _ ->
                dialog.dismiss()
                errorDialog = null
                fetchWeatherForCurrentLocation()
            }
            .show()
    }

    private fun convertUnixToTime(time: Long): String {
        val date = Date(time * 1000)
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return sdf.format(date)
    }

    private fun isAfter6PM(): Boolean {
        val calendar = Calendar.getInstance()
        return calendar.get(Calendar.HOUR_OF_DAY) >= 18
    }

    override fun onResume() {
        super.onResume()

        when {
            LocationUtils.hasLocationPermission(requireContext()) && binding.progressBar.isVisible -> {
                fetchWeatherForCurrentLocation()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                showPermissionDeniedDialog()
            }
            else -> {
                if (permissionDenied) {
                    requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
            }
        }
    }
}
