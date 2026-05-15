package com.yapp.ndgl.data.travel.model

import kotlinx.serialization.Serializable

@Serializable
data class SuggestTemplateRequest(
    val videoLink: String,
    val recommendReason: String,
    val category: List<String>,
)
