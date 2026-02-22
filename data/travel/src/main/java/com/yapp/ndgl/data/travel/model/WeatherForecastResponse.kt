@file:OptIn(kotlinx.serialization.InternalSerializationApi::class)

package com.yapp.ndgl.data.travel.model

import kotlinx.serialization.Serializable

@Serializable
data class WeatherForecastResponse(
    val forecastDays: List<ForecastDay> = emptyList(),
) {
    @Serializable
    data class ForecastDay(
        val displayDate: DisplayDate,
        val daytimeForecast: DaytimeForecast? = null,
        val maxTemperature: Temperature? = null,
        val minTemperature: Temperature? = null,
    )

    @Serializable
    data class DisplayDate(val year: Int, val month: Int, val day: Int)

    @Serializable
    data class DaytimeForecast(val weatherCondition: WeatherCondition? = null)

    @Serializable
    data class WeatherCondition(
        val type: String? = null,
        val iconBaseUri: String? = null,
    )

    @Serializable
    data class Temperature(val degrees: Double, val unit: String = "CELSIUS")
}
