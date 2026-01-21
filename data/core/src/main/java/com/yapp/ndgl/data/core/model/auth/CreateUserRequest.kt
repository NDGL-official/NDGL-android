package com.yapp.ndgl.data.core.model.auth

import kotlinx.serialization.Serializable

@Serializable
data class CreateUserRequest(
    val fcmToken: String,
    val deviceModel: String,
    val deviceOs: String,
    val deviceOsVersion: String,
    val appVersion: String,
)
