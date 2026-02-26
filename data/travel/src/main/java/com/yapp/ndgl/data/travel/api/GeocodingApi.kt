package com.yapp.ndgl.data.travel.api

import com.yapp.ndgl.data.travel.model.GeocodingResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface GeocodingApi {
    @GET("maps/api/geocode/json")
    suspend fun geocode(
        @Query("address") address: String,
    ): GeocodingResponse
}
