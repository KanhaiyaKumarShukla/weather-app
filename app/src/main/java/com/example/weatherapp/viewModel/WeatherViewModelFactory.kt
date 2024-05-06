package com.example.weatherapp.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.Repository.WeatherRepository

class WeatherViewModelFactory(private val repository: WeatherRepository, private val lat:Double,private val lon:Double):ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return WeatherViewModel(repository, lat, lon) as T
    }
}