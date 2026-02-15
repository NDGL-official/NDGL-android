package com.yapp.ndgl.data.travel.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.yapp.ndgl.data.core.adapter.NDGLCallAdapterFactory
import com.yapp.ndgl.data.travel.api.UserTravelApi
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
object TravelNetworkModule {
    @Provides
    @Singleton
    fun provideUserTravelApi(
        json: Json,
        baseUrl: String,
        okHttpClient: OkHttpClient,
        callAdapterFactory: NDGLCallAdapterFactory,
    ): UserTravelApi = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .addCallAdapterFactory(callAdapterFactory)
        .build()
        .create(UserTravelApi::class.java)
}
