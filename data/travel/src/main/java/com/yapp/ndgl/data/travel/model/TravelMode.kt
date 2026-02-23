package com.yapp.ndgl.data.travel.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class TravelMode {
    @SerialName("DRIVE")
    DRIVE,

    @SerialName("TRANSIT")
    TRANSIT,

    @SerialName("WALK")
    WALK,

    @SerialName("BICYCLE")
    BICYCLE,

    @SerialName("TWO_WHEELER")
    TWO_WHEELER,
}
