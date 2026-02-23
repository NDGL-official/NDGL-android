package com.yapp.ndgl.data.travel.model

import kotlinx.serialization.Serializable

@Serializable
data class ComputeRoutesResponse(
    val routes: List<RouteInfo> = emptyList(),
)

@Serializable
data class RouteInfo(
    val distanceMeters: Int = 0,
    val duration: String = "0s",
)
