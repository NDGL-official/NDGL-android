package com.yapp.ndgl.data.travel.model

import kotlinx.serialization.Serializable

@Serializable
data class UserTravelPlace(
    val id: Long,
    val estimatedDuration: Int,
    val place: Place?,
) {
    @Serializable
    data class Place(
        val googlePlaceId: String,
        val thumbnail: String? = null,
        val latitude: Double,
        val longitude: Double,
        val name: String,
        val regularOpeningHours: String? = null,
        val googleMapsUri: String,
        val category: PlaceCategory,
    )
}
