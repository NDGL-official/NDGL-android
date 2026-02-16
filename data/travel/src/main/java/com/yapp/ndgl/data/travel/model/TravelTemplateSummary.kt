package com.yapp.ndgl.data.travel.model

import kotlinx.serialization.Serializable

@Serializable
data class TravelTemplateSummary(
    val id: Long,
    val title: String,
    val country: String,
    val city: String,
    val nights: Int,
    val days: Int,
    val programName: String,
    val programType: ProgramType,
    val traveler: String? = null,
    val thumbnail: String? = null,
)
