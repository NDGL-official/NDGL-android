package com.yapp.ndgl.data.core.token

interface TokenManager {
    suspend fun getAccessToken(): String
    suspend fun getUuid(): String
    suspend fun setAccessToken(accessToken: String)
    suspend fun setUuid(uuid: String)
    suspend fun refreshToken(): String
}
