package com.yapp.ndgl.data.auth.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.yapp.ndgl.data.auth.api.AuthApi
import com.yapp.ndgl.data.core.adapter.NDGLCallAdapterFactory
import com.yapp.ndgl.data.core.di.AuthClient
import com.yapp.ndgl.data.core.di.BaseUrl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthNetworkModule {
    @Provides
    @Singleton
    fun provideAuthApi(
        json: Json,
        @BaseUrl baseUrl: String,
        @AuthClient okHttpClient: OkHttpClient,
        callAdapterFactory: NDGLCallAdapterFactory,
    ): AuthApi = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .addCallAdapterFactory(callAdapterFactory)
        .build()
        .create(AuthApi::class.java)
}
