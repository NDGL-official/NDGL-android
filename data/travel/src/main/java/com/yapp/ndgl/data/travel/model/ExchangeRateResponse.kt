package com.yapp.ndgl.data.travel.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ExchangeRateResponse(
    @SerialName("result") val result: String,
    @SerialName("base_code") val baseCode: String,
    @SerialName("time_last_update_utc") val timeLastUpdateUtc: String,
    @SerialName("conversion_rates") val conversionRates: Map<String, Double>,
)
