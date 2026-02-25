package com.yapp.ndgl.data.travel.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateTravelPlaceRequest(
    val memo: String? = null,
    @SerialName("budget")
    val cost: Int? = null,
)
