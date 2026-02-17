package com.yapp.ndgl.navigation.model

import kotlinx.serialization.Serializable

@Serializable
data class RouteTipContent(
    val creatorName: String,
    val tips: List<String>,
)
