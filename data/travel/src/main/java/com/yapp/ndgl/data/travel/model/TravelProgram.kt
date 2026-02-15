package com.yapp.ndgl.data.travel.model

import kotlinx.serialization.Serializable

@Serializable
data class TravelProgram(
    val id: Long,
    val name: String,
    val profileImage: String? = null,
    val type: ProgramType,
)
