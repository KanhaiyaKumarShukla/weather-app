package com.example.weatherapp.NetworkRequest

import com.example.weatherapp.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitObject {
    companion object{
        fun getInstance(): Retrofit {

            return Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
    }
}