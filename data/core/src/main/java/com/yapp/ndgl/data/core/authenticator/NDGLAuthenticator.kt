package com.yapp.ndgl.data.core.authenticator

import com.yapp.ndgl.data.core.api.NDGLApi
import com.yapp.ndgl.data.core.local.datasource.LocalAuthDataSource
import com.yapp.ndgl.data.core.model.auth.LoginRequest
import com.yapp.ndgl.data.core.model.getData
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Provider

class NDGLAuthenticator @Inject constructor(
    private val localAuthDataSource: LocalAuthDataSource,
    private val ndglApi: Provider<NDGLApi>,
) : Authenticator {
    private val mutex = Mutex()

    override fun authenticate(route: Route?, response: Response): Request? {
        val originRequest = response.request

        if (originRequest.header("Authorization").isNullOrEmpty()) {
            return null
        }

        if (originRequest.url.encodedPath.contains("/api/v1/auth/login")) {
            runBlocking {
                localAuthDataSource.clearSession()
            }

            return null
        }

        val retryCount = originRequest.header(RETRY_HEADER)?.toIntOrNull() ?: 0
        if (retryCount >= MAX_RETRY_COUNT) {
            return null
        }

        val uuid = runBlocking { localAuthDataSource.getUuid() }

        val authResponse = runBlocking {
            mutex.withLock {
                ndglApi.get().login(LoginRequest(uuid))
            }
        }.getData()

        runBlocking {
            localAuthDataSource.apply {
                setAccessToken(authResponse.accessToken)
                setUuid(authResponse.uuid)
            }
        }

        val newRequest = originRequest.newBuilder()
            .header(RETRY_HEADER, (retryCount + 1).toString())
            .header("Authorization", "Bearer ${authResponse.accessToken}")
            .build()

        return newRequest
    }

    companion object {
        private const val MAX_RETRY_COUNT = 3
        private const val RETRY_HEADER = "Retry-Count"
    }
}
