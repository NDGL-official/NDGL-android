package com.yapp.ndgl.data.travel.repository

import com.yapp.ndgl.data.travel.api.GeocodingApi
import com.yapp.ndgl.data.travel.api.WeatherApi
import com.yapp.ndgl.data.travel.model.WeatherForecastResponse
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepository @Inject constructor(
    private val geocodingApi: GeocodingApi,
    private val weatherApi: WeatherApi,
) {
    suspend fun getWeatherForecast(
        city: String,
        country: String,
        startDate: LocalDate,
        endDate: LocalDate,
    ): WeatherForecastResponse? {
        val address = "$city,$country"
        val location = geocodingApi.geocode(address).results.firstOrNull()?.geometry?.location
            ?: return null

        val today = LocalDate.now()
        val days = maxOf(ChronoUnit.DAYS.between(today, endDate).toInt() + 2, 1)

        val response = weatherApi.getDailyForecast(location.lat, location.lng, days)

        val filterStart = if (today.isAfter(startDate)) today else startDate
        val filtered = response.forecastDays.filter { day ->
            val date = LocalDate.of(day.displayDate.year, day.displayDate.month, day.displayDate.day)
            !date.isBefore(filterStart) && !date.isAfter(endDate)
        }
        return response.copy(forecastDays = filtered)
    }
}
