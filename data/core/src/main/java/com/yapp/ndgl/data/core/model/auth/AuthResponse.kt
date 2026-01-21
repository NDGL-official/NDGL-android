package com.yapp.ndgl.data.core.model.auth

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val uuid: String,
    val accessToken: String,
    val nickname: String,
)
