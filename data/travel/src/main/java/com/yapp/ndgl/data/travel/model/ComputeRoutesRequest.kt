package com.yapp.ndgl.data.travel.model

import kotlinx.serialization.Serializable

@Serializable
data class ComputeRoutesRequest(
    val origin: RouteLocation,
    val destination: RouteLocation,
    val travelMode: TravelMode,
)

@Serializable
data class RouteLocation(
    val location: Location,
)

@Serializable
data class Location(
    val latLng: LatLng,
)

@Serializable
data class LatLng(
    val latitude: Double,
    val longitude: Double,
)
