package com.yapp.ndgl.data.travel.api

import com.yapp.ndgl.data.travel.model.WeatherForecastResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("v1/forecast/days:lookup")
    suspend fun getDailyForecast(
        @Query("location.latitude") latitude: Double,
        @Query("location.longitude") longitude: Double,
        @Query("days") days: Int,
    ): WeatherForecastResponse
}
