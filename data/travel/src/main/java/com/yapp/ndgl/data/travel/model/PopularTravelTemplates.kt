package com.yapp.ndgl.data.travel.model

import kotlinx.serialization.Serializable

@Serializable
data class PopularTravelTemplates(
    val content: List<TravelTemplateSummary>,
    val hasNext: Boolean,
)
