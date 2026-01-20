package com.yapp.ndgl.data.core.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.yapp.ndgl.data.core.BuildConfig
import com.yapp.ndgl.data.core.adapter.NDGLCallAdapterFactory
import com.yapp.ndgl.data.core.api.NDGLApi
import com.yapp.ndgl.data.core.authenticator.NDGLAuthenticator
import com.yapp.ndgl.data.core.interceptor.NDGLInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Singleton
    @Provides
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
    }

    @Singleton
    @Provides
    fun provideDefaultOkHttpClient(
        interceptor: NDGLInterceptor,
        authenticator: NDGLAuthenticator,
    ): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .authenticator(authenticator)

        if (BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            builder.addInterceptor(loggingInterceptor)
        }

        return builder.build()
    }

    @Singleton
    @Provides
    fun provideNDGLApi(
        json: Json,
        okHttpClient: OkHttpClient,
        callAdapterFactory: NDGLCallAdapterFactory,
    ): NDGLApi = Retrofit.Builder()
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .addCallAdapterFactory(callAdapterFactory)
        .baseUrl(BuildConfig.NDGL_BASE_URL)
        .build()
        .create(NDGLApi::class.java)
}
