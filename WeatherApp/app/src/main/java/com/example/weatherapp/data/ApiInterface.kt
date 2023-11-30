package com.example.weatherapp.data

import com.example.weatherapp.data.models.CurrentLocationWeather
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {

    @GET("weather?")
    suspend fun getCurrentLocationWeather(
       @Query("q") city:String,
       @Query("units") unit:String,
       @Query("appid") apiKey:String,

    ):Response<CurrentLocationWeather>
}