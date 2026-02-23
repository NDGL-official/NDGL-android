package com.yapp.ndgl.data.core.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class RouteInterceptor @Inject constructor(
    private val apiKey: String,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("Content-Type", "application/json")
            .addHeader("X-Goog-Api-Key", apiKey)
            .addHeader("X-Goog-FieldMask", "routes.duration,routes.distanceMeters")
            .build()
        return chain.proceed(request)
    }
}
