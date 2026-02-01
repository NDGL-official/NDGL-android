package com.yapp.ndgl.data.core.interceptor

import com.yapp.ndgl.data.core.token.TokenManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject

class NDGLInterceptor @Inject constructor(
    private val tokenManager: TokenManager,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originRequest = chain.request()
        val requestBuilder = originRequest.newBuilder()

        if (isAccessTokenUsed(originRequest)) {
            requestBuilder.addHeader(
                "Authorization",
                "Bearer ${runBlocking { tokenManager.getAccessToken() }}",
            )
        }

        return chain.proceed(requestBuilder.build())
    }

    private fun isAccessTokenUsed(request: Request): Boolean {
        return when (request.url.encodedPath) {
            "/api/v1/auth/users" -> false
            "/api/v1/auth/login" -> false
            else -> true
        }
    }
}
