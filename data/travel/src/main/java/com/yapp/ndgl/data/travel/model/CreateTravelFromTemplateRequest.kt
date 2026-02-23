package com.yapp.ndgl.data.travel.model

import kotlinx.serialization.Serializable

@Serializable
data class CreateTravelFromTemplateRequest(
    val templateId: Long,
    val startDate: String,
    val endDate: String,
)
