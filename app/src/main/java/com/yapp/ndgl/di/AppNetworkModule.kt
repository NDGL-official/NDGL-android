package com.yapp.ndgl.di

import com.yapp.ndgl.BuildConfig
import com.yapp.ndgl.data.core.di.ApiKey
import com.yapp.ndgl.data.core.di.BaseUrl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppNetworkModule {

    @BaseUrl
    @Singleton
    @Provides
    fun provideBaseUrl(): String = BuildConfig.NDGL_BASE_URL

    @ApiKey
    @Singleton
    @Provides
    fun provideApiKey(): String = BuildConfig.NDGL_API_KEY
}
