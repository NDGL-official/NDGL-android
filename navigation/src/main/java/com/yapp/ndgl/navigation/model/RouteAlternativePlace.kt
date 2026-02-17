package com.yapp.ndgl.navigation.model

import kotlinx.serialization.Serializable

@Serializable
data class RouteAlternativePlace(
    val id: String,
    val name: String,
    val thumbnail: String,
    val placeType: String,
)
