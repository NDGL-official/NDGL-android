package com.yapp.ndgl.data.core.di

import com.yapp.ndgl.data.core.BuildConfig
import com.yapp.ndgl.data.core.authenticator.NDGLAuthenticator
import com.yapp.ndgl.data.core.interceptor.NDGLInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Qualifier
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
    fun provideBaseUrl(): String = BuildConfig.NDGL_BASE_URL

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

    @AuthClient
    @Singleton
    @Provides
    fun provideAuthOkHttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()

        if (BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            builder.addInterceptor(loggingInterceptor)
        }

        return builder.build()
    }
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthClient
