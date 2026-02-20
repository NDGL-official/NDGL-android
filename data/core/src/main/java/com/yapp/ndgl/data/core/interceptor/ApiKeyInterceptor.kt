package com.yapp.ndgl.data.core.interceptor

import com.yapp.ndgl.data.core.di.ApiKey
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class ApiKeyInterceptor @Inject constructor(
    @ApiKey private val apiKey: String,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("X-API-KEY", apiKey)
            .build()
        return chain.proceed(request)
    }
}
