package com.yapp.ndgl.data.core.di

import com.yapp.ndgl.data.core.BuildConfig
import com.yapp.ndgl.data.core.authenticator.NDGLAuthenticator
import com.yapp.ndgl.data.core.interceptor.NDGLInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private val prettyJson = Json { prettyPrint = true }

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
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor { message ->
            if (message.startsWith("{").not() && message.startsWith("[").not()) {
                Timber.tag("OkHttp").d(message)
                return@HttpLoggingInterceptor
            }

            try {
                val element = prettyJson.decodeFromString<JsonElement>(message)
                Timber.tag("OkHttp").d(prettyJson.encodeToString(JsonElement.serializer(), element))
            } catch (_: Exception) {
                Timber.tag("OkHttp").d(message)
            }
        }.apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

    @Singleton
    @Provides
    fun provideDefaultOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor,
        interceptor: NDGLInterceptor,
        authenticator: NDGLAuthenticator,
    ): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor(interceptor)
            .authenticator(authenticator)
        return builder.build()
    }

    @AuthClient
    @Singleton
    @Provides
    fun provideAuthOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor,
    ): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
        return builder.build()
    }
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthClient
