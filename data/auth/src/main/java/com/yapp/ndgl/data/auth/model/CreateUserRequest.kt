package com.yapp.ndgl.data.auth.model

import kotlinx.serialization.Serializable

@Serializable
data class CreateUserRequest(
    val fcmToken: String,
    val deviceModel: String,
    val deviceOs: String,
    val deviceOsVersion: String,
    val appVersion: String,
)
