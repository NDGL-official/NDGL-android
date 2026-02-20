package com.yapp.ndgl.data.core.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.yapp.ndgl.data.core.BuildConfig
import com.yapp.ndgl.data.core.adapter.NDGLCallAdapterFactory
import com.yapp.ndgl.data.core.authenticator.NDGLAuthenticator
import com.yapp.ndgl.data.core.interceptor.ApiKeyInterceptor
import com.yapp.ndgl.data.core.interceptor.NDGLInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
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
        apiKeyInterceptor: ApiKeyInterceptor,
        interceptor: NDGLInterceptor,
        authenticator: NDGLAuthenticator,
        httpLoggingInterceptor: HttpLoggingInterceptor,
    ): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .addInterceptor(apiKeyInterceptor)
            .addInterceptor(interceptor)
            .authenticator(authenticator)
            .addInterceptor(httpLoggingInterceptor)
        return builder.build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(
        json: Json,
        @BaseUrl baseUrl: String,
        okHttpClient: OkHttpClient,
        callAdapterFactory: NDGLCallAdapterFactory,
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .addCallAdapterFactory(callAdapterFactory)
            .build()
    }

    @AuthClient
    @Singleton
    @Provides
    fun provideAuthOkHttpClient(
        apiKeyInterceptor: ApiKeyInterceptor,
        httpLoggingInterceptor: HttpLoggingInterceptor,
    ): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .addInterceptor(apiKeyInterceptor)
            .addInterceptor(httpLoggingInterceptor)

        return builder.build()
    }
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BaseUrl

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ApiKey
