package com.yapp.ndgl.data.travel.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetBookmarkedPlacesResponse(
    @SerialName("content")
    val places: List<PlaceInfo>,
    val hasNext: Boolean,
)

@Serializable
data class PlaceInfo(
    val id: Long,
    val googlePlaceId: String,
    val name: String,
    val formattedAddress: String?,
    val latitude: Double,
    val longitude: Double,
    val thumbnail: String?,
    val rating: Double?,
    val userRatingCount: Int?,
    val category: PlaceCategory,
)
