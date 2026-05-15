package com.yapp.ndgl.data.travel.model

import kotlinx.serialization.Serializable

@Serializable
data class SubscribeTemplateRequest(
    val videoLink: String,
)
