package com.yapp.ndgl.data.travel.di

import android.content.Context
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.yapp.ndgl.data.travel.BuildConfig
import com.yapp.ndgl.data.travel.api.PlaceApi
import com.yapp.ndgl.data.travel.api.TravelProgramApi
import com.yapp.ndgl.data.travel.api.TravelTemplateApi
import com.yapp.ndgl.data.travel.api.UserTravelApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TravelNetworkModule {
    @Provides
    @Singleton
    fun providePlacesClient(
        @ApplicationContext context: Context,
    ): PlacesClient {
        if (!Places.isInitialized()) {
            Places.initializeWithNewPlacesApiEnabled(context, BuildConfig.PLACE_API_KEY)
        }
        return Places.createClient(context)
    }

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

    @Provides
    @Singleton
    fun providePlaceApi(
        retrofit: Retrofit,
    ): PlaceApi = retrofit.create(PlaceApi::class.java)
}
