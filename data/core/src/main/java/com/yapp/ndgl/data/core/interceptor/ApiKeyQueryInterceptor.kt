package com.yapp.ndgl.data.core.interceptor

import okhttp3.Interceptor
import okhttp3.Response

class ApiKeyQueryInterceptor(private val apiKey: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val newUrl = chain.request().url.newBuilder()
            .addQueryParameter("key", apiKey)
            .build()
        return chain.proceed(chain.request().newBuilder().url(newUrl).build())
    }
}
