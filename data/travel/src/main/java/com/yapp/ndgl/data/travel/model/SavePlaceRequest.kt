package com.yapp.ndgl.data.travel.model

import kotlinx.serialization.Serializable

@Serializable
data class SavePlaceRequest(
    val googlePlaceId: String,
)
