package com.yapp.ndgl.data.travel.model

import kotlinx.serialization.Serializable

@Serializable
data class TravelTemplateItinerary(
    val itineraries: List<ItineraryItem>,
) {
    @Serializable
    data class ItineraryItem(
        val id: Long,
        val day: Int,
        val sequence: Int,
        val distanceKm: Double? = null,
        val transportation: List<Transportation>? = null,
        val travelerTips: List<String>? = null,
        val planB: List<PlanBPlace>? = null,
        val estimatedDuration: Int,
        val place: ItineraryPlace,
    )

    @Serializable
    data class Transportation(
        val mode: TransportCategory,
        val timeMin: Int,
    )

    @Serializable
    data class PlanBPlace(
        val name: String,
        val feature: String? = null,
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
