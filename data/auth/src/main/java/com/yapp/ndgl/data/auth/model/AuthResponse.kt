package com.yapp.ndgl.data.auth.model

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val uuid: String,
    val accessToken: String,
    val nickname: String,
)
