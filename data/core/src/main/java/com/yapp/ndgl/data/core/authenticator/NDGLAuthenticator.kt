package com.yapp.ndgl.data.core.authenticator

import com.yapp.ndgl.data.core.token.TokenManager
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import timber.log.Timber
import javax.inject.Inject

class NDGLAuthenticator @Inject constructor(
    private val tokenManager: TokenManager,
) : Authenticator {
    private val mutex = Mutex()

    override fun authenticate(route: Route?, response: Response): Request? {
        val originRequest = response.request

        if (originRequest.header("Authorization").isNullOrEmpty()) {
            return null
        }

        val retryCount = originRequest.header(RETRY_HEADER)?.toIntOrNull() ?: 0
        if (retryCount >= MAX_RETRY_COUNT) {
            return null
        }

        val newAccessToken = runBlocking {
            mutex.withLock {
                try {
                    tokenManager.refreshToken()
                } catch (e: Exception) {
                    Timber.e(e, "Failed to refresh token")
                    null
                }
            }
        } ?: return null

        val newRequest = originRequest.newBuilder()
            .header(RETRY_HEADER, (retryCount + 1).toString())
            .header("Authorization", "Bearer $newAccessToken")
            .build()

        return newRequest
    }

    companion object {
        private const val MAX_RETRY_COUNT = 3
        private const val RETRY_HEADER = "Retry-Count"
    }
}
