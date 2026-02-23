package com.yapp.ndgl.data.travel.model

import kotlinx.serialization.Serializable

@Serializable
data class BulkUpdateStartTimeRequest(
    val updates: List<StartTimeUpdateItem>,
)

@Serializable
data class StartTimeUpdateItem(
    val id: Long,
    val startTime: String,
)
