package com.example.weatherapp.Database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherapp.Models.WeatherResponse

@Dao
interface WeatherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(weatherResponse: WeatherResponse)

    @Query("SELECT * FROM WeatherResponse")
    suspend fun getAll():WeatherResponse

    @Delete
    suspend fun deleteWeather(weatherResponse: WeatherResponse)
}