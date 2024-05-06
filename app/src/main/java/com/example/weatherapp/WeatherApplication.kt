package com.example.weatherapp

import android.app.Application
import com.example.weatherapp.Database.WeatherDatabase
import com.example.weatherapp.NetworkRequest.RetrofitObject
import com.example.weatherapp.NetworkRequest.WeatherService
import com.example.weatherapp.Repository.WeatherRepository

class WeatherApplication:Application() {
    lateinit var weatherRepository: WeatherRepository
    override fun onCreate() {
        super.onCreate()
        initialise()
    }

    private fun initialise() {
        val weatherService=RetrofitObject.getInstance().create(WeatherService::class.java)
        val weatherDatabase=WeatherDatabase.getInstance(applicationContext)
        weatherRepository= WeatherRepository(weatherService, weatherDatabase, applicationContext)
    }
}