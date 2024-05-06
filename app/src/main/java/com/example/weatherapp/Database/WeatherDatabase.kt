package com.example.weatherapp.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.weatherapp.Models.WeatherResponse


@Database(entities = [WeatherResponse::class], version = 1)
@TypeConverters(Converters::class)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun weatherDao(): WeatherDao
    companion object{
        @Volatile
        private var INSTANCE:WeatherDatabase?=null
        fun getInstance(context: Context):WeatherDatabase{
            synchronized(this){
                if(INSTANCE==null){
                    INSTANCE= Room.databaseBuilder(
                        context.applicationContext,
                        WeatherDatabase::class.java,
                        "WeatherDB"
                        ).build()
                }
                return INSTANCE!!
            }
        }
    }
}
