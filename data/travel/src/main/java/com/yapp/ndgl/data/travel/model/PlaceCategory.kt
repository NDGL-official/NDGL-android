package com.yapp.ndgl.data.travel.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class PlaceCategory {
    @SerialName("AIRPORT")
    AIRPORT,

    @SerialName("TRANSPORT")
    TRANSPORT,

    @SerialName("ATTRACTION")
    ATTRACTION,

    @SerialName("RESTAURANT")
    RESTAURANT,

    @SerialName("CAFE")
    CAFE,

    @SerialName("ACCOMMODATION")
    ACCOMMODATION,
}
