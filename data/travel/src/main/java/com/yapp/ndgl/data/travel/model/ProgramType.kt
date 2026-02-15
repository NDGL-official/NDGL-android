package com.yapp.ndgl.data.travel.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class ProgramType {
    @SerialName("YOUTUBE")
    YOUTUBE,

    @SerialName("TV")
    TV,
}
