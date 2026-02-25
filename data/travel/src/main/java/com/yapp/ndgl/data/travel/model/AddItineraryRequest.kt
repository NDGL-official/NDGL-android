package com.yapp.ndgl.data.travel.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AddItineraryRequest(
    val googlePlaceId: String,
    val day: Int,
    val sequence: Int,
    val startTime: String? = null,
    val estimatedDuration: Int,
    val memo: String? = null,
    @SerialName("budget")
    val cost: Int? = null,
    val distanceKm: Double? = null,
    val transportation: List<TransportationItem>? = null,
)
