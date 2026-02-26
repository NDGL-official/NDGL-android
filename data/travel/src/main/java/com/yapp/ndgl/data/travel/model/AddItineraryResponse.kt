package com.yapp.ndgl.data.travel.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AddItineraryResponse(
    val id: Long,
    val day: Int,
    val sequence: Int,
    val startTime: String? = null,
    val estimatedDuration: Int,
    val memo: String? = null,
    @SerialName("budget")
    val cost: Int? = null,
    val distanceKm: Double? = null,
    val transportation: List<Transportation>? = null,
    val travelerTips: List<String>? = null,
    val planB: List<PlanBPlace>? = null,
    val place: ItineraryPlace,
) {
    @Serializable
    data class Transportation(
        val mode: TransportCategory,
        val timeMin: Int,
    )

    @Serializable
    data class PlanBPlace(
        val googlePlaceId: String,
        val name: String,
        val thumbnail: String? = null,
        val category: PlaceCategory,
    )

    @Serializable
    data class ItineraryPlace(
        val googlePlaceId: String,
        val thumbnail: String? = null,
        val latitude: Double,
        val longitude: Double,
        val name: String,
        val regularOpeningHours: String? = null,
        val googleMapsUri: String? = null,
        val category: PlaceCategory,
        val priceRange: String? = null,
    )
}
