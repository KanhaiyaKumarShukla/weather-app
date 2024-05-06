package com.example.weatherapp.Repository

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.weatherapp.Constants
import com.example.weatherapp.Database.WeatherDatabase
import com.example.weatherapp.Models.WeatherResponse
import com.example.weatherapp.NetworkRequest.WeatherService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WeatherRepository(private val weatherService: WeatherService, private val weatherDatabase: WeatherDatabase, private val context:Context) {
    private var _weather= MutableLiveData<WeatherResponse?>()
    val weather: LiveData<WeatherResponse?>
        get() = _weather

    suspend fun getWeatherResponse(lat:Double, lon:Double){
        if(Constants.isInternetAvailable(context)){
            val result=weatherService.getWeather(lat, lon, Constants.METRIC_UNIT, Constants.APP_ID)
            result.enqueue(object : Callback<WeatherResponse> {
                override fun onResponse(
                    call: Call<WeatherResponse>,
                    response: Response<WeatherResponse>
                ) {
                    if (response.isSuccessful) {
                        val weatherList = response.body()
                        if(weatherList!=null) {
                            CoroutineScope(Dispatchers.IO).launch {
                                weatherDatabase.weatherDao().insert(weatherList)
                            }
                            _weather.value=weatherList
                        }
                        Log.i("Response Result: ", "$weatherList")
                    } else {
                        val message=when (response.code()) {
                            400 -> "Error 400 Bad Connection"
                            404 -> "Error 404 Not Found"
                            else -> "Error Generic Error"
                        }
                        Handler(Looper.getMainLooper()).post {
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                    Handler(Looper.getMainLooper()).post {
                        Toast.makeText(context, "Error found!", Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }else{
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(context, "You are offline!", Toast.LENGTH_SHORT).show()
            }
            val weatherResponse=weatherDatabase.weatherDao().getAll()
            _weather.postValue(weatherResponse)
        }
    }


}