package com.example.weatherapp

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.weatherapp.Models.WeatherResponse
import com.example.weatherapp.NetworkRequest.RetrofitObject
import com.example.weatherapp.NetworkRequest.WeatherService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.SimpleTimeZone
import java.util.TimeZone

class WeatherDetails(private val context: Activity) {

    private val progressLayout: LinearLayout
        get() = context.findViewById(R.id.loading_layout)

    private val mEmptyTextView: TextView
        get() = context.findViewById(R.id.empty_tv)

    private val WeatherLayout: LinearLayout
        get() = context.findViewById(R.id.weather_layout)

    private val mTvMain:TextView
        get() = context.findViewById(R.id.tv_main)

    private val mTvDescr:TextView
        get() = context.findViewById(R.id.tv_main_description)

    private val mTvTemp:TextView
        get() = context.findViewById(R.id.tv_temp)

    private val mTvHumidity:TextView
        get() = context.findViewById(R.id.tv_humidity)

    private val mTvMin:TextView
        get() = context.findViewById(R.id.tv_min)

    private val mTvMax:TextView
        get() = context.findViewById(R.id.tv_max)

    private val mTvSpeed:TextView
        get() = context.findViewById(R.id.tv_speed)

    private val mTvSpeedUnit:TextView
        get() = context.findViewById(R.id.tv_speed_unit)

    private val mTvCountry:TextView
        get() = context.findViewById(R.id.tv_country)

    private val mTvName:TextView
        get() = context.findViewById(R.id.tv_name)

    private val mTvSunrise:TextView
        get() = context.findViewById(R.id.tv_sunrise_time)

    private val mTvSunset:TextView
        get() = context.findViewById(R.id.tv_sunset_time)

    private val mIvMain:ImageView
        get() = context.findViewById(R.id.iv_main)

        fun getLocationWeatherDetails(latitude:Double, longitude:Double) {
            if (Constants.isInternetAvailable(context)) {
                val retrofit = RetrofitObject.getInstance()
                val service = retrofit.create(WeatherService::class.java)
                val listCall = service.getWeather(latitude, longitude, Constants.METRIC_UNIT, Constants.APP_ID)
                listCall.enqueue(object : Callback<WeatherResponse> {
                    override fun onResponse(
                        call: Call<WeatherResponse>,
                        response: Response<WeatherResponse>
                    ) {
                        progressLayout.visibility= View.GONE
                        if (response.isSuccessful) {
                            val weatherList = response.body()
                            if(weatherList!=null) {
                                mEmptyTextView.visibility = View.GONE
                                WeatherLayout.visibility=View.VISIBLE
                                setUI(weatherList)
                            }else{
                                mEmptyTextView.visibility=View.VISIBLE
                                mEmptyTextView.text="Weather detail not Found"
                            }
                            Log.i("Response Result: ", "$weatherList")
                        } else {
                            val rc = response.code()
                            mEmptyTextView.visibility=View.VISIBLE
                            mEmptyTextView.text=when (rc) {
                                400 -> "Error 400 Bad Connection"
                                404 -> "Error 404 Not Found"
                                else -> "Error Generic Error"
                            }
                        }
                    }

                    override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                        Log.e("Errrorrrrr", t.message.toString())
                        progressLayout.visibility= View.GONE
                        mEmptyTextView.visibility=View.VISIBLE
                        mEmptyTextView.text="Errorrrrr"
                    }
                })
            }else{
                progressLayout.visibility= View.GONE
                mEmptyTextView.visibility=View.VISIBLE
                mEmptyTextView.text="Internet Problem: Internet Not Found"
            }
        }

    private fun setUI(weatherList:WeatherResponse) {
        val weather=weatherList.weather[0]
        mTvMain.text=weather.main
        mTvDescr.text=weather.description
        val main=weatherList.main
        //context.application.resources.configuration.locales.toString() :- here we are accessing the configuration of app using resource and then locales which will give location code.
        mTvTemp.text=context.getString(R.string.format_detail, main.temp.toString(), getUnit(context.application.resources.configuration.locales[0].toString()))
        //Log.i("Locals", context.application.resources.configuration.locales[0].toString())
        mTvHumidity.text=context.getString(R.string.format_detail, main.humidity.toString(), "%")
        mTvMin.text=context.getString(R.string.format_detail,main.temp_min.toString(), "min")
        mTvMax.text=context.getString(R.string.format_detail,main.temp_min.toString(), "min")
        mTvSunrise.text=formatTime(weatherList.sys.sunrise)
        mTvSunset.text=formatTime(weatherList.sys.sunset)

        mTvSpeed.text=weatherList.wind.speed.toString()

        mTvName.text=weatherList.name
        mTvCountry.text=weatherList.sys.country

        mIvMain.setImageResource(
            when(weather.icon){
                "01d"->R.drawable.sunny
                "02d", "03d", "04d", "04n", "01n", "02n","03n", "10n"->R.drawable.cloud
                "10d", "11n"->R.drawable.rain
                "11d"->R.drawable.storm
                "13d","13n"->R.drawable.snowflake
                else ->R.drawable.sunny

            }
        )


    }

    //This function takes a String parameter value representing a location code. It returns a String representing the unit of temperature, either "℉" (Fahrenheit) or "℃" (Celsius), based on the location code.
    private fun getUnit(value:String):String{
        var result:String?=null
        result=when(value) {
            //US: United States , LR: Liberia , MM: Myanmar
            "US", "LR", "MM" -> "℉"
            else -> "℃"
        }
        return result
    }
    private fun formatTime(time:Long):String{
        // the time is in second , to convert it in millisecond we have multiple it by 1000
        val date= Date(time*1000L)
        val format=SimpleDateFormat("HH:mm", Locale("en", "IN"))
        format.timeZone= TimeZone.getDefault()
        return format.format(date)
    }

}

