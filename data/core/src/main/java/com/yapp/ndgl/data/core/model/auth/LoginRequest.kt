package com.yapp.ndgl.data.core.model.auth

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val uuid: String,
)
