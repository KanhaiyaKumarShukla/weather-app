package com.example.weatherapp

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

object Constants {
    const val APP_ID:String="f661c5ec7673b162ef2051aacfc8c73f"
    const val BASE_URL:String="https://api.openweathermap.org/data/"
    const val METRIC_UNIT:String="metric"

    fun isInternetAvailable(context: Context): Boolean {

        val connectivityManager=context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork
            if (network != null) {
                val capabilities = connectivityManager.getNetworkCapabilities(network)
                if (capabilities != null) {
                    return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                }
            }
        }else{
            val networkInfo=connectivityManager.activeNetworkInfo
            return (networkInfo!=null && networkInfo.isConnectedOrConnecting)
        }
        return false
    }
}