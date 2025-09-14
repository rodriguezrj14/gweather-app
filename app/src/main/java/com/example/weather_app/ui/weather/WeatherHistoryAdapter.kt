package com.example.weather_app.ui.weather

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weather_app.R
import com.example.weather_app.databinding.ItemWeatherHistoryBinding
import com.example.weather_app.domain.model.WeatherEntry
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class WeatherHistoryAdapter :
    ListAdapter<WeatherEntry, WeatherHistoryAdapter.WeatherViewHolder>(DiffCallback) {

    object DiffCallback : DiffUtil.ItemCallback<WeatherEntry>() {
        override fun areItemsTheSame(oldItem: WeatherEntry, newItem: WeatherEntry): Boolean {
            return oldItem.timestamp == newItem.timestamp
        }

        override fun areContentsTheSame(oldItem: WeatherEntry, newItem: WeatherEntry): Boolean {
            return oldItem == newItem
        }
    }

    inner class WeatherViewHolder(val binding: ItemWeatherHistoryBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        val binding = ItemWeatherHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WeatherViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        val item = getItem(position)

        holder.binding.tvCity.text = holder.itemView.context.getString(
            R.string.city_country,
            item.city,
            item.country
        )

        holder.binding.tvTemp.text = holder.itemView.context.getString(
            R.string.temperature,
            item.temp
        )

        holder.binding.tvDescription.text = item.description

        val date = Date(item.timestamp)
        val formatter = SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault())
        holder.binding.tvDateTime.text = formatter.format(date)

        val isClearSky = item.icon == "01d"
        val calendar = Calendar.getInstance().apply { time = date }
        val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)

        val finalIconCode = if (isClearSky) {
            if (hourOfDay >= 18 || hourOfDay < 6) "01n" else "01d"
        } else {
            item.icon
        }

        Glide.with(holder.itemView.context)
            .load("https://openweathermap.org/img/wn/$finalIconCode@2x.png")
            .placeholder(R.drawable.ic_broken_img)
            .into(holder.binding.imgWeatherIcon)
    }
}