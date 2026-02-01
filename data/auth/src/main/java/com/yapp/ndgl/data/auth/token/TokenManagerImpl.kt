package com.yapp.ndgl.data.auth.token

import com.yapp.ndgl.data.auth.api.AuthApi
import com.yapp.ndgl.data.auth.local.LocalAuthDataSource
import com.yapp.ndgl.data.auth.model.LoginRequest
import com.yapp.ndgl.data.core.model.getData
import com.yapp.ndgl.data.core.token.TokenManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManagerImpl @Inject constructor(
    private val localAuthDataSource: LocalAuthDataSource,
    private val authApi: AuthApi,
) : TokenManager {

    override suspend fun getAccessToken(): String {
        return localAuthDataSource.getAccessToken()
    }

    override suspend fun getUuid(): String {
        return localAuthDataSource.getUuid()
    }

    override suspend fun setAccessToken(accessToken: String) {
        localAuthDataSource.setAccessToken(accessToken)
    }

    override suspend fun setUuid(uuid: String) {
        localAuthDataSource.setUuid(uuid)
    }

    override suspend fun refreshToken(): String {
        val uuid = getUuid()
        check(uuid.isNotEmpty()) { "UUID is empty" }

        val response = authApi.login(LoginRequest(uuid)).getData()
        setAccessToken(response.accessToken)
        setUuid(response.uuid)

        return response.accessToken
    }
}
