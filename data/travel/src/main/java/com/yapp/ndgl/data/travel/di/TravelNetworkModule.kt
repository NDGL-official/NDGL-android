package com.yapp.ndgl.data.travel.di

import com.yapp.ndgl.data.travel.api.TravelProgramApi
import com.yapp.ndgl.data.travel.api.TravelTemplateApi
import com.yapp.ndgl.data.travel.api.UserTravelApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TravelNetworkModule {
    @Provides
    @Singleton
    fun provideTravelProgramApi(
        retrofit: Retrofit,
    ): TravelProgramApi = retrofit.create(TravelProgramApi::class.java)

    @Provides
    @Singleton
    fun provideTravelTemplateApi(
        retrofit: Retrofit,
    ): TravelTemplateApi = retrofit.create(TravelTemplateApi::class.java)

    @Provides
    @Singleton
    fun provideUserTravelApi(
        retrofit: Retrofit,
    ): UserTravelApi = retrofit.create(UserTravelApi::class.java)
}
