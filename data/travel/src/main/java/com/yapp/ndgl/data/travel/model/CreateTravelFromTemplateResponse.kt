package com.yapp.ndgl.data.travel.model

import kotlinx.serialization.Serializable

@Serializable
data class CreateTravelFromTemplateResponse(
    val userTravelId: Long,
)
