package com.example.weatherapp.Database

import androidx.room.TypeConverter
import com.example.weatherapp.Models.Clouds
import com.example.weatherapp.Models.Coord
import com.example.weatherapp.Models.Main
import com.example.weatherapp.Models.Sys
import com.example.weatherapp.Models.Weather
import com.example.weatherapp.Models.Wind
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    @TypeConverter
    fun fromClouds(clouds: Clouds): String {
        return Gson().toJson(clouds)
    }

    @TypeConverter
    fun toClouds(cloudsString: String): Clouds {
        return Gson().fromJson(cloudsString, Clouds::class.java)
    }

    @TypeConverter
    fun fromCoord(coord:Coord): String {
        return Gson().toJson(coord)
    }

    @TypeConverter
    fun toCoord(coordString: String): Coord {
        return Gson().fromJson(coordString, Coord::class.java)
    }

    @TypeConverter
    fun fromMain(main: Main): String {
        return Gson().toJson(main)
    }

    @TypeConverter
    fun toMain(mainString: String): Main {
        return Gson().fromJson(mainString, Main::class.java)
    }

    @TypeConverter
    fun fromSys(sys: Sys): String {
        return Gson().toJson(sys)
    }

    @TypeConverter
    fun toSys(sysString: String): Sys {
        return Gson().fromJson(sysString, Sys::class.java)
    }

    @TypeConverter
    fun fromWeather(weatherList: List<Weather>): String {
        return Gson().toJson(weatherList)
    }

    @TypeConverter
    fun toWeather(weatherString: String): List<Weather> {
        val listType = object : TypeToken<List<Weather>>() {}.type
        return Gson().fromJson(weatherString, listType)
    }

    @TypeConverter
    fun fromWind(wind: Wind): String {
        return Gson().toJson(wind)
    }

    @TypeConverter
    fun toWind(windString: String): Wind {
        return Gson().fromJson(windString, Wind::class.java)
    }

}