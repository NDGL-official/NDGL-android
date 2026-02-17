package com.yapp.ndgl.data.travel.model

import kotlinx.serialization.Serializable

@Serializable
data class GetPlacePhotosResponse(
    val photos: List<PlacePhoto>,
)

@Serializable
data class PlacePhoto(
    val photoUri: String,
    val widthPx: Int,
    val heightPx: Int,
)
