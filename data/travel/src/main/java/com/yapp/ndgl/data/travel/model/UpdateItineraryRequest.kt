package com.yapp.ndgl.data.travel.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateItineraryRequest(
    val itineraries: List<ItineraryUpdateItem>,
)

@Serializable
data class ItineraryUpdateItem(
    val placeId: Long,
    val day: Int,
    val sequence: Int,
    val startTime: String,
    val estimatedDuration: Int,
    @SerialName("travelerTip")
    val memo: String?,
    val distanceKm: Double? = null,
    val transportation: List<TransportationItem>? = null,
)

@Serializable
data class TransportationItem(
    val mode: TransportCategory,
    val timeMin: Int,
)
