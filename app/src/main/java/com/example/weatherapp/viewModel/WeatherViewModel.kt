package com.example.weatherapp.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.Models.WeatherResponse
import com.example.weatherapp.Repository.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WeatherViewModel(private val repository: WeatherRepository, lat:Double, lon:Double) :ViewModel(){
    init{
        viewModelScope.launch(Dispatchers.IO){
            repository.getWeatherResponse(lat, lon)
        }
    }
    val weatherResponse: LiveData<WeatherResponse?>
        get()=repository.weather
}