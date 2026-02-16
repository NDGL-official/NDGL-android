package com.yapp.ndgl.data.travel.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class TransportCategory {
    @SerialName("DRIVING")
    DRIVING,

    @SerialName("TRANSIT")
    TRANSIT,

    @SerialName("WALKING")
    WALKING,

    @SerialName("BICYCLING")
    BICYCLING,

    @SerialName("TAXI")
    TAXI,

    @SerialName("TWO_WHEELER")
    TWO_WHEELER,

    @SerialName("FERRY")
    FERRY,

    @SerialName("FLIGHT")
    FLIGHT,
}
