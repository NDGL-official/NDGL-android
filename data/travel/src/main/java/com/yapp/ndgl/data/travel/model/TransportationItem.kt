package com.yapp.ndgl.data.travel.model

import kotlinx.serialization.Serializable

@Serializable
data class TransportationItem(
    val mode: TransportCategory,
    val timeMin: Int,
)
