package com.yapp.ndgl.data.travel.di

import android.content.Context
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.yapp.ndgl.data.core.di.RouteApiKey
import com.yapp.ndgl.data.core.di.RouteBaseUrl
import com.yapp.ndgl.data.core.di.RouteClient
import com.yapp.ndgl.data.travel.BuildConfig
import com.yapp.ndgl.data.travel.api.PlaceApi
import com.yapp.ndgl.data.travel.api.RouteApi
import com.yapp.ndgl.data.travel.api.TravelProgramApi
import com.yapp.ndgl.data.travel.api.TravelTemplateApi
import com.yapp.ndgl.data.travel.api.UserTravelApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TravelNetworkModule {
    private const val ROUTES_BASE_URL = "https://routes.googleapis.com/"

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

    @RouteApiKey
    @Provides
    @Singleton
    fun provideRouteApiKey(): String = BuildConfig.ROUTE_API_KEY

    @RouteBaseUrl
    @Provides
    @Singleton
    fun provideRouteBaseUrl(): String = ROUTES_BASE_URL

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

    @RouteClient
    @Provides
    @Singleton
    fun provideRouteRetrofit(
        @RouteClient okHttpClient: OkHttpClient,
        @RouteBaseUrl baseUrl: String,
        json: Json,
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    @Provides
    @Singleton
    fun provideRouteApi(
        @RouteClient retrofit: Retrofit,
    ): RouteApi = retrofit.create(RouteApi::class.java)
}
