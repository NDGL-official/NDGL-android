package com.yapp.ndgl.data.travel.model

import kotlinx.serialization.Serializable

@Serializable
data class GeocodingResponse(
    val results: List<Result> = emptyList(),
    val status: String = "",
) {
    @Serializable
    data class Result(val geometry: Geometry)

    @Serializable
    data class Geometry(val location: Location)

    @Serializable
    data class Location(val lat: Double, val lng: Double)
}
